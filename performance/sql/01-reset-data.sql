DO $$
BEGIN
    IF to_regclass('public.databasechangelog') IS NULL
       OR to_regclass('public.tasks') IS NULL THEN
        RAISE EXCEPTION
            'Liquibase schema is missing. Start the application with the performance profile first.';
    END IF;
END;
$$;

TRUNCATE TABLE
    audit_logs,
    comments,
    tasks,
    project_memberships,
    projects,
    user_roles,
    users
CASCADE;
