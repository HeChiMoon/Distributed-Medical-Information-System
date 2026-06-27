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

Recommended next service after appointment: `medical-record-service`.

Reason: medical records depend on both patient profiles and appointment records, so it is the natural next step after patient and appointment flows.

## Completed Slice: Appointment Service

`appointment-service` now supports appointment scheduling and the first real business-level remote call to `patient-service`.

Implemented capabilities:

- Create and page departments.
- Create and page doctors.
- Create, page, detail, update, and delete schedules.
- Book appointments by validating patient summary through Feign.
- Reject full schedules.
- Cancel appointments and release schedule quota.
- MySQL persistence through Spring Data JPA.
- Service-level unit tests covering schedule creation, booking, full schedule rejection, and cancellation.

## Appointment APIs

| Method | Path | Description |
| --- | --- | --- |
| GET | `/appointments/modules` | Appointment service capability metadata |
| GET | `/appointments/demo/remote-patient/{patientId}` | Feign patient summary demo |
| POST | `/appointments/departments` | Create department |
| GET | `/appointments/departments` | Page departments |
| POST | `/appointments/doctors` | Create doctor |
| GET | `/appointments/doctors` | Page doctors |
| POST | `/appointments` | Book appointment |
| GET | `/appointments` | Page appointments |
| GET | `/appointments/{id}` | Get appointment detail |
| PUT | `/appointments/{id}/cancel` | Cancel appointment |
| POST | `/schedules` | Create schedule |
| GET | `/schedules` | Page schedules |
| GET | `/schedules/{id}` | Get schedule detail |
| PUT | `/schedules/{id}` | Update schedule |
| DELETE | `/schedules/{id}` | Delete schedule |
