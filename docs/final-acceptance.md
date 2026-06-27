# Final Acceptance Matrix

| Requirement | Status | Evidence |
| --- | --- | --- |
| 1. Complete API documentation, CRUD/query APIs, total APIs >= 50 | Done | `docs/api-progress.md` documents 76 APIs; Swagger UI is enabled per service |
| 2. Redis middleware and database caching | Done | `patient-service` uses Redis cache-aside; `GET /patients/{id}/cache-demo` exposes key/TTL/strategy |
| 3. Unit test code and execution result | Done | Service tests across auth, gateway, patient, appointment, record, pharmacy, billing, admin; `mvn test` passes |
| 4. Git commit and push demo | Done | `docs/git-demo.md`, `scripts/verify-milestone4.ps1`, Git history; push command documented and attempted in milestone 4 |
| 5. Login authentication process | Done | `auth-service` issues JWT; gateway validates Bearer token; `JwtAuthenticationFilterTest` verifies protected route behavior |
| 6. Service registry/discovery and service registration/deregistration demo | Done | Nacos discovery configured for all services; `docs/demo-runbook.md` contains registration/deregistration steps |
| 7. Multiple services and remote data exchange | Done | Feign calls: appointment->patient, record->patient/appointment, pharmacy->record, billing->patient |
| 8. API gateway and gateway access demo | Done | `gateway-service` routes business APIs under `/api`; `scripts/demo-api.ps1` calls through gateway |
| 9. Config center and real-time config modification demo | Done | `infra/nacos/config/*.yml`, shared `/demo/config`, gateway `/api/config/*/demo/config`, actuator refresh exposure |
| 10. Development difficulties and solution process | Done | `docs/issue-log.md` records service boundaries, auth, Redis resilience, remote calls, billing states, config demo, UI encoding fix |

## Final Verification Commands

```powershell
.\scripts\verify-milestone4.ps1
```

Optional runtime demo after services are started:

```powershell
.\scripts\import-nacos-config.ps1
.\scripts\demo-api.ps1
```
