# Smart Society Complaint & Maintenance System (Enterprise Handover Grade)

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.3+](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud 2023.x](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-blue.svg)](https://spring.io/projects/spring-cloud)
[![React 18](https://img.shields.io/badge/React-18-61dafb.svg)](https://react.dev/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ed.svg)](https://www.docker.com/)

A production-ready, distributed microservices platform engineered for residential society maintenance management. It features AI-driven SLA priority prediction, cluster-safe automated SLA escalation monitoring, Apache Kafka event streaming with Dead Letter Queues (DLT), Redis Cache-Aside, OpenFeign RPC with Resilience4j circuit breakers, and real-time STOMP WebSocket alerts.

---

## 1. Architectural Topology & Service Matrix

```
                      +------------------------------------------+
                      |     Frontend Client (React 18 + Vite)    |
                      |     Port 5173 (Zustand, React Query)     |
                      +--------------------+---------------------+
                                           |
                                           | HTTP / REST / WebSockets
                                           v
                      +------------------------------------------+
                      |        API Gateway (Port 8080)           |
                      | Spring Cloud Gateway + Global JWT Filter |
                      +----+----------------+---------------+----+
                           |                |               |
         +-----------------+                |               +-----------------+
         |                                  |                                 |
         v                                  v                                 v
+------------------+              +-------------------+             +-------------------+
|   User Service   |              | Complaint Service |             |Notification Svc   |
|   (Port 8081)    |<--Feign/R4j--|    (Port 8082)    |--KafkaEvts->|    (Port 8083)    |
| Postgres: users  |              |Postgres:complaints|             | Kafka Consumer    |
| Spring Security6 |              | Redis / ShedLock  |             | STOMP WebSocket   |
+------------------+              +-------------------+             +---------+---------+
         ^                                  ^                                 |
         |                                  |                                 v
+--------+----------------------------------+---------------------------------+---------+
|                    Netflix Eureka Service Discovery Registry (Port 8761)              |
+---------------------------------------------------------------------------------------+
```

| Service | Port | Technology Stack | Primary Responsibilities |
| :--- | :--- | :--- | :--- |
| **discovery-service** | `8761` | Netflix Eureka Server, Spring Boot 3.3 | Service registry, health heartbeats, dynamic service discovery |
| **api-gateway** | `8080` | Spring Cloud Gateway, Reactive Redis, JJWT | Route forwarding, reactive JWT claim extraction (`X-User-*`), CORS |
| **user-service** | `8081` | Spring Boot 3.3, Spring Security 6, Flyway, PostgreSQL | RBAC identity, resident/staff directory, availability check API |
| **complaint-service**| `8082` | Spring Boot 3.3, OpenFeign, Resilience4j, Kafka, Redis, ShedLock | Ticket lifecycle, text taxonomy SLA prediction, cron SLA watcher |
| **notification-service**| `8083`| Spring Kafka, STOMP / SockJS WebSocket | Event consumer with retry/DLT, real-time push toast alerts, mock SMS/Email |
| **frontend-client** | `5173` | React 18, Vite, TypeScript, Tailwind, TanStack Query | Resident portal, staff Kanban workstation, admin KPI dashboard |

---

## 2. Pre-Configured Test Credentials

The database migrations (`V2__seed_users.sql`) automatically provision 5 demonstration persona accounts with known credentials:

| Persona | Role | Email | Password | Department / Flat |
| :--- | :--- | :--- | :--- | :--- |
| **Administrator** | `ROLE_ADMIN` | `admin@smartsociety.com` | `Password@123` | Society Management |
| **Staff Member 1** | `ROLE_STAFF` | `plumber@smartsociety.com` | `Password@123` | Plumbing Department |
| **Staff Member 2** | `ROLE_STAFF` | `electrician@smartsociety.com` | `Password@123` | Electrical Department |
| **Resident 1** | `ROLE_RESIDENT`| `resident1@smartsociety.com`| `Password@123` | Block A, Flat 101 |
| **Resident 2** | `ROLE_RESIDENT`| `resident2@smartsociety.com`| `Password@123` | Block B, Flat 204 |

> **Tip:** The frontend includes an **Instant Demo Persona Switcher** on the sign-in screen allowing 1-click evaluation of all roles without typing passwords!

---

## 3. Single-Command Setup (Docker Compose)

All databases (`society_users` and `society_complaints`), Redis, Kafka, Zookeeper, and all 6 application containers can be provisioned and launched with a single command:

```bash
docker compose -f infra/docker-compose.yml up --build
```

To run in the background (detached mode):
```bash
docker compose -f infra/docker-compose.yml up -d --build
```

To stop and remove containers:
```bash
docker compose -f infra/docker-compose.yml down
```

Once running, access:
- **Frontend Application:** `http://localhost:5173`
- **API Gateway:** `http://localhost:8080`
- **Eureka Service Registry Dashboard:** `http://localhost:8761`

---

## 4. Local Development Guide (Running Standalone)

### Prerequisites
- Java 21+ JDK
- Node.js 20+ & npm
- PostgreSQL running on `localhost:5432` with databases `society_users` and `society_complaints` (execute `infra/scripts/init-multiple-dbs.sql`)
- Redis running on `localhost:6379`
- Kafka running on `localhost:9092`

### Start Backend Services
In separate terminal windows:
```bash
# 1. Discovery Service
cd discovery-service
./mvnw spring-boot:run

# 2. User Service
cd ../user-service
./mvnw spring-boot:run

# 3. Complaint Service
cd ../complaint-service
./mvnw spring-boot:run

# 4. Notification Service
cd ../notification-service
./mvnw spring-boot:run

# 5. API Gateway
cd ../api-gateway
./mvnw spring-boot:run
```

### Start Frontend Client
```bash
cd frontend-client
npm install
npm run dev
```

---

## 5. End-to-End API Smoke Testing Guide (cURL)

### 1. Authenticate (Login as Resident)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "resident1@smartsociety.com",
    "password": "Password@123"
  }'
```
*Save the returned `accessToken` for subsequent calls.*

---

### 2. Live Priority Prediction (AI Engine Preview)
```bash
curl -X POST http://localhost:8080/api/v1/complaints/predict-priority \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "title": "Severe pipe burst flooding master bathroom",
    "description": "Water is rapidly flooding the balcony and floor",
    "categoryId": 1
  }'
```
*Expected response: Priority `HIGH` with `2 Hours SLA` due to emergency keyword matching.*

---

### 3. Submit a High-Priority Complaint
```bash
curl -X POST http://localhost:8080/api/v1/complaints \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "categoryId": 1,
    "title": "Severe pipe burst flooding bathroom",
    "description": "Water gushing from main pipe",
    "locationDetails": "Block A - Flat 101"
  }'
