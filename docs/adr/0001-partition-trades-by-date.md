# ADR-0001 — Partition the `trades` table by `trade_date`

## Status

Accepted

## Context

ReconX processes approximately 50,000 trades per day and retains trade data for five years, resulting in roughly 91 million rows over time. Most analyst dashboards and reconciliation queries filter data by trade date.

Alternatives considered:

- Single large table
- Hash partitioning
- Yearly partitions

## Decision

Partition the `trades` table using PostgreSQL RANGE partitioning on `trade_date` with monthly partitions. A default partition captures unexpected records while new partitions are created in advance.

## Consequences

### Positive

- Faster date-range queries through partition pruning.
- Easier archival using partition detach operations.
- Smaller indexes improve maintenance.

### Negative

- Composite primary keys become more complex.
- Monthly partition maintenance is required.