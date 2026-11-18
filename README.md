# CloudOps Agent

**CloudOps Agent is an AI-powered autonomous cloud operations system designed to automatically detect anomalies, investigate failures, execute authorized remediation actions, and verify recovery.**

The project integrates Spring Boot, PostgreSQL, React, AWS CloudWatch, Machine Learning, Spring AI, AI Agents, and Retrieval-Augmented Generation (RAG).

Its core objective is to build a closed-loop cloud operations workflow:

**Automated Monitoring → ML Anomaly Detection → AI Agent Diagnosis → Safe Remediation → Post-Remediation Verification**

Unlike a traditional monitoring dashboard or conversational AI assistant, CloudOps Agent is designed to proactively identify and investigate operational issues without requiring users to manually initiate each investigation.

The initial integration target is **FitCloud**, an AWS serverless application using Lambda, API Gateway, and DynamoDB.

> **Project Status:** Active development. Cloud monitoring data access, backend infrastructure, and foundational LLM integration are implemented. Autonomous detection, diagnosis orchestration, RAG, and remediation are under development.

---

## Project Goals

CloudOps Agent aims to:

- Continuously monitor backend services without requiring manual API requests.
- Detect operational anomalies using machine learning and statistical analysis.
- Automatically trigger AI Agent investigations when anomalies are detected.
- Correlate metrics, application logs, and operational knowledge to identify likely root causes.
- Use RAG to retrieve relevant incident histories, documentation, and remediation runbooks.
- Select and execute predefined remediation actions under explicit safety policies.
- Verify recovery using fresh operational evidence.
- Record incident investigations, decisions, actions, and outcomes for auditing.

The long-term objective is a system that can progress from detecting an operational failure to verifying its resolution with minimal manual intervention, while maintaining appropriate human oversight.

## System Architecture

The planned autonomous operations workflow is:

```text
AWS Cloud Infrastructure
(Lambda / API Gateway / DynamoDB)
              |
              v
       Amazon CloudWatch
        Metrics + Logs
              |
              v
     Automated Monitoring
     (Spring Boot Scheduler)
              |
              v
    ML Anomaly Detection
              |
       Anomaly Detected?
          /        \
        No          Yes
        |            |
   Continue          v
   Monitoring   AI Agent Investigation
                     |
              +------+------+
              |             |
              v             v
        Monitoring      RAG Knowledge
        Tools           Retrieval
              |             |
              +------+------+
                     |
                     v
             Root Cause Analysis
                     |
                     v
            Remediation Decision
                     |
              Safety / Approval
                     |
                     v
             Authorized Action
                     |
                     v
            Recovery Verification
                     |
                     v
            Incident Audit Trail
                     |
                     v
             Continue Monitoring
```

The monitoring and anomaly detection components initiate the workflow automatically.

The AI Agent is responsible for evidence gathering, diagnosis, and selecting permitted remediation tools. Execution remains subject to safety policies and approval requirements.

## Current Implementation

### 1. Backend Infrastructure

Implemented using Java 21 and Spring Boot.

Current capabilities include:

- REST API architecture.
- PostgreSQL persistence through Spring Data JPA.
- Incident creation, retrieval, pagination, filtering, and status management.
- System metrics collection infrastructure.
- AWS CloudWatch integration.
- Backend tests designed to avoid modifying the development database.

Incident management provides persistence and operational history. It is not intended to be the primary mechanism for initiating investigations.

### 2. AWS CloudWatch Metrics

The backend retrieves real operational metrics from AWS Lambda using the AWS SDK for Java.

Currently supported metrics:

| Metric | Statistic | Purpose |
|---|---|---|
| Invocations | Sum | Monitor Lambda invocation volume |
| Errors | Sum | Identify execution failures |
| Duration | Average | Monitor execution latency |

Example endpoint:

```http
GET /api/cloudwatch/metrics?functionName=SaveWorkout&metricName=Errors
```

Example response:

```json
[
  {
    "timestamp": "2026-11-17T09:20:00Z",
    "value": 0.0,
    "unit": "Count"
  }
]
```

The backend has successfully retrieved real CloudWatch metric data from the FitCloud `SaveWorkout` Lambda function.

Current metric retrieval is request-driven. Continuous scheduled monitoring and automated anomaly detection are not yet implemented.

### 3. Spring AI and Local LLM Integration

The backend integrates:

- Spring AI `ChatClient`.
- Ollama for local model inference.
- Qwen3 4B as the current language model.
- Java tools registered through Spring AI.
- A natural-language investigation endpoint.

The current AI service is configured to use available monitoring tools, avoid fabricating CloudWatch data, and distinguish observations from hypotheses.

Example investigation endpoint:

```http
POST /api/agent/investigate
Content-Type: application/json
```

Example request:

```json
{
  "message": "Use the get_lambda_metrics tool to check the Errors metric of the SaveWorkout Lambda function."
}
```

The endpoint has successfully generated contextual responses about Lambda monitoring data.

A CloudWatch metric retrieval tool has been registered with Spring AI. End-to-end verification of actual model-triggered tool execution remains a validation task.

