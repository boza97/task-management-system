import argparse
import csv
import subprocess
from datetime import datetime
from pathlib import Path


PERFORMANCE_DIR = Path(__file__).resolve().parent
COMPOSE_FILE = PERFORMANCE_DIR / "docker-compose.yml"
SQL_DIR = PERFORMANCE_DIR / "sql"
RESULTS_DIR = PERFORMANCE_DIR / "results"

CSV_HEADER = [
    "dataset_size",
    "query_name",
    "index_state",
    "run_number",
    "planning_time_ms",
    "execution_time_ms",
    "plan_node",
    "actual_rows",
    "shared_hit_blocks",
    "shared_read_blocks",
]

MEASURE_SQL = "06-measure.sql"


def run_command(command, input_text=None, capture_output=False):
    result = subprocess.run(
        command,
        input=input_text,
        text=True,
        capture_output=capture_output,
        check=False,
    )

    if result.returncode != 0:
        if result.stderr:
            print(result.stderr)
        raise RuntimeError(f"Command failed: {' '.join(command)}")

    return result.stdout if capture_output else ""


def start_database():
    print("Starting PostgreSQL performance database...")
    run_command([
        "docker",
        "compose",
        "-f",
        str(COMPOSE_FILE),
        "up",
        "-d",
        "--wait",
    ])


def run_sql_file(file_name, variables=None, csv_output=False):
    variables = variables or {}
    sql_text = (SQL_DIR / file_name).read_text(encoding="utf-8")

    command = [
        "docker",
        "compose",
        "-f",
        str(COMPOSE_FILE),
        "exec",
        "-T",
        "postgres-performance",
        "psql",
        "-U",
        "postgres",
        "-d",
        "tms_performance",
        "-v",
        "ON_ERROR_STOP=1",
    ]

    for name, value in variables.items():
        command.extend(["-v", f"{name}={value}"])

    if csv_output:
        command.extend(["--csv", "--tuples-only"])

    command.extend(["-f", "-"])
    return run_command(command, input_text=sql_text, capture_output=csv_output)


def prepare_dataset(dataset_size):
    print(f"Preparing dataset: {dataset_size} tasks and audit rows")
    run_sql_file("04-drop-benchmark-indexes.sql")
    run_sql_file("01-reset-data.sql")
    run_sql_file("02-generate-data.sql", {"row_count": dataset_size})
    run_sql_file("03-benchmark-function.sql")


def measure(result_file, dataset_size, index_state, run_number):
    output = run_sql_file(
        MEASURE_SQL,
        {
            "dataset_size": dataset_size,
            "index_state": index_state,
            "run_number": run_number,
        },
        csv_output=True,
    )

    with result_file.open("a", encoding="utf-8", newline="") as file:
        file.write(output)


def run_benchmark(dataset_sizes, runs):
    RESULTS_DIR.mkdir(parents=True, exist_ok=True)
    timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
    result_file = RESULTS_DIR / f"benchmark-results-{timestamp}.csv"

    with result_file.open("w", encoding="utf-8", newline="") as file:
        writer = csv.writer(file)
        writer.writerow(CSV_HEADER)

    start_database()

    for dataset_size in dataset_sizes:
        prepare_dataset(dataset_size)

        print("Measuring queries without indexes...")
        run_sql_file(
            MEASURE_SQL,
            {
                "dataset_size": dataset_size,
                "index_state": "warmup_without_index",
                "run_number": 0,
            },
        )
        for run_number in range(1, runs + 1):
            measure(result_file, dataset_size, "without_index", run_number)

        print("Creating indexes...")
        run_sql_file("05-create-benchmark-indexes.sql")

        print("Measuring queries with indexes...")
        run_sql_file(
            MEASURE_SQL,
            {
                "dataset_size": dataset_size,
                "index_state": "warmup_with_index",
                "run_number": 0,
            },
        )
        for run_number in range(1, runs + 1):
            measure(result_file, dataset_size, "with_index", run_number)

    print(f"Benchmark completed: {result_file}")


def parse_arguments():
    parser = argparse.ArgumentParser(
        description="Run PostgreSQL query performance benchmark for the TMS application."
    )
    parser.add_argument(
        "--dataset-sizes",
        default="10000,100000,1000000",
        help="Comma-separated dataset sizes. Default: 10000,100000,1000000",
    )
    parser.add_argument(
        "--runs",
        type=int,
        default=5,
        help="Number of measured runs per query and index state. Default: 5",
    )
    return parser.parse_args()


if __name__ == "__main__":
    args = parse_arguments()
    sizes = [int(value.strip()) for value in args.dataset_sizes.split(",")]
    run_benchmark(sizes, args.runs)
