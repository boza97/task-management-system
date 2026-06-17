DROP FUNCTION IF EXISTS benchmark_query(TEXT, TEXT);

CREATE OR REPLACE FUNCTION benchmark_query(
    query_text TEXT,
    target_relation TEXT
)
RETURNS TABLE (
    planning_time_ms NUMERIC,
    execution_time_ms NUMERIC,
    plan_node TEXT,
    actual_rows BIGINT,
    shared_hit_blocks BIGINT,
    shared_read_blocks BIGINT
)
LANGUAGE plpgsql
AS $$
DECLARE
    plan JSON;
    scan_node TEXT;
BEGIN
    EXECUTE 'EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON) ' || query_text
    INTO plan;

    WITH RECURSIVE plan_nodes(node) AS (
        SELECT plan::jsonb->0->'Plan'

        UNION ALL

        SELECT child
        FROM plan_nodes
        CROSS JOIN LATERAL jsonb_array_elements(
            COALESCE(plan_nodes.node->'Plans', '[]'::jsonb)
        ) AS child
    )
    SELECT node->>'Node Type'
    INTO scan_node
    FROM plan_nodes
    WHERE node->>'Relation Name' = target_relation
    LIMIT 1;

    RETURN QUERY
    SELECT
        (plan->0->>'Planning Time')::numeric,
        (plan->0->>'Execution Time')::numeric,
        COALESCE(scan_node, plan->0->'Plan'->>'Node Type'),
        COALESCE((plan->0->'Plan'->>'Actual Rows')::bigint, 0),
        COALESCE((plan->0->'Plan'->>'Shared Hit Blocks')::bigint, 0),
        COALESCE((plan->0->'Plan'->>'Shared Read Blocks')::bigint, 0);
END;
$$;
