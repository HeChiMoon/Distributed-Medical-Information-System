# Demo Runbook

This runbook maps the original project requirements to concrete demo steps.

## 1. Start Infrastructure

Recommended daily startup:

```powershell
.\scripts\start-dev.ps1
```

The script starts infrastructure, imports Nacos config, packages and starts backend services, and starts the Vue frontend.

Manual infrastructure startup:

```powershell
docker compose up -d mysql redis nacos
```

Import Nacos config:

```powershell
.\scripts\import-nacos-config.ps1
```

Open Nacos:

```text
http://localhost:8848/nacos
```

## 2. Start Services

If `scripts/start-dev.ps1` was used, services are already started and logs are written to `logs/dev/`.

Start each Spring Boot service:

```text
gateway-service          9527
auth-service             9001
patient-service          9002
appointment-service      9003
medical-record-service   9004
pharmacy-service         9005
billing-service          9006
admin-service            9007
```

In Nacos, verify that services appear in the service list. Stop one service and refresh the list to demonstrate deregistration.

## 3. Login And Gateway Authentication

Login:

```powershell
curl -X POST http://localhost:9527/api/auth/login `
  -H "Content-Type: application/json" `
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

Call a protected API without a token:

```powershell
curl http://localhost:9527/api/patients
```

Expected result: `401 Missing bearer token`.

Call the same API with a token:

```powershell
curl http://localhost:9527/api/patients `
  -H "Authorization: Bearer <token>"
```

## 4. Redis Cache Demo

Show the cache key and TTL:

```powershell
curl http://localhost:9527/api/patients/1/cache-demo `
  -H "Authorization: Bearer <token>"
```

Then call patient detail twice:

```powershell
curl http://localhost:9527/api/patients/1 -H "Authorization: Bearer <token>"
curl http://localhost:9527/api/patients/1 -H "Authorization: Bearer <token>"
```

The first call loads MySQL and writes Redis. The second call can be served from Redis.

## 5. Remote Call Demo

Appointment service calls patient service:

```powershell
curl http://localhost:9527/api/appointments/demo/remote-patient/1 `
  -H "Authorization: Bearer <token>"
```

Business remote call chain examples:

- Create appointment: validates patient through Feign.
- Create medical record with appointment: validates patient and appointment ownership.
- Dispense drug: validates active medical order.
- Create bill: validates patient through Feign.

## 6. API Gateway Demo

All business calls use gateway prefix:

```text
http://localhost:9527/api/patients
http://localhost:9527/api/appointments
http://localhost:9527/api/records
http://localhost:9527/api/orders
http://localhost:9527/api/pharmacy/drugs
http://localhost:9527/api/bills
http://localhost:9527/api/admin/dict-types
```

## 7. Config Center Demo

Import files from `infra/nacos/config/*.yml` into Nacos.

Read current patient config through gateway:

```powershell
curl http://localhost:9527/api/config/patient/demo/config `
  -H "Authorization: Bearer <token>"
```

Modify `dmis.demo.config-message` in Nacos, publish it, then trigger refresh if needed:

```powershell
curl -X POST http://localhost:9002/actuator/refresh
```

Read the config endpoint again and verify the message changed.

## 8. Swagger API Docs

Open Swagger UI for each service:

```text
http://localhost:9001/swagger-ui.html
http://localhost:9002/swagger-ui.html
http://localhost:9003/swagger-ui.html
http://localhost:9004/swagger-ui.html
http://localhost:9005/swagger-ui.html
http://localhost:9006/swagger-ui.html
http://localhost:9007/swagger-ui.html
```

The consolidated inventory is in `docs/api-progress.md`.

## 9. Unit Test Result Demo

```powershell
mvn test
```

Expected result: all Maven modules pass.

## 10. Git Demo

```powershell
git status --short --branch
git log --oneline --decorate -5
git remote -v
git push -u origin main
```

## 11. Scripted Verification

Run all local verification gates:

```powershell
.\scripts\verify-milestone4.ps1
```

Run gateway smoke demo after services are started:

```powershell
.\scripts\demo-api.ps1
```

## 12. Stop Everything

Recommended daily shutdown:

```powershell
.\scripts\stop-dev.ps1
```

If a local MySQL process is occupying `3306` and is only used for this project:

```powershell
.\scripts\stop-dev.ps1 -StopLocalMySql
```

## 13. Demo Video

The required final delivery artifact is a demo video. Use `docs/demo-video-script.md` as the recording script. The video should explain:

- Technology stack: Vue 3, Spring Boot 3, Spring Cloud Gateway, Nacos, Redis, MySQL, OpenFeign, Swagger, Maven tests.
- Architecture: frontend, gateway, microservices, service discovery, config center, cache, database-per-service.
- Functions: login, patient management, appointment scheduling, medical records, medical orders, pharmacy inventory, billing, search center, integration checks.
- Verification: Swagger API docs, unit test result, frontend build result, Git commit and remote repository.
