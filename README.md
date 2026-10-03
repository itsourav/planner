# Embabel KYC Verification Agent (Planner)

A Spring Boot agentic AI application built using **Embabel** that demonstrates goal-driven workflow planning for automated Know-Your-Customer (KYC) compliance verification.

## Features

- **Automated Workflow Planning**: Powered by Embabel Agent Platform — plan transitions seamlessly:
  `KycRequest` ➔ `screenCustomer` ➔ `KycScreening` ➔ `assessRisk` ➔ `KycAssessment`
- **Model Support**: Uses OpenRouter models (default: `google/gemini-2.5-flash`) or OpenAI models.
- **Property Filtering**: Employs property filtering (`.withoutProperties("customerId")`) to prevent sensitive internal database identifiers from leaking into LLM prompts.
- **Interactive REST API**: Endpoints for invoking the agent workflow and testing various screening scenarios.

---

## Prerequisites

- Java 21+
- OpenRouter API key

---

## Configuration

Set your OpenRouter API key as an environment variable before launching the application:

```bash
export OPENROUTER_API_KEY="your-openrouter-api-key-here"
```

You can configure additional settings in [`src/main/resources/application.yaml`](file:///Users/sourav/Documents/Development/Agent%20Development/planner/src/main/resources/application.yaml):
- `embabel.models.default-llm`: Default LLM (e.g. `google/gemini-2.5-flash`)
- `embabel.openrouter.base-url`: Base URL for OpenRouter API
- `server.port`: Server port (default: `8080`)

---

## Running the Application

### 1. Build and Run
```bash
./mvnw spring-boot:run
```

### 2. Run Tests
```bash
./mvnw test
```

---

## API Usage Examples

Sample requests can be found in [`requests.http`](file:///Users/sourav/Documents/Development/Agent%20Development/planner/requests.http).

### Endpoint
`POST http://localhost:8080/api/kyc/verify`

#### Sample Request (Clean Customer)
```json
{
  "customerId": "CUST-001",
  "fullName": "Maria Müller",
  "dateOfBirth": "1985-03-15",
  "nationality": "Swiss",
  "occupation": "Software Engineer",
  "sourceOfFunds": "Employment salary"
}
```

#### Expected Outcome
- **Risk Level**: `LOW`
- **Recommendation**: `APPROVE`
