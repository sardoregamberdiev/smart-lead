# SmartLead 🎯

> AI-powered lead qualification backend built with Java and Spring Boot.

SmartLead automatically analyzes inbound messages from a company contact form and uses the HuggingFace Llama 3.3 AI model to determine whether a message is a genuine sales lead. Qualified leads are stored with structured data including a title, type, urgency level, and summary — ready for a sales team to act on immediately.

---

## The Problem

Companies receive hundreds of contact form messages every day. Most are general inquiries, compliments, or support requests. Hidden inside are genuine sales opportunities — but finding them manually is slow, inconsistent, and expensive.

## The Solution

SmartLead automates lead qualification. Every inbound message is analyzed asynchronously by an AI model that decides:

- Is this a genuine sales lead?
- If yes — what type is it and how urgent is it?
- What is a one-line summary for the sales team?

Sales teams open their dashboard and see only qualified, structured, prioritized leads. No noise. No manual sorting.

---

## Live Demo

API is live and accessible at:

```
https://smart-lead-production.up.railway.app
```

Swagger UI — explore and test all endpoints directly in the browser:

```
https://smart-lead-production.up.railway.app/swagger-ui/index.html
```

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core language |
| Spring Boot 4 | Backend framework |
| Spring Data JPA | Database access layer |
| H2 Database | Embedded in-memory database |
| Flyway | Database schema versioning and migrations |
| Bean Validation | Request input validation |
| OpenAPI / Swagger UI | Auto-generated API documentation |
| HuggingFace Inference API | AI lead qualification (Llama 3.3-70B) |
| JUnit 5 + Mockito | Unit and integration testing |
| Docker | Containerization with multi-stage build |
| GitHub Actions | CI/CD pipeline |
| Railway | Cloud deployment |

---

## Project Structure

The project follows a **feature-based package structure**. Instead of grouping all controllers together, all services together, etc. — each feature owns all of its related classes. This makes the codebase easier to navigate and scale.

```
src/
├── main/
│   ├── java/com/esardor/smartlead/
│   │   │
│   │   ├── SmartleadApplication.java
│   │   │
│   │   ├── message/                         ← Everything related to inbound messages
│   │   │   ├── InboundMessage.java          (Entity)
│   │   │   ├── MessageRepository.java       (Repository)
│   │   │   ├── MessageRequest.java          (DTO — what the client sends)
│   │   │   ├── MessageResponse.java         (DTO — what the client receives)
│   │   │   ├── MessageMapper.java           (Converts entity ↔ DTO)
│   │   │   ├── MessageService.java          (Business logic)
│   │   │   └── MessageController.java       (HTTP endpoints)
│   │   │
│   │   ├── lead/                            ← Everything related to qualified leads
│   │   │   ├── Lead.java                   (Entity)
│   │   │   ├── LeadType.java               (Enum — DEMO_REQUEST, PRICING_INQUIRY...)
│   │   │   ├── LeadUrgency.java            (Enum — LOW, MEDIUM, HIGH)
│   │   │   ├── LeadRepository.java         (Repository)
│   │   │   ├── LeadResponse.java           (DTO — what the client receives)
│   │   │   ├── LeadMapper.java             (Converts entity ↔ DTO)
│   │   │   ├── LeadService.java            (Business logic)
│   │   │   └── LeadController.java         (HTTP endpoints)
│   │   │
│   │   ├── ai/                              ← Everything related to AI integration
│   │   │   ├── HuggingFaceClient.java      (HTTP interface to HuggingFace API)
│   │   │   ├── HuggingFaceConfig.java      (Wires up the HTTP client with token)
│   │   │   ├── LeadQualificationService.java (Orchestrates AI qualification)
│   │   │   └── dto/
│   │   │       ├── ChatRequest.java        (What we send to HuggingFace)
│   │   │       ├── ChatResponse.java       (What HuggingFace sends back)
│   │   │       └── LeadQualificationResult.java (Parsed AI decision)
│   │   │
│   │   └── common/                          ← Shared across all features
│   │       ├── exception/
│   │       │   ├── ResourceNotFoundException.java  (Thrown when entity not found)
│   │       │   ├── ErrorResponse.java              (Shape of every error response)
│   │       │   └── GlobalExceptionHandler.java     (Handles all errors in one place)
│   │       └── config/
│   │           ├── AsyncConfig.java         (Thread pool for async AI calls)
│   │           ├── JacksonConfig.java       (JSON serialization configuration)
│   │           └── OpenApiConfig.java       (Swagger UI metadata)
│   │
│   └── resources/
│       ├── application.yml                  ← Main configuration file
│       ├── application-local.yml            ← Local secrets (never committed)
│       └── db/migration/
│           └── V1__create_tables.sql        ← Flyway database migration
│
└── test/
    ├── java/com/esardor/smartlead/
    │   ├── ai/
    │   │   └── LeadQualificationServiceTest.java   (Unit tests for AI logic)
    │   ├── message/
    │   │   ├── MessageServiceTest.java             (Unit tests for message service)
    │   │   └── MessageControllerIntegrationTest.java (Integration tests for API)
    │   └── lead/
    │       ├── LeadServiceTest.java                (Unit tests for lead service)
    │       └── LeadControllerIntegrationTest.java  (Integration tests for API)
    └── resources/
        └── application-test.yml             ← Isolated test database configuration
```

