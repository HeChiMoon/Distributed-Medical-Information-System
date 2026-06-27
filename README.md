# Distributed Medical Information System

A Spring Boot 3 + Spring Cloud + Vue 3 distributed medical information system demo.

## Current Scope

- `backend/dmis-common`: shared API response, paging model, error codes, business exception handling, validation handling, and common DTOs.
- `backend/gateway-service`: API gateway, service routes, and JWT authentication filter.
- `backend/auth-service`: login API and JWT token generation.
- `backend/patient-service`: patient CRUD, patient summaries, and Redis cache-aside detail lookup.
- `backend/appointment-service`: departments, doctors, schedules, appointment booking/cancellation, and Feign patient lookup.
- `backend/medical-record-service`: electronic medical records and medical order management.
- `backend/pharmacy-service`: drug catalog, inventory inbound, inventory flows, and dispensing with remote medical order validation.
- `backend/billing-service`: bill creation, bill items, payment records, and payment status transitions.
- `backend/admin-service`: dictionary type/item management and operation logs.
- `frontend`: Vue 3 admin console scaffold.
- `infra`: MySQL, Redis, Nacos, and database initialization scripts.

## Milestone Status

- Milestone 1: base distributed framework, gateway, auth, Nacos/Redis/MySQL infrastructure.
- Milestone 2: core business services and 68 business APIs.
- Milestone 3: integration demo layer, dynamic config endpoint, Redis cache demo endpoint, frontend demo console, runbook, and 76 documented APIs.

## Local Startup

1. Start infrastructure:

```powershell
docker compose up -d mysql redis nacos
```

2. Start backend services:

- `gateway-service`: `9527`
- `auth-service`: `9001`
- `patient-service`: `9002`
- `appointment-service`: `9003`
- `medical-record-service`: `9004`
- `pharmacy-service`: `9005`
- `billing-service`: `9006`
- `admin-service`: `9007`

3. Start frontend:

```powershell
cd frontend
npm install
npm run dev
```

## Verification

```powershell
mvn test
cd frontend
npm run build
```

## Demo APIs Through Gateway

All gateway paths use `http://localhost:9527/api`.

- Login: `POST /api/auth/login`
- Patient create/list/detail/update/delete: `/api/patients`
- Patient Redis cache demo: `GET /api/patients/{id}/cache-demo`
- Department create: `POST /api/appointments/departments`
- Doctor create: `POST /api/appointments/doctors`
- Schedule create: `POST /api/schedules`
- Appointment book/cancel: `/api/appointments`
- Medical record create/list/detail/update/delete: `/api/records`
- Medical order create/list/detail/stop: `/api/orders`
- Drug create/list/detail/update/delete: `/api/pharmacy/drugs`
- Inventory inbound/query: `/api/pharmacy/inventory/inbound`, `/api/pharmacy/inventory`
- Dispense create/list/detail: `/api/pharmacy/dispenses`
- Bill create/list/detail/cancel: `/api/bills`
- Bill payment create/list: `/api/bills/{id}/payments`
- Dictionary type CRUD: `/api/admin/dict-types`
- Dictionary item CRUD: `/api/admin/dict-items`
- Operation log create/query: `/api/admin/operation-logs`
- Feign demo: `GET /api/appointments/demo/remote-patient/1`
- Config center demo: `GET /api/config/patient/demo/config`

## API Documentation

Each service exposes SpringDoc Swagger UI:

```text
http://localhost:9001/swagger-ui.html
http://localhost:9002/swagger-ui.html
http://localhost:9003/swagger-ui.html
http://localhost:9004/swagger-ui.html
http://localhost:9005/swagger-ui.html
http://localhost:9006/swagger-ui.html
http://localhost:9007/swagger-ui.html
```

The consolidated API inventory is in `docs/api-progress.md`.

## Demo Runbook

Use `docs/demo-runbook.md` to demonstrate:

- JWT login and gateway authentication.
- Redis cache-aside behavior.
- Nacos service registration and deregistration.
- Feign remote calls between services.
- Gateway access to business APIs.
- Nacos config update through `/demo/config`.
- Unit test results and Git push workflow.

## Remote Repository

GitHub: https://github.com/HeChiMoon/Distributed-Medical-Information-System
