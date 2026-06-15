SELECT
    :dataset_size AS dataset_size,
    'advanced_task_search' AS query_name,
    :'index_state' AS index_state,
    :run_number AS run_number,
    result.*
FROM benchmark_query(
    $query$
    SELECT
        t.id,
        t.title,
        t.priority,
        s.name AS status_name,
        u.first_name || ' ' || u.last_name AS assignee_name,
        t.due_date,
        t.updated_at
    FROM tasks t
    JOIN projects p ON p.id = t.project_id
    JOIN task_status s ON s.id = t.status_id
    LEFT JOIN users u ON u.id = t.assignee_id
    WHERE p.id = '10000000-0000-0000-0000-000000000042'
      AND s.code = 'READY_FOR_CODE_REVIEW'
      AND t.priority = 'HIGH'
      AND t.due_date BETWEEN current_date AND current_date + 365
    ORDER BY t.updated_at DESC
    LIMIT 100
    $query$,
    'tasks'
) AS result;

SELECT
    :dataset_size AS dataset_size,
    'project_workload' AS query_name,
    :'index_state' AS index_state,
    :run_number AS run_number,
    result.*
FROM benchmark_query(
    $query$
    SELECT
        u.id,
        u.first_name,
        u.last_name,
        count(*) AS task_count,
        count(*) FILTER (WHERE s.code = 'DONE') AS completed_count,
        round(
            100.0 * count(*) FILTER (WHERE s.code = 'DONE') /
            NULLIF(count(*), 0),
            2
        ) AS completion_percentage
    FROM tasks t
    JOIN users u ON u.id = t.assignee_id
    JOIN task_status s ON s.id = t.status_id
    WHERE t.project_id = '10000000-0000-0000-0000-000000000042'
    GROUP BY u.id, u.first_name, u.last_name
    HAVING count(*) >= 5
    ORDER BY task_count DESC, completed_count DESC
    LIMIT 20
    $query$,
    'tasks'
) AS result;

SELECT
    :dataset_size AS dataset_size,
    'audit_history' AS query_name,
    :'index_state' AS index_state,
    :run_number AS run_number,
    result.*
FROM benchmark_query(
    $query$
    SELECT id, action_type, timestamp, old_value, new_value
    FROM audit_logs
    WHERE task_id = '40000000-0000-0000-0000-000000000001'
    ORDER BY timestamp DESC
    LIMIT 100
    $query$,
    'audit_logs'
) AS result;