---

## What Each Class Does

### SmartleadApplication.java
The entry point of the entire application. Contains the `main` method that starts Spring Boot. Also has `@EnableAsync` which enables background thread processing for AI calls.

---

### message/ package

**InboundMessage.java** — Entity (database table)
Represents a raw contact form submission stored in the database. Maps directly to the `inbound_message` table. Contains the sender's name, email, message text, and timestamp.

**MessageRepository.java** — Repository (database access)
An interface that extends `JpaRepository`. Spring automatically generates all database operations — save, find, delete — without you writing any SQL. You just define the interface.

**MessageRequest.java** — DTO (Data Transfer Object)
Represents what the client sends when submitting a message. Contains validation annotations like `@NotBlank` and `@Email` that reject invalid input before it reaches the service layer.

**MessageResponse.java** — DTO (Data Transfer Object)
Represents what the client receives after submitting a message. Contains only the fields the client needs — never exposes internal database details.

**MessageMapper.java** — Mapper
Converts between `InboundMessage` entity and DTOs. Isolates the conversion logic in one place so controllers and services stay clean. If the response shape changes, only the mapper needs updating.

**MessageService.java** — Service (business logic)
Handles the core business logic. Saves the message to the database, then triggers the AI qualification asynchronously. The client gets a response immediately — the AI runs in the background.

**MessageController.java** — Controller (HTTP layer)
Handles HTTP requests. Maps `POST /api/v1/messages` to submit a message and `GET /api/v1/messages` to retrieve all messages. Delegates all logic to the service — the controller itself contains no business logic.

---

### lead/ package

**Lead.java** — Entity (database table)
Represents a qualified sales lead stored in the database. Maps to the `lead` table. Has a one-to-one relationship with `InboundMessage` — every lead is linked to the message that generated it.

**LeadType.java** — Enum
Defines the category of a lead. Values: `DEMO_REQUEST`, `PRICING_INQUIRY`, `PARTNERSHIP`, `SUPPORT`, `OTHER`. The AI picks one of these based on the message content.

**LeadUrgency.java** — Enum
Defines how urgently the sales team should follow up. Values: `LOW`, `MEDIUM`, `HIGH`. The AI determines this based on signals in the message like deadlines, budget approval, or ASAP language.

**LeadRepository.java** — Repository (database access)
Same pattern as `MessageRepository`. Spring generates all database operations automatically.

**LeadResponse.java** — DTO (Data Transfer Object)
Represents what the client receives when viewing leads. Includes the sender's name and email pulled from the linked message — so the sales team sees everything they need in one response without making a second API call.

**LeadMapper.java** — Mapper
Converts `Lead` entity to `LeadResponse` DTO. Handles the nested relationship — pulls sender name and email from the linked `InboundMessage`.

**LeadService.java** — Service (business logic)
Handles lead retrieval logic. `findAll()` returns all qualified leads. `findById()` returns a specific lead or throws `ResourceNotFoundException` if it doesn't exist — which triggers a clean 404 response.

**LeadController.java** — Controller (HTTP layer)
Handles HTTP requests for leads. Maps `GET /api/v1/leads` and `GET /api/v1/leads/{id}`. Delegates all logic to the service.

---

### ai/ package

**HuggingFaceClient.java** — HTTP Interface
A declarative HTTP client using Spring's `@HttpExchange`. You define the interface — Spring generates the actual HTTP call. No need to write RestTemplate or WebClient manually.

**HuggingFaceConfig.java** — Configuration
Wires up the `HuggingFaceClient` with the base URL and your API token. Creates a `RestClient` with the correct headers and registers it as a Spring bean.

