# Milestone 2 Progress

Milestone 2 status: complete.

Goal: implement the core business services for patient management, appointment scheduling, medical records, medical orders, pharmacy inventory/dispensing, billing/payment, and admin dictionaries/logs.

## Completed Slices

### Patient Service

Implemented capabilities:

- Patient create, page, detail, update, and logical delete.
- Patient summary APIs for external display and internal Feign calls.
- Redis cache-aside for patient detail query, with eviction on update/delete.
- MySQL persistence through Spring Data JPA.
- Service-level unit tests for create, cache hit, update eviction, delete, and paging.

### Appointment Service

Implemented capabilities:

- Department and doctor creation/query.
- Schedule creation, query, detail, update, and delete.
- Appointment booking with patient validation through Feign.
- Full-schedule rejection.
- Appointment cancellation with schedule quota release.
- Service-level unit tests for schedule creation, booking, full schedule rejection, and cancellation.

### Medical Record Service

Implemented capabilities:

- Medical record create, page, detail, update, and logical delete.
- Medical order create, page, detail, and stop.
- Patient validation through Feign before creating a record.
- Appointment ownership validation through Feign when a record references an appointment.
- Order ownership copied from record to keep patient/doctor data consistent.
- Service-level unit tests for record creation, appointment mismatch rejection, order creation, and order stop.

### Pharmacy Service

Implemented capabilities:

- Drug create, page, detail, update, and logical delete.
- Inventory inbound with inventory flow records.
- Inventory query by drug.
- Drug dispensing for active medical orders.
- Remote medical order lookup through Feign to `medical-record-service`.
- Insufficient-stock protection.
- Service-level unit tests for drug creation, inbound flow, insufficient stock rejection, and successful dispensing.

### Billing Service

Implemented capabilities:

- Bill creation with line items.
- Bill paging/detail and bill item query.
- Bill cancellation when unpaid.
- Payment record creation.
- Payment status transitions: `UNPAID`, `PARTIAL`, `PAID`.
- Cancelled bill payment rejection and over-payment protection.
- Patient validation through Feign to `patient-service`.
- Service-level unit tests for total calculation, partial payment, full payment, and cancelled bill rejection.

### Admin Service

Implemented capabilities:

- Dictionary type create, page, detail, update, and logical delete.
- Dictionary item create, page, detail, update, and logical delete.
- Operation log create, page, and detail query.
- Service-level unit tests for dictionary type creation, dictionary item creation, and operation log creation.

## Remote Call Chain

The current milestone includes real service-to-service data exchange:

- `appointment-service` -> `patient-service`: verify patient summary before booking.
- `medical-record-service` -> `patient-service`: verify patient before creating a record.
- `medical-record-service` -> `appointment-service`: verify appointment ownership.
- `pharmacy-service` -> `medical-record-service`: verify active medical order before dispensing.
- `billing-service` -> `patient-service`: verify patient before bill creation.

## Gateway Demo Paths

Gateway paths use the `/api` prefix:

```text
POST http://localhost:9527/api/patients
POST http://localhost:9527/api/appointments
POST http://localhost:9527/api/records
POST http://localhost:9527/api/orders
POST http://localhost:9527/api/pharmacy/drugs
POST http://localhost:9527/api/pharmacy/inventory/inbound
POST http://localhost:9527/api/pharmacy/dispenses
POST http://localhost:9527/api/bills
POST http://localhost:9527/api/bills/{id}/payments
POST http://localhost:9527/api/admin/dict-types
POST http://localhost:9527/api/admin/operation-logs
```

## API Count

Milestone 2 implemented API count: 68.

The detailed inventory is maintained in `docs/api-progress.md`.

## Verification

```powershell
mvn -pl backend/pharmacy-service -am test
mvn -pl backend/billing-service -am test
mvn -pl backend/admin-service -am test
mvn test
```

Latest verification result:

- `mvn test`: success across 10 Maven modules.
