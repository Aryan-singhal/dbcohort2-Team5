# ADR-0002 — Store Instrument Metadata using JSONB

## Status

Accepted

## Context

Different financial instruments require different metadata attributes. Maintaining separate tables or adding many nullable columns would make the schema difficult to evolve.

Alternatives considered:

- Separate metadata tables
- Entity-Attribute-Value (EAV)
- Additional nullable columns

## Decision

Store flexible instrument metadata in a PostgreSQL JSONB column.

## Consequences

### Positive

- Flexible schema.
- Easier support for new instrument types.
- Efficient querying with PostgreSQL JSONB features.

### Negative

- Validation moves partly to the application.
- Updates may be slightly slower than fixed columns.