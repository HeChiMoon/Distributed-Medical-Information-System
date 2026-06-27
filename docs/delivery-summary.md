# Delivery Summary

## Project

Distributed Medical Information System.

Backend:

- Spring Boot 3
- Spring Cloud
- Spring Cloud Gateway
- Nacos Discovery and Config
- OpenFeign
- Spring Data JPA
- Redis
- MySQL
- SpringDoc OpenAPI

Frontend:

- Vue 3
- Vite
- lucide-vue-next

## Business Scope

- Patient management
- Appointment scheduling
- Drug inventory
- Electronic medical records
- Electronic billing
- Pharmacy dispensing
- Medical orders
- Dictionary and operation log management

## Service List

| Service | Port | Main Capability |
| --- | ---: | --- |
| gateway-service | 9527 | API gateway and JWT authentication |
| auth-service | 9001 | Login and JWT issuing |
| patient-service | 9002 | Patient CRUD and Redis cache demo |
| appointment-service | 9003 | Department, doctor, schedule, appointment |
| medical-record-service | 9004 | Medical records and medical orders |
| pharmacy-service | 9005 | Drug catalog, inventory, dispensing |
| billing-service | 9006 | Bills, bill items, payments |
| admin-service | 9007 | Dictionary and operation logs |

## Key Demo Flow

1. Start MySQL, Redis, and Nacos.
2. Import Nacos config with `scripts/import-nacos-config.ps1`.
3. Start backend services and verify registration in Nacos.
4. Login through gateway and obtain JWT.
5. Call protected business APIs through gateway.
6. Demonstrate Redis cache endpoint.
7. Demonstrate Feign remote call endpoint.
8. Modify Nacos config and show `/demo/config` value.
9. Run unit tests and frontend build.
10. Show Git commit history and push to GitHub.

## Verification Result

- Backend tests: passed with `mvn test`.
- Frontend build: passed with `npm run build`.
- API count: 76 documented APIs.
- Git branch: `main`.
- Remote: `https://github.com/HeChiMoon/Distributed-Medical-Information-System.git`.
- Push status: local push was attempted, but GitHub HTTPS TLS handshake failed on this machine. Local commits are ready for retry with `git push -u origin main`.