**LeadQualificationService.java** — Service (AI orchestration)
The most important class in the AI package. Marked with `@Async` so it runs in a background thread. Builds the prompt, calls HuggingFace, parses the JSON response, and saves a lead if the AI says it qualifies. If anything goes wrong — bad response, network error, malformed JSON — it logs the error and continues silently without crashing.

**dto/ChatRequest.java** — DTO
The exact JSON structure HuggingFace expects to receive. Contains the model name, the conversation messages (system prompt + user message), temperature, and max tokens.

**dto/ChatResponse.java** — DTO
The exact JSON structure HuggingFace sends back. The actual AI answer is buried inside `choices[0].message.content`. The `content()` helper method extracts it cleanly.

**dto/LeadQualificationResult.java** — DTO
The parsed AI decision. After extracting the raw JSON string from `ChatResponse`, it gets deserialized into this record — giving you typed fields like `isLead`, `type`, `urgency`, and `summary`.

---

### common/ package

**ResourceNotFoundException.java** — Custom Exception
Thrown when a requested resource does not exist — for example, `GET /api/v1/leads/999` when lead 999 doesn't exist. Gets caught by `GlobalExceptionHandler` and returns a clean 404.

**ErrorResponse.java** — DTO
The consistent shape of every error response in the API. Contains `status`, `error`, `message`, `details` (a list of validation errors), and `timestamp`. Every error across the entire API looks the same.

**GlobalExceptionHandler.java** — Exception Handler
Annotated with `@RestControllerAdvice`. Catches exceptions from any controller across the entire application and converts them into clean JSON error responses. Handles validation errors (400), not found errors (404), type mismatch errors (400), and any unexpected errors (500).

**AsyncConfig.java** — Configuration
Configures the thread pool used for async AI calls. Sets core pool size, max pool size, and thread name prefix (`smartlead-async-`). Named `taskExecutor` so `@Async` uses it specifically.

**JacksonConfig.java** — Configuration
Registers a custom `ObjectMapper` bean with the `JavaTimeModule` so Java 8 date/time types like `LocalDateTime` serialize correctly to JSON.

**OpenApiConfig.java** — Configuration
Configures the Swagger UI metadata — title, description, version, and author contact. When you visit `/swagger-ui/index.html` this is where the page title and description come from.

---

### Database Migration

**V1__create_tables.sql** — Flyway Migration
The SQL that creates the `inbound_message` and `lead` tables on startup. Flyway runs this automatically before the app starts. Every schema change gets a new versioned file — this is the professional way to manage database changes safely across environments.

---

### Test Classes

**LeadQualificationServiceTest.java** — Unit Test
Tests the AI qualification logic in complete isolation. Mocks `HuggingFaceClient` and `LeadRepository` so no real HTTP calls or database writes happen. Tests: qualified lead saves correctly, non-lead is ignored, AI failure is handled gracefully, malformed JSON is handled gracefully.

**MessageServiceTest.java** — Unit Test
Tests that saving a message triggers AI qualification and that `findAll` returns the correct data. Mocks the repository and qualification service.

**LeadServiceTest.java** — Unit Test
Tests `findAll`, `findById` success, and `findById` throwing `ResourceNotFoundException` for unknown IDs.

**MessageControllerIntegrationTest.java** — Integration Test
Starts the full Spring context with a real H2 database. Tests the complete HTTP flow — submitting a message returns 201, blank name returns 400, invalid email returns 400, short message returns 400, GET all returns the correct list. The AI service is mocked so no real HuggingFace calls are made.

**LeadControllerIntegrationTest.java** — Integration Test
Starts the full Spring context. Creates real leads in the database before each test. Tests GET all leads, GET by ID, 404 for unknown ID, 400 for invalid ID type.

---

## Architecture

```
┌─────────────────────────────────────────────┐
│                REST API Layer                │
│         Controllers · DTOs · Validation      │
└───────────────────┬─────────────────────────┘
                    │
┌───────────────────▼─────────────────────────┐
│              Service Layer                   │
│      Business Logic · AI Integration         │
└───────────────────┬─────────────────────────┘
                    │
┌───────────────────▼─────────────────────────┐
│            Repository Layer                  │
│          Spring Data JPA · H2                │
└─────────────────────────────────────────────┘
```

---

## How Lead Qualification Works

