# API Progress

Target: at least 50 documented APIs.

Current implemented API count: 76.

| Domain | Count | Notes |
| --- | ---: | --- |
| Auth | 3 | Login, module metadata, dynamic config demo |
| Patient | 10 | CRUD, paging, summary, internal summary, Redis cache-aside detail, cache/config demos |
| Appointment | 16 | Departments, doctors, schedules, booking, cancellation, remote patient demo, config demo |
| Medical Record | 11 | Medical records, medical orders, remote patient and appointment validation, config demo |
| Pharmacy | 12 | Drug CRUD, inventory inbound/query, dispensing, medical order remote lookup, config demo |
| Billing | 9 | Bill create/query/cancel, bill items, payment records, config demo |
| Admin | 15 | Dictionary type CRUD, dictionary item CRUD, operation log create/query, config demo |

## API Inventory

### Auth

| Method | Path | Description |
| --- | --- | --- |
| GET | `/auth/modules` | Auth service metadata |
| POST | `/auth/login` | Login and issue JWT |
| GET | `/demo/config` | Dynamic config center demo |

### Patient

| Method | Path | Description |
| --- | --- | --- |
| GET | `/patients/modules` | Patient service metadata |
| POST | `/patients` | Create patient |
| GET | `/patients` | Page patients |
| GET | `/patients/{id}` | Get patient detail |
| PUT | `/patients/{id}` | Update patient |
| DELETE | `/patients/{id}` | Logical delete patient |
| GET | `/patients/{id}/summary` | Get patient summary |
| GET | `/patients/{id}/cache-demo` | Show Redis cache key, TTL, and strategy |
| GET | `/patients/internal/{id}/summary` | Internal summary for Feign |
| GET | `/demo/config` | Dynamic config center demo |

### Appointment

| Method | Path | Description |
| --- | --- | --- |
| GET | `/appointments/modules` | Appointment service metadata |
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
| GET | `/demo/config` | Dynamic config center demo |

### Medical Record

| Method | Path | Description |
| --- | --- | --- |
| GET | `/records/modules` | Medical record service metadata |
| POST | `/records` | Create medical record |
| GET | `/records` | Page medical records |
| GET | `/records/{id}` | Get medical record detail |
| PUT | `/records/{id}` | Update medical record |
| DELETE | `/records/{id}` | Delete medical record |
| POST | `/orders` | Create medical order |
| GET | `/orders` | Page medical orders |
| GET | `/orders/{id}` | Get medical order detail |
| PUT | `/orders/{id}/stop` | Stop medical order |
| GET | `/demo/config` | Dynamic config center demo |

### Pharmacy

| Method | Path | Description |
| --- | --- | --- |
| GET | `/pharmacy/modules` | Pharmacy service metadata |
| POST | `/pharmacy/drugs` | Create drug |
| GET | `/pharmacy/drugs` | Page drugs |
| GET | `/pharmacy/drugs/{id}` | Get drug detail |
| PUT | `/pharmacy/drugs/{id}` | Update drug |
| DELETE | `/pharmacy/drugs/{id}` | Logical delete drug |
| POST | `/pharmacy/inventory/inbound` | Inbound inventory and create inventory flow |
| GET | `/pharmacy/inventory` | Query inventory by drug |
| POST | `/pharmacy/dispenses` | Dispense drug for an active medical order |
| GET | `/pharmacy/dispenses` | Page dispense records |
| GET | `/pharmacy/dispenses/{id}` | Get dispense detail |
| GET | `/demo/config` | Dynamic config center demo |

### Billing

| Method | Path | Description |
| --- | --- | --- |
| GET | `/bills/modules` | Billing service metadata |
| POST | `/bills` | Create bill with items |
| GET | `/bills` | Page bills |
| GET | `/bills/{id}` | Get bill detail |
| GET | `/bills/{id}/items` | List bill items |
| PUT | `/bills/{id}/cancel` | Cancel unpaid bill |
| POST | `/bills/{id}/payments` | Pay bill |
| GET | `/bills/{id}/payments` | List bill payments |
| GET | `/demo/config` | Dynamic config center demo |

### Admin

| Method | Path | Description |
| --- | --- | --- |
| GET | `/admin/modules` | Admin service metadata |
| POST | `/admin/dict-types` | Create dictionary type |
| GET | `/admin/dict-types` | Page dictionary types |
| GET | `/admin/dict-types/{id}` | Get dictionary type |
| PUT | `/admin/dict-types/{id}` | Update dictionary type |
| DELETE | `/admin/dict-types/{id}` | Delete dictionary type |
| POST | `/admin/dict-items` | Create dictionary item |
| GET | `/admin/dict-items` | Page dictionary items |
| GET | `/admin/dict-items/{id}` | Get dictionary item |
| PUT | `/admin/dict-items/{id}` | Update dictionary item |
| DELETE | `/admin/dict-items/{id}` | Delete dictionary item |
| POST | `/admin/operation-logs` | Create operation log |
| GET | `/admin/operation-logs` | Page operation logs |
| GET | `/admin/operation-logs/{id}` | Get operation log |
| GET | `/demo/config` | Dynamic config center demo |

## Gateway Demo Routes

These routes demonstrate gateway + Nacos load-balanced access to dynamic config endpoints:

| Gateway Path | Target |
| --- | --- |
| `/api/config/auth/demo/config` | `auth-service:/demo/config` |
| `/api/config/patient/demo/config` | `patient-service:/demo/config` |
| `/api/config/appointment/demo/config` | `appointment-service:/demo/config` |
| `/api/config/record/demo/config` | `medical-record-service:/demo/config` |
| `/api/config/pharmacy/demo/config` | `pharmacy-service:/demo/config` |
| `/api/config/billing/demo/config` | `billing-service:/demo/config` |
| `/api/config/admin/demo/config` | `admin-service:/demo/config` |
