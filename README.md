# CloudOps Agent

CloudOps Agent is the operations dashboard and backend foundation for monitoring
the FitCloud serverless workload on AWS.

The project is being prepared to read real operational signals from Amazon
CloudWatch for AWS Lambda, Amazon API Gateway and Amazon DynamoDB. It does not
currently connect to AWS, and the dashboard intentionally displays no simulated
metrics.

## Current capabilities

- Spring Boot REST API and health endpoint
- PostgreSQL-backed incident management
- Incident creation, lookup, pagination, filtering and status transitions
- React and TypeScript dashboard shell for future AWS telemetry
- Backend tests isolated from the development database

Local host CPU and memory monitoring is intentionally out of scope. FitCloud is
a serverless AWS workload, so host metrics from the machine running this service
do not represent FitCloud health.

## Repository layout

```text
backend/   Java 21, Spring Boot, Maven, JPA and PostgreSQL
frontend/  React, TypeScript, Vite and Recharts
```

## Backend development

Prerequisites:

- Java 21
- PostgreSQL 16-compatible database
- An existing `cloudops_db` schema on `localhost:5433`

Set the database password in the environment. Do not commit it:

```powershell
$env:DB_PASSWORD = Read-Host 'Database password' -MaskInput
cd backend
.\mvnw.cmd spring-boot:run
```

The default database username is `cloudops`; override it with `DB_USERNAME` if
needed. Hibernate uses `ddl-auto=validate` and will not create or drop tables.

Run the isolated backend tests with:

```powershell
cd backend
.\mvnw.cmd test
```

The context test disables database/JPA auto-configuration, replaces the Incident
repository with a mock and disables scheduling. Tests do not connect to the local
PostgreSQL development database.

## Frontend development

Prerequisite: a current Node.js LTS release.

```powershell
cd frontend
npm ci
npm run dev
```

Vite proxies `/api` requests to `http://localhost:8080` during development.

Validation commands:

```powershell
npm run build
npm run lint
```

## Database changes

The current database is treated as existing state and is validated on startup.
A versioned migration baseline must be added before evolving the Incident model
or introducing the AWS Alert and CloudWatch metric schemas. Cleanup work must not
drop the existing `incidents`, `alerts` or `system_metrics` tables without an
explicit data-retention decision.

## Planned AWS integration

The next implementation phase will define the CloudWatch data model, resource
scope and least-privilege read-only AWS access before adding SDK clients. Machine
learning, RAG, agent diagnosis and remediation are later phases.