```

---

### 4. Check Staff Availability via Internal Inter-Service API
```bash
curl -X GET http://localhost:8080/api/v1/internal/staff/2
```
*Returns `isAvailable: true`, department `PLUMBING`.*

---

### 5. Assign Maintenance Staff (Login as Admin)
```bash
# First login as admin
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@smartsociety.com", "password": "Password@123"}'

# Assign staff member #2 (Plumber) to Complaint #1
curl -X POST http://localhost:8080/api/v1/complaints/1/assign \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -d '{
    "staffId": 2,
    "notes": "Emergency plumber dispatched immediately"
  }'
```

---

### 6. Progress Ticket to In Progress & Complete (Login as Staff)
```bash
# Accept ticket (set to IN_PROGRESS)
curl -X PATCH http://localhost:8080/api/v1/complaints/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <STAFF_TOKEN>" \
  -d '{
    "status": "IN_PROGRESS",
    "notes": "Arrived at Flat A-101. Replacing broken valve."
  }'

# Complete ticket (set to RESOLVED with photo)
curl -X PATCH http://localhost:8080/api/v1/complaints/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <STAFF_TOKEN>" \
  -d '{
    "status": "RESOLVED",
    "notes": "Pipe valve replaced and tested under full pressure.",
    "resolutionPhotoUrl": "https://images.unsplash.com/photo-1621905251189-08b45d6a269e"
  }'
```

---

### 7. Resident Submits 5-Star Feedback Rating
```bash
curl -X POST http://localhost:8080/api/v1/complaints/1/feedback \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <RESIDENT_TOKEN>" \
  -d '{
    "rating": 5,
    "review": "Very fast response! Plumber arrived within 30 minutes and fixed the leak cleanly."
  }'
```

---

### 8. Query Admin KPI Dashboard Analytics
```bash
curl -X GET http://localhost:8080/api/v1/complaints/society/1/stats \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

---

## 6. Resilience & Concurrency Guarantees

1. **Optimistic Locking (`@Version`):** Prevents lost updates when multiple staff members or administrators concurrently update or re-assign the same complaint. Throws RFC 7807 `409 Conflict` if version collision occurs.
2. **Cluster-Safe SLA Watcher (ShedLock):** Backed by PostgreSQL `shedlock` table, guaranteeing that only one microservice instance executes the SLA escalation scanner at any given minute across a clustered deployment.
3. **Resilience4j Circuit Breakers:** The `complaint-service` protects inter-service calls to `user-service` with a 10-call sliding window. If failure exceeds 50%, calls transition to open state and trigger the fallback factory.
4. **Kafka Retries & Dead Letter Queue (DLT):** Events in `notification-service` are retried up to 3 times with exponential backoff (`delay = 1000ms, multiplier = 2.0`). Poison pills are automatically routed to `society.complaint.events-dlt` and logged with critical diagnostic alerts.
