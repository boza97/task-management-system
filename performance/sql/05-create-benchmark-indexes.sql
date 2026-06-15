CREATE INDEX idx_perf_tasks_advanced_search
ON tasks (project_id, status_id, priority, due_date, updated_at DESC);

CREATE INDEX idx_perf_tasks_project_workload
ON tasks (project_id, assignee_id, status_id);

CREATE INDEX idx_perf_audit_task_timestamp
ON audit_logs (task_id, timestamp DESC);

ANALYZE tasks;
ANALYZE audit_logs;
