# CloudOps Agent

**CloudOps Agent is an AI-powered backend operations platform designed to automatically detect, diagnose, and safely remediate backend failures.**

The project combines Spring Boot, PostgreSQL, React, AWS CloudWatch, Machine Learning, Retrieval-Augmented Generation (RAG), and AI Agent capabilities.

Its primary goal is to move beyond traditional monitoring dashboards by building an autonomous incident-response workflow:

**Monitoring → Anomaly Detection → AI Diagnosis → Remediation → Verification**

The initial integration target is FitCloud, a serverless application deployed on AWS using Lambda, API Gateway, and DynamoDB.

## Project Objectives

CloudOps Agent aims to:

- Automatically identify backend anomalies using operational metrics and machine learning.
- Investigate potential root causes using logs, monitoring data, and historical incidents.
- Use LLMs and RAG to generate evidence-based diagnoses.
- Enable an AI Agent to select and execute predefined, authorized remediation actions.
- Verify remediation outcomes and maintain an auditable incident history.

Remediation will initially be tested in controlled environments with appropriate safeguards, permissions, and approval mechanisms.

## Current Capabilities

### Backend

- Java 21 and Spring Boot REST API.
- PostgreSQL-backed incident management.
- Incident creation, lookup, pagination, filtering, and status transitions.
- AWS SDK integration with Amazon CloudWatch.
- Real CloudWatch metric retrieval for AWS Lambda.
- Support for Lambda `Invocations`, `Errors`, and `Duration` metrics.
- Configurable AWS authentication through the AWS SDK default credential provider chain.
- Backend tests isolated from the development database.

### Frontend

- React and TypeScript application foundation.
- Vite development environment.
- Recharts dependency for future operational visualizations.

### AWS Integration

The backend can query real operational metrics from AWS CloudWatch for FitCloud Lambda functions.

Example API:

```http
GET /api/cloudwatch/metrics?functionName=SaveWorkout&metricName=Errors
```

Supported metrics:

| Metric | Statistic | Description |
|---|---|---|
| Invocations | Sum | Number of Lambda invocations |
| Errors | Sum | Number of Lambda execution errors |
| Duration | Average | Average Lambda execution duration |

CloudWatch integration currently supports on-demand metric retrieval. Automated anomaly detection, AI diagnosis, and remediation are not yet implemented.

## Planned Core Architecture

### 1. Monitoring and Data Collection

Collect operational signals from supported backend services, initially using Amazon CloudWatch.

### 2. Machine Learning Anomaly Detection

Apply statistical methods and machine learning models to identify abnormal backend behavior, including elevated error rates and unusual execution latency.

### 3. AI-Powered Root Cause Analysis

Use an LLM with tool access to investigate detected anomalies, inspect relevant metrics and logs, and generate evidence-based diagnoses.

### 4. Retrieval-Augmented Generation (RAG)

Introduce embeddings and a vector database to retrieve relevant technical documentation, incident histories, and operational runbooks.

### 5. AI Agent and Automated Remediation

Build an agent capable of selecting predefined remediation tools based on diagnostic evidence.

Execution will be restricted by explicit permissions, safety checks, and appropriate approval policies.

### 6. Post-Remediation Verification

Re-evaluate operational metrics after remediation to determine whether the incident has been resolved.

### 7. Operations Dashboard

Provide visibility into backend health, detected anomalies, AI diagnoses, remediation actions, and incident histories.

The dashboard supports the AI operations workflow rather than serving as the project's primary purpose.

## Technology Stack

| Layer | Technologies |
|---|---|
| Backend | Java 21, Spring Boot, Spring Data JPA |
| Frontend | React, TypeScript, Vite, Recharts |
| Database | PostgreSQL |
| Cloud Monitoring | AWS CloudWatch, AWS SDK for Java |
| Anomaly Detection | Machine Learning (planned) |
| AI Diagnosis | LLM and tool calling (planned) |
| Knowledge Retrieval | RAG, embeddings, vector database (planned) |
| Remediation | Controlled agent tools (planned) |

## Repository Structure

```text
cloudops-agent/
├── backend/       Spring Boot backend and AWS integration
└── frontend/      React and TypeScript frontend
```

## Backend Development

### Prerequisites

- Java 21
- PostgreSQL 16-compatible database
- Existing `cloudops_db` database on `localhost:5433`
- Maven Wrapper (included in the backend)
- AWS credentials with appropriate read-only CloudWatch permissions for AWS integration

Set the database password in the environment. Do not commit credentials.

```powershell
$env:DB_PASSWORD = Read-Host 'Database password' -MaskInput
cd backend
.\mvnw.cmd spring-boot:run
```

The default database username is `cloudops`. Override it using `DB_USERNAME` if needed.

Hibernate uses `ddl-auto=validate` and does not automatically create or drop tables.

### AWS Authentication

The AWS SDK uses the default credential provider chain.

For local development, select an appropriately configured AWS profile:

```powershell
$env:AWS_PROFILE = "fitcloud-dev"
$env:AWS_REGION = "ap-southeast-2"
```

Authenticate using the mechanism configured for the selected profile before starting the backend.

Do not commit AWS credentials, session tokens, or secrets.

### Backend Tests

```powershell
cd backend
.\mvnw.cmd test
```

The context test disables database/JPA auto-configuration, replaces the Incident repository with a mock, and disables scheduling. Tests are designed not to connect to the local PostgreSQL development database.

## Frontend Development

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

## Database Changes

The current database is treated as existing state and validated on startup.

A versioned migration baseline should be introduced before evolving the Incident model or adding new persistent schemas.

Existing `incidents`, `alerts`, and `system_metrics` tables must not be dropped without an explicit data-retention decision.

## Development Roadmap

- [x] Establish Spring Boot backend and PostgreSQL integration.
- [x] Implement basic incident management.
- [x] Establish React and TypeScript frontend.
- [x] Connect to AWS CloudWatch and retrieve real Lambda metrics.
- [ ] Implement the first end-to-end incident detection and AI diagnosis workflow.
- [ ] Introduce machine learning-based anomaly detection.
- [ ] Integrate CloudWatch Logs into the diagnostic workflow.
- [ ] Implement RAG with a vector database.
- [ ] Implement AI Agent tool calling and controlled remediation.
- [ ] Add post-remediation verification and audit trails.
- [ ] Build an operations dashboard for monitoring AI-driven incident response.

## Project Status

**Active development — AI operations prototype.**

CloudOps Agent currently provides a working backend foundation and real AWS CloudWatch metric retrieval.

Its autonomous anomaly detection, AI diagnosis, RAG, and remediation capabilities are under development.

The next priority is to implement an end-to-end AI-driven incident investigation workflow, rather than expanding the project into a standalone monitoring dashboard.