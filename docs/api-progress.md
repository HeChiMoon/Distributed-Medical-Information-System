# API Progress

Target: at least 50 documented APIs.

Current implemented API count: 68.

| Domain | Count | Notes |
| --- | ---: | --- |
| Auth | 2 | Login, module metadata |
| Patient | 8 | CRUD, paging, summary, internal summary, Redis cache-aside detail |
| Appointment | 15 | Departments, doctors, schedules, booking, cancellation, remote patient demo |
| Medical Record | 10 | Medical records, medical orders, remote patient and appointment validation |
| Pharmacy | 11 | Drug CRUD, inventory inbound/query, dispensing, medical order remote lookup |
| Billing | 8 | Bill create/query/cancel, bill items, payment records |
| Admin | 14 | Dictionary type CRUD, dictionary item CRUD, operation log create/query |

## API Inventory

### Auth

| Method | Path | Description |
| --- | --- | --- |
| GET | `/auth/modules` | Auth service metadata |
| POST | `/auth/login` | Login and issue JWT |

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
| GET | `/patients/internal/{id}/summary` | Internal summary for Feign |

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
