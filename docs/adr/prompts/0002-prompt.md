# Prompt

Write an Architecture Decision Record in Michael Nygard format.

System:
ReconX Trade Reconciliation Platform

Decision:
Store instrument metadata in PostgreSQL JSONB.

Alternatives:
Separate tables
Nullable columns
EAV model

Constraints:
Flexible schema
PostgreSQL 16
Support multiple asset classes