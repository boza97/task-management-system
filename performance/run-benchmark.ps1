param(
    [int[]]$DatasetSizes = @(10000, 100000, 1000000),
    [int]$Runs = 5
)

$ErrorActionPreference = "Stop"

$performanceDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$composeFile = Join-Path $performanceDir "docker-compose.yml"
$sqlDir = Join-Path $performanceDir "sql"
$resultsDir = Join-Path $performanceDir "results"
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$resultFile = Join-Path $resultsDir "benchmark-results-$timestamp.csv"

New-Item -ItemType Directory -Force -Path $resultsDir | Out-Null

function Invoke-PsqlFile {
    param(
        [string]$File,
        [string[]]$Variables = @(),
        [switch]$Csv
    )

    $arguments = @(
        "compose", "-f", $composeFile, "exec", "-T",
        "postgres-performance", "psql",
        "-U", "postgres", "-d", "tms_performance",
        "-v", "ON_ERROR_STOP=1"
    )

    foreach ($variable in $Variables) {
        $arguments += @("-v", $variable)
    }

    if ($Csv) {
        $arguments += @("--csv", "--tuples-only")
    }

    $arguments += @("-f", "-")
    Get-Content -Raw $File | & docker @arguments

    if ($LASTEXITCODE -ne 0) {
        throw "PostgreSQL command failed for $File"
    }
}

Write-Host "Starting isolated PostgreSQL container..."
& docker compose -f $composeFile up -d --wait
if ($LASTEXITCODE -ne 0) {
    throw "Could not start the performance database."
}

"dataset_size,query_name,index_state,run_number,planning_time_ms,execution_time_ms,plan_node,actual_rows,shared_hit_blocks,shared_read_blocks" |
    Set-Content -Encoding utf8 $resultFile

foreach ($size in $DatasetSizes) {
    Write-Host "Preparing dataset with $size tasks and $size audit rows..."
    Invoke-PsqlFile -File (Join-Path $sqlDir "04-drop-benchmark-indexes.sql")
    Invoke-PsqlFile -File (Join-Path $sqlDir "01-reset-data.sql")
    Invoke-PsqlFile -File (Join-Path $sqlDir "02-generate-data.sql") `
        -Variables @("row_count=$size")
    Invoke-PsqlFile -File (Join-Path $sqlDir "03-benchmark-function.sql")

    Invoke-PsqlFile -File (Join-Path $sqlDir "06-measure.sql") `
        -Variables @(
            "dataset_size=$size",
            "index_state=warmup_without_index",
            "run_number=0"
        ) | Out-Null

    for ($run = 1; $run -le $Runs; $run++) {
        $output = Invoke-PsqlFile -File (Join-Path $sqlDir "06-measure.sql") `
            -Variables @(
                "dataset_size=$size",
                "index_state=without_index",
                "run_number=$run"
            ) -Csv
        $output | Add-Content -Encoding utf8 $resultFile
    }

    Invoke-PsqlFile -File (Join-Path $sqlDir "05-create-benchmark-indexes.sql")
    Invoke-PsqlFile -File (Join-Path $sqlDir "06-measure.sql") `
        -Variables @(
            "dataset_size=$size",
            "index_state=warmup_with_index",
            "run_number=0"
        ) | Out-Null

    for ($run = 1; $run -le $Runs; $run++) {
        $output = Invoke-PsqlFile -File (Join-Path $sqlDir "06-measure.sql") `
            -Variables @(
                "dataset_size=$size",
                "index_state=with_index",
                "run_number=$run"
            ) -Csv
        $output | Add-Content -Encoding utf8 $resultFile
    }
}

Write-Host "Benchmark completed: $resultFile"
