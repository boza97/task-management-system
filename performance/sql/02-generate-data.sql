INSERT INTO users (id, email, first_name, last_name, password)
SELECT
    ('20000000-0000-0000-0000-' || lpad(i::text, 12, '0'))::uuid,
    'user' || i || '@example.com',
    'User',
    i::text,
    'benchmark-password'
FROM generate_series(1, 100) AS i;

INSERT INTO projects (
    id, project_key, name, description, owner_id, created_at, updated_at
)
SELECT
    ('10000000-0000-0000-0000-' || lpad(i::text, 12, '0'))::uuid,
    'P' || lpad(i::text, 3, '0'),
    'Performance project ' || i,
    'Project generated for database performance testing',
    ('20000000-0000-0000-0000-' || lpad(i::text, 12, '0'))::uuid,
    now(),
    now()
FROM generate_series(1, 100) AS i;

INSERT INTO tasks (
    id, title, description, priority, due_date, project_id, status_id,
    created_by_id, assignee_id, created_at, updated_at
)
SELECT
    ('40000000-0000-0000-' ||
     lpad(((g - 1) / 100000000)::text, 4, '0') || '-' ||
     lpad((((g - 1) % 100000000) + 1)::text, 12, '0'))::uuid,
    'Generated task ' || g,
    'Task generated for performance analysis',
    (ARRAY['LOW', 'MEDIUM', 'HIGH'])[
        ((((g - 1) / 600) % 3) + 1)::integer
    ],
    current_date + ((g - 1) % 365)::integer,
    ('10000000-0000-0000-0000-' ||
     lpad((((g - 1) % 100) + 1)::text, 12, '0'))::uuid,
    status_ids.ids[((((g - 1) / 100) % 6) + 1)::integer],
    ('20000000-0000-0000-0000-' ||
     lpad((((g - 1) % 100) + 1)::text, 12, '0'))::uuid,
    ('20000000-0000-0000-0000-' ||
     lpad(((((g - 1) / 100) % 10) + 1)::text, 12, '0'))::uuid,
    now() - ((g % 100000) || ' seconds')::interval,
    now() - ((g % 50000) || ' seconds')::interval
FROM generate_series(1, :row_count) AS g
CROSS JOIN (
    SELECT array_agg(id ORDER BY display_order) AS ids
    FROM task_status
) AS status_ids;

INSERT INTO audit_logs (
    id, action_type, timestamp, old_value, new_value, task_id, performed_by_id
)
SELECT
    ('50000000-0000-0000-' ||
     lpad(((g - 1) / 100000000)::text, 4, '0') || '-' ||
     lpad((((g - 1) % 100000000) + 1)::text, 12, '0'))::uuid,
    (ARRAY['STATUS_CHANGED', 'ASSIGNEE_CHANGED', 'PRIORITY_CHANGED'])[
        (((g - 1) % 3) + 1)::integer
    ],
    now() - ((g % 1000000) || ' milliseconds')::interval,
    'old-' || g,
    'new-' || g,
    ('40000000-0000-0000-0000-' ||
     lpad((((g - 1) % 100) + 1)::text, 12, '0'))::uuid,
    ('20000000-0000-0000-0000-' ||
     lpad((((g - 1) % 100) + 1)::text, 12, '0'))::uuid
FROM generate_series(1, :row_count) AS g;

ANALYZE users;
ANALYZE projects;
ANALYZE tasks;
ANALYZE audit_logs;
