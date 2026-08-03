# Prompt

Write an Architecture Decision Record in Michael Nygard format.

System:
ReconX Trade Reconciliation Platform

Decision:
Partition the trades table using monthly RANGE partitioning on trade_date.

Alternatives:
Single table
Hash partitioning
Yearly partitioning

Constraints:
50,000 trades/day
5-year retention
PostgreSQL 16