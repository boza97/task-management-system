DROP INDEX IF EXISTS idx_perf_tasks_advanced_search;
DROP INDEX IF EXISTS idx_perf_tasks_project_workload;
DROP INDEX IF EXISTS idx_perf_audit_task_timestamp;
DROP INDEX IF EXISTS idx_tasks_project_id;
DROP INDEX IF EXISTS idx_tasks_status_id;
DROP INDEX IF EXISTS idx_tasks_assignee_id;
DROP INDEX IF EXISTS idx_tasks_priority;
DROP INDEX IF EXISTS idx_tasks_due_date;
DROP INDEX IF EXISTS idx_audit_logs_task_id;
DROP INDEX IF EXISTS idx_audit_logs_timestamp;

ANALYZE tasks;
ANALYZE audit_logs;
