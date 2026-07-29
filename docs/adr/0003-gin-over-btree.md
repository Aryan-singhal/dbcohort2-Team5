# ADR-0003 — Use GIN Index for JSONB Metadata

## Status

Accepted

## Context

ReconX frequently searches JSONB metadata using containment operators. Standard B-tree indexes do not efficiently support these queries.

Alternatives considered:

- No index
- B-tree index
- Expression indexes

## Decision

Use a PostgreSQL GIN index with `jsonb_path_ops` for JSONB metadata columns.

## Consequences

### Positive

- Faster JSONB containment searches.
- Better query performance.
- Reduced scan time for metadata filtering.

### Negative

- Additional storage usage.
- Slightly slower insert and update operations.