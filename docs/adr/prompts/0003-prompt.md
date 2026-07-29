# Prompt

Write an Architecture Decision Record in Michael Nygard format.

System:
ReconX Trade Reconciliation Platform

Decision:
Use a GIN index instead of a B-tree index for JSONB metadata.

Alternatives:
No index
B-tree
Expression indexes

Constraints:
Fast JSONB searches
Large datasets
PostgreSQL 16