```
Contact form submission
        ↓
POST /api/v1/messages
        ↓
Message saved to database instantly
        ↓
201 returned to client immediately
        ↓ (runs in background — client never waits)
AI analysis triggered asynchronously
        ↓
HuggingFace Llama 3.3 reads the message
        ↓
   Is it a lead?
   ┌────┴────┐
  YES        NO
   │          │
Lead saved   Nothing created
with title,  Message stored
type,        but not surfaced
urgency,
summary
```

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/messages` | Submit a new inbound message |
| `GET` | `/api/v1/messages` | Get all inbound messages |
| `GET` | `/api/v1/leads` | Get all qualified leads |
| `GET` | `/api/v1/leads/{id}` | Get a specific lead by ID |

---

## Lead Data Structure

| Field | Values | Description |
|---|---|---|
| `title` | Auto-generated | Short headline for the sales team |
| `type` | `DEMO_REQUEST` `PRICING_INQUIRY` `PARTNERSHIP` `SUPPORT` `OTHER` | Category of the lead |
| `urgency` | `LOW` `MEDIUM` `HIGH` | How urgently to follow up |
| `summary` | Auto-generated | One paragraph summary of the opportunity |

---

## Example Usage

**Submit a message:**

```bash
curl -X POST https://smart-lead-production.up.railway.app/api/v1/messages \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "email": "john@techcorp.com",
    "message": "We are a team of 50 engineers and would like to schedule a demo of your enterprise plan. Our budget is approved."
  }'
```

**View all qualified leads:**

```bash
curl https://smart-lead-production.up.railway.app/api/v1/leads
```

**Example lead response:**

```json
{
  "id": 1,
  "messageId": 1,
  "senderName": "John Smith",
  "senderEmail": "john@techcorp.com",
  "title": "Enterprise Demo Request from TechCorp",
  "type": "DEMO_REQUEST",
  "urgency": "HIGH",
  "summary": "Team of 50 engineers with approved budget seeking enterprise demo.",
  "createdAt": "2026-04-24T09:00:00"
}
```

---

## Project Status

| Phase | Description | Status |
|---|---|---|
| 1 | Project setup and configuration | ✅ Done |
| 2 | Domain model and database | ✅ Done |
| 3 | REST API layer | ✅ Done |
| 4 | AI integration | ✅ Done |
| 5 | Testing and error handling | ✅ Done |
| 6 | Docker, CI/CD and deployment | ✅ Done |

---

## How to Run Locally

### Prerequisites

- Java 21
- Maven
- Docker Desktop (optional)
- A free HuggingFace account and API token → [huggingface.co](https://huggingface.co)

---

### Option 1 — Run with IntelliJ

**1. Clone the repository**

```bash
git clone https://github.com/sardoregamberdiev/smart-lead.git
cd smart-lead
```

**2. Create a secrets file**

Create `src/main/resources/application-local.yml`:

```yaml
HF_TOKEN: your_actual_token_here
```

> ⚠️ This file is in `.gitignore` and will never be committed to GitHub.

**3. Set active profile**

Go to **Run** → **Edit Configurations** → **Active profiles** → type `local`

**4. Run the application**

Click the green Run button in IntelliJ.

**5. Visit Swagger UI**

```
http://localhost:8080/swagger-ui/index.html
```

**6. Visit H2 Console**

```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:smartleaddb
Username: sa
Password: (leave empty)
```

---

### Option 2 — Run with Docker

**1. Clone the repository**

```bash
git clone https://github.com/sardoregamberdiev/smart-lead.git
cd smart-lead
```

**2. Create a .env file**

```bash
echo "HF_TOKEN=your_actual_token_here" > .env
```

**3. Build and run**

```bash
docker-compose up --build
```

**4. Visit Swagger UI**

```
http://localhost:8080/swagger-ui/index.html
```

---

### Option 3 — Run with Maven

```bash
git clone https://github.com/sardoregamberdiev/smart-lead.git
cd smart-lead
echo "HF_TOKEN: your_token_here" > src/main/resources/application-local.yml
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

---

## Running Tests

```bash
./mvnw test
```

Tests use an isolated H2 database and never call the real HuggingFace API.

---

## CI/CD Pipeline

Every push to `main` automatically runs:

1. Checkout code
2. Set up Java 21
3. Run all tests
4. Build Docker image

---

## Environment Variables

| Variable | Description | Required |
|---|---|---|
| `HF_TOKEN` | HuggingFace API token for AI inference | Yes |

---

## Author

**Sardor Egamberdiev**

[GitHub](https://github.com/sardoregamberdiev) · [LinkedIn](https://linkedin.com/in/sardoregamberdiev)

---

## License

This project was built as part of the
[Amigoscode Academy](https://skool.com/amigoscode-academy) technical challenge.