**This endpoint is currently a development and debugging interface. It is not the intended primary trigger for production investigations.**

In the target architecture, the anomaly detection engine will automatically initiate AI investigations.

### 4. Frontend Foundation

The frontend uses:

- React.
- TypeScript.
- Vite.
- Recharts for future visualizations.

The frontend is currently a development foundation.

Its intended purpose is to display operational health, anomalies, investigation results, remediation decisions, and verification outcomes.

It is not intended to replace the autonomous backend workflow.

## Core Development Modules

### Module 1: Automated Monitoring

**Status: Planned — Next Development Priority**

Implement scheduled monitoring using Spring Boot.

Responsibilities:

- Periodically retrieve CloudWatch metrics.
- Monitor configured Lambda functions.
- Collect metrics within defined time windows.
- Handle missing data and AWS API failures.
- Store relevant monitoring observations.
- Trigger anomaly evaluation automatically.

The monitoring process must operate without user interaction.

### Module 2: Machine Learning Anomaly Detection

**Status: Planned**

Develop an anomaly detection component using operational metrics.

Potential techniques include:

- Statistical baselines and moving averages.
- Z-score-based detection.
- Isolation Forest.
- Other models selected based on available monitoring data.

Target anomalies include:

- Unexpected increases in error rates.
- Abnormal execution latency.
- Significant changes in invocation volume.
- Repeated failures.
- Unusual operational patterns.

Initial rule-based checks may be used to validate the monitoring pipeline, but they will not be presented as machine learning.

### Module 3: Autonomous AI Agent Investigation

**Status: Foundational LLM integration implemented; autonomous orchestration planned**

Automatically initiate investigations when anomalies are detected.

The Agent will:

1. Receive a structured anomaly event.
2. Retrieve relevant CloudWatch metrics.
3. Inspect application logs.
4. Gather additional operational evidence.
5. Retrieve relevant knowledge using RAG.
6. Analyze potential root causes.
7. Generate a structured diagnosis with supporting evidence.
8. Recommend an appropriate response.

The existing Spring AI integration and CloudWatch tools provide the initial foundation for this module.

### Module 4: Retrieval-Augmented Generation (RAG)

**Status: Planned**

Implement a knowledge retrieval pipeline using embeddings and a vector database.

Potential knowledge sources:

- Historical incident reports.
- Operational runbooks.
- System architecture documentation.
- Troubleshooting guides.
- Previous remediation outcomes.

The RAG pipeline will provide contextual evidence to improve diagnostic consistency and support remediation decisions.

### Module 5: Safe Automated Remediation

**Status: Planned**

Provide predefined tools for authorized remediation actions.

The Agent will not receive unrestricted infrastructure access.

Remediation controls will include:

- Tool allowlists.
- Least-privilege AWS permissions.
- Policy-based action restrictions.
- Human approval for higher-risk operations.
- Execution timeouts and safeguards.
- Audit logging.
- Prevention of repeated or conflicting actions.

Development will begin with simulated remediation and controlled test environments.

### Module 6: Post-Remediation Verification

**Status: Planned**

After a remediation action, CloudOps Agent will automatically collect fresh monitoring evidence.

The verification component will:

- Re-query relevant metrics.
- Evaluate whether the anomaly persists.
- Compare pre-remediation and post-remediation conditions.
- Record the outcome.
- Escalate unresolved incidents when appropriate.

A remediation action will not be considered successful solely because an API call completed.

### Module 7: Operations Dashboard

**Status: Frontend foundation implemented; dashboard planned**

The dashboard will provide visibility into:

- Monitored services.
- Current operational health.
- Detected anomalies.
- AI Agent investigations.
- Root-cause findings.
- Remediation recommendations and execution.
- Recovery verification.
- Historical incidents and audit records.

The dashboard is an observability and oversight interface for the autonomous system.

## Technology Stack

| Component | Technology | Status |
|---|---|---|
| Backend | Java 21, Spring Boot | Implemented |
| Persistence | PostgreSQL, Spring Data JPA | Implemented |
| Frontend | React, TypeScript, Vite | Foundation implemented |
| Visualization | Recharts | Dependency added |
| Cloud Monitoring | AWS CloudWatch, AWS SDK for Java | Metric retrieval implemented |
| AI Framework | Spring AI | Integrated |
| LLM Runtime | Ollama | Integrated |
| Language Model | Qwen3 4B | Integrated |
| Automated Monitoring | Spring Boot Scheduling | Planned |
| Anomaly Detection | Statistical methods, Machine Learning | Planned |
| Knowledge Retrieval | RAG, embeddings, vector database | Planned |
| Remediation | Authorized AI Agent tools | Planned |
| Recovery Verification | Automated metric re-evaluation | Planned |

## Repository Structure

```text
cloudops-agent/
├── backend/           Spring Boot backend, AI integration,
│                      monitoring, and persistence
└── frontend/          React and TypeScript frontend
```

## Local Development

### Prerequisites

