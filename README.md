# SmartLead 🎯

> AI-powered lead qualification backend built with Java and Spring Boot.

SmartLead automatically analyzes inbound messages from a company contact form
and uses the HuggingFace Llama 3.3 AI model to determine whether a message
is a genuine sales lead. Qualified leads are stored with structured data
including a title, type, urgency level, and summary — ready for a sales team
to act on immediately.

---

## The Problem

Companies receive hundreds of contact form messages every day. Most are general
inquiries, compliments, or support requests. Hidden inside are genuine sales
opportunities — but finding them manually is slow, inconsistent, and expensive.

## The Solution

SmartLead automates lead qualification. Every inbound message is analyzed
asynchronously by an AI model that decides:

- Is this a genuine sales lead?
- If yes — what type is it and how urgent is it?
- What is a one-line summary for the sales team?

Sales teams open their dashboard and see only qualified, structured,
prioritized leads. No noise. No manual sorting.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core language |
| Spring Boot 3 | Backend framework |
| Spring Data JPA | Database access layer |
| H2 Database | Embedded in-memory database |
| Flyway | Database schema versioning and migrations |
| Bean Validation | Request input validation |
| OpenAPI / Swagger UI | Auto-generated API documentation |
| HuggingFace Inference API | AI lead qualification (Llama 3.3-70B) |
| JUnit 5 + Mockito | Unit and integration testing |
| Docker | Containerization |

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

**Package structure — feature based:**

```
com.amigoscode.smartlead/
├── message/        ← InboundMessage entity, controller, service, repository
├── lead/           ← Lead entity, controller, service, repository
├── ai/             ← HuggingFace client and prompt logic
└── common/
    ├── exception/  ← Global exception handler
    └── config/     ← HTTP client and app configuration
```

---

## How Lead Qualification Works

```
Contact form submission
        ↓
POST /api/v1/messages
        ↓
Message saved to database
        ↓
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

Full interactive documentation available at:

```
http://localhost:8080/swagger-ui.html
```

---

## Lead Data Structure

When the AI qualifies a message as a lead it generates:

| Field | Values | Description |
|---|---|---|
| `title` | Auto-generated | Short headline for the sales team |
| `type` | `DEMO_REQUEST` `PRICING_INQUIRY` `PARTNERSHIP` `SUPPORT` `OTHER` | Category of the lead |
| `urgency` | `LOW` `MEDIUM` `HIGH` | How urgently to follow up |
| `summary` | Auto-generated | One paragraph summary of the opportunity |

---

## Project Status

| Phase | Description | Status |
|---|---|---|
| 1 | Project setup and configuration | ✅ Done |
| 2 | Domain model and database | ✅ Done |
| 3 | REST API layer | 🚧 In progress |
| 4 | AI integration | ⏳ Pending |
| 5 | Testing and error handling | ⏳ Pending |
| 6 | Polish and deployment | ⏳ Pending |

---

## How to Run Locally

**Prerequisites**

- Java 21
- Maven
- A free HuggingFace account and API token → [huggingface.co](https://huggingface.co)

**Steps**

1. Clone the repository

```bash
git clone https://github.com/yourusername/smartlead.git
cd smartlead
```

2. Create a secrets file

```bash
touch src/main/resources/application-local.yaml
```

3. Add your HuggingFace token to that file

```properties
HF_TOKEN=your_actual_token_here
```

4. Set the active profile to `local` in your IDE run configuration

5. Run the application

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

6. Visit Swagger UI to explore and test the API

```
http://localhost:8080/swagger-ui.html
```

7. Visit H2 Console to inspect the database

```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:smartleaddb
Username: sa
Password: (leave empty)
```

---

## Example Usage

Submit a message:

```bash
curl -X POST http://localhost:8080/api/v1/messages \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "email": "john@techcorp.com",
    "message": "We are a team of 50 engineers and would like to schedule a demo of your enterprise plan next week. Our budget is approved."
  }'
```

View all qualified leads:

```bash
curl http://localhost:8080/api/v1/leads
```

---

## Author

**Your Name**
[LinkedIn](https://linkedin.com/in/yourprofile) ·
[GitHub](https://github.com/yourusername)

---

## License

This project was built as part of the
[Amigoscode Academy](https://skool.com/amigoscode-academy) technical challenge.