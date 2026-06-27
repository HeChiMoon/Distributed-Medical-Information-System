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
