# Milestone 2 Progress

## Current Slice: Patient Service

`patient-service` has moved from demo endpoints to a real CRUD service slice.

Implemented capabilities:

- Create patient profile.
- Page patient profiles by optional name keyword.
- Get patient detail with Redis cache-aside lookup.
- Update patient profile and evict Redis detail cache.
- Logical delete patient profile and evict Redis detail cache.
- Get patient summary for internal Feign calls.
- MySQL persistence through Spring Data JPA.
- Service-level unit tests covering create, cache hit, update eviction, delete, and paging.

## Patient APIs

| Method | Path | Description |
| --- | --- | --- |
| GET | `/patients/modules` | Patient service capability metadata |
| POST | `/patients` | Create patient |
| GET | `/patients` | Page patients |
| GET | `/patients/{id}` | Get patient detail |
| PUT | `/patients/{id}` | Update patient |
| DELETE | `/patients/{id}` | Logical delete patient |
| GET | `/patients/{id}/summary` | Get patient summary |
| GET | `/patients/internal/{id}/summary` | Internal patient summary for Feign |

Gateway paths use the `/api` prefix, for example:

```text
GET http://localhost:9527/api/patients
```

## Verification

```powershell
mvn -pl backend/patient-service -am test
mvn test
```

## Next Slice

Recommended next service: `appointment-service`.

Reason: appointment scheduling depends on patient summary lookup, so it naturally verifies the first real cross-service business flow.