- Java 21.
- PostgreSQL 16-compatible database.
- Existing `cloudops_db` database on `localhost:5433`.
- Maven Wrapper.
- Node.js LTS.
- Ollama.
- Qwen3 4B.
- AWS credentials with appropriate read permissions for CloudWatch.

### Database Configuration

Set the PostgreSQL password:

```powershell
$env:DB_PASSWORD = Read-Host 'Database password' -MaskInput
```

The default database username is `cloudops`. It can be overridden using `DB_USERNAME`.

Hibernate uses `ddl-auto=validate`, so the existing schema must be available before starting the application.

### Ollama Configuration

Download the model:

```powershell
ollama pull qwen3:4b
```

Test it locally:

```powershell
ollama run qwen3:4b
```

The backend expects Ollama at:

```text
http://localhost:11434
```

The current Spring AI model configuration is:

```properties
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=qwen3:4b
spring.ai.ollama.chat.options.temperature=0.2
```

### AWS Authentication

Configure an AWS CLI profile with the required permissions.

Example local development configuration:

```powershell
$env:AWS_PROFILE = "fitcloud-dev"
$env:AWS_REGION = "ap-southeast-2"
```

Authenticate using the configured profile mechanism, then verify:

```powershell
aws sts get-caller-identity --profile fitcloud-dev
```

Do not commit credentials, session tokens, or database passwords.

### Start the Backend

From the repository root:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Default backend address:

```text
http://localhost:8080
```

### Test CloudWatch Connectivity

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:8080/api/cloudwatch/metrics?functionName=SaveWorkout&metricName=Errors" `
    -Method GET
```

### Test the AI Investigation Foundation

```powershell
$body = @{
    message = "Use the get_lambda_metrics tool to check the Errors metric of the SaveWorkout Lambda function. Report the actual result."
} | ConvertTo-Json

$result = Invoke-RestMethod `
    -Uri "http://localhost:8080/api/agent/investigate" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body

$result.response
```

This endpoint is currently intended for manual validation and development. Automated investigation triggering will be introduced through the anomaly detection pipeline.

### Backend Tests

```powershell
cd backend
.\mvnw.cmd test
```

Existing tests are designed to avoid connecting to or modifying the development PostgreSQL database.

### Frontend Development

```powershell
cd frontend
npm ci
npm run dev
```

The Vite development environment proxies `/api` requests to `http://localhost:8080`.

Validation:

```powershell
npm run build
npm run lint
```

## Database Evolution

The existing database schema is validated at startup.

A versioned migration baseline should be introduced before adding persistent monitoring events, anomaly records, AI investigation results, remediation executions, or audit trails.

Existing `incidents`, `alerts`, and `system_metrics` tables should not be dropped without an explicit data-retention decision.

## Development Roadmap

### Phase 1 — Core Infrastructure

- [x] Spring Boot REST API.
- [x] PostgreSQL integration.
- [x] Basic incident management.
- [x] React and TypeScript frontend foundation.
- [x] AWS CloudWatch metric retrieval.
- [x] Spring AI and local Qwen3 integration.
- [x] Natural-language investigation endpoint.
- [x] Register CloudWatch metric retrieval tool.
- [ ] Verify end-to-end model-triggered tool execution.

### Phase 2 — Autonomous Monitoring and Detection

- [ ] Implement scheduled CloudWatch monitoring.
- [ ] Configure monitored services and metric windows.
- [ ] Implement initial rule-based anomaly evaluation.
- [ ] Introduce machine learning-based anomaly detection.
- [ ] Persist anomaly events and prevent duplicate investigations.

### Phase 3 — Autonomous AI Diagnosis

- [ ] Automatically trigger AI investigations from anomaly events.
- [ ] Integrate CloudWatch Logs.
- [ ] Correlate metrics and logs.
- [ ] Implement RAG using embeddings and a vector database.
- [ ] Generate structured root-cause investigation results.

### Phase 4 — Safe Remediation

- [ ] Implement predefined remediation tools.
- [ ] Add policy-based authorization and approval controls.
- [ ] Test simulated remediation.
- [ ] Enable approved actions in controlled environments.
- [ ] Record remediation decisions and execution results.

### Phase 5 — Verification and Dashboard

- [ ] Implement automatic post-remediation verification.
- [ ] Add incident audit trails.
- [ ] Build an operations dashboard.
- [ ] Demonstrate the complete autonomous workflow.

## Project Status

**Active Development — Autonomous Cloud Operations System**

The current implementation establishes the technical foundation for autonomous operations through Spring Boot, PostgreSQL, AWS CloudWatch, Spring AI, and a locally hosted Qwen3 language model.

The next priority is to implement scheduled monitoring and anomaly detection, allowing CloudOps Agent to proactively identify operational problems without requiring manual user requests.

Subsequent development will connect anomaly events to AI Agent investigations, RAG-based knowledge retrieval, controlled remediation, and recovery verification.

**The project's defining objective is not to answer monitoring questions, but to automatically detect operational failures, investigate their causes, safely respond, and verify recovery.**