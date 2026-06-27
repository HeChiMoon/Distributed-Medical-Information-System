# Development Issue Log

## 1. Service boundary before CRUD

Phenomenon: The system needs many business modules, but implementing all CRUD first would blur microservice ownership.

Analysis: Patient, appointment, medical record, pharmacy, billing, and admin data should be owned by separate services.

Solution: Create independent service modules and database schemas first, then implement CRUD within each service boundary.

## 2. Authentication at gateway and service level

Phenomenon: Login belongs to `auth-service`, but business APIs should not be exposed directly without authentication.

Analysis: The gateway is the stable place for token validation, while services can focus on business behavior during the first milestone.

Solution: Add a global JWT filter to `gateway-service`, keep `/api/auth/login` public, and require `Authorization: Bearer <token>` for protected API paths.

## 3. Shared exception handling across services

Phenomenon: Each service needs consistent validation and error responses.

Analysis: Duplicating exception handlers in every service would make later changes noisy.

Solution: Add shared error code, business exception, and validation exception handling in `dmis-common`, then scan `com.dmis` from each service application.

## 4. Patient cache should not block core workflow

Phenomenon: Patient details should be cached in Redis, but local development or demo environments may start MySQL before Redis.

Analysis: Cache failures are infrastructure failures, while CRUD operations should still be testable and demonstrable with the database.

Solution: Wrap Redis get, put, and evict operations in `PatientCache`; cache failures fall back to database behavior and do not interrupt the patient workflow.

## 5. Appointment booking needs a business remote call

Phenomenon: A remote call demo alone does not prove that services exchange data during real business behavior.

Analysis: Appointment booking naturally depends on patient existence and patient summary lookup.

Solution: `appointment-service` calls `patient-service` through Feign before occupying schedule quota and creating an appointment record.

## 6. Schedule quota must stay consistent during cancellation

Phenomenon: Cancelling an appointment without releasing quota would make doctors appear fully booked too early.

Analysis: Appointment status and schedule `currentNumber` need to be updated together in one service transaction.

Solution: `AppointmentService.cancel` marks the appointment as `CANCELLED`, decrements the schedule quota with a zero floor, and saves both records in one transactional method.

## 7. Medical records need patient and appointment consistency

Phenomenon: A medical record can reference both a patient and an appointment, but those two IDs may not belong together.

Analysis: Creating a record from mismatched data would break later billing and pharmacy flows.

Solution: `medical-record-service` calls `patient-service` to verify the patient and calls `appointment-service` to verify appointment ownership before saving the record.

## 8. Pharmacy dispensing must not create negative inventory

Phenomenon: Dispensing directly from an order can overdraw stock if inventory is not checked first.

Analysis: Pharmacy inventory is owned by `pharmacy-service`, while the prescription/order is owned by `medical-record-service`.

Solution: `pharmacy-service` calls `medical-record-service` to verify an active order, loads the earliest-expiring inventory batch, rejects insufficient stock, deducts quantity in one transaction, and writes an inventory flow.

## 9. Billing payment status needs deterministic transitions

Phenomenon: A bill can be paid in multiple payments, so a single boolean paid flag is not enough.

Analysis: Demo billing needs to show unpaid, partially paid, and fully paid states without allowing over-payment or payment against cancelled bills.

Solution: `BillingService.pay` records each payment, updates `paidAmount`, and derives `UNPAID`, `PARTIAL`, or `PAID` from the accumulated amount.

## 10. Admin data is shared configuration, not business ownership

Phenomenon: Dictionary and operation-log APIs are needed for demos, but they should not leak into patient, appointment, pharmacy, or billing services.

Analysis: Keeping dictionaries and logs in `admin-service` preserves service boundaries and lets business services stay focused.

Solution: Add a dedicated `admin-service` slice with dictionary type/item CRUD and operation log query APIs.

## 11. Config center changes need a visible runtime endpoint

Phenomenon: Nacos config files existed, but there was no simple API that proved a runtime service had read the changed value.

Analysis: A demo needs an observable endpoint that can be called before and after modifying Nacos config.

Solution: Add shared `GET /demo/config` in `dmis-common`, bind `dmis.demo.config-message`, annotate it with `@RefreshScope`, expose actuator `refresh`, and route config demo paths through the gateway.

## 12. Redis caching needs to be explainable during a demo

Phenomenon: Patient detail already used Redis cache-aside, but the audience could not see which key or TTL was involved without reading code.

Analysis: The demo needs a safe observability endpoint that reveals cache strategy without exposing sensitive patient data.

Solution: Add `GET /patients/{id}/cache-demo` returning the Redis key, TTL, and cache-aside strategy.

## 13. Frontend demo page had encoding corruption

Phenomenon: The Vue dashboard text was garbled, which would distract from the system demo.

Analysis: The frontend is not the milestone's core, but it still needs to communicate architecture and progress clearly.

Solution: Rebuild the Vue page as a milestone-3 demo console covering API count, service registry, integration flow, and verification gates.
