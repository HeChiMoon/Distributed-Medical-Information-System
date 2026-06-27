# Database Design

The system uses one MySQL schema per service so that each microservice owns its data.

## Schemas

- `dmis_auth`
- `dmis_patient`
- `dmis_appointment`
- `dmis_record`
- `dmis_pharmacy`
- `dmis_billing`
- `dmis_admin`

## Common Fields

Most business tables include:

- `id BIGINT PRIMARY KEY AUTO_INCREMENT`
- `created_at DATETIME`
- `updated_at DATETIME`
- `created_by BIGINT`
- `updated_by BIGINT`
- `deleted TINYINT`
- `version INT`

## Core Tables

### Auth

- `sys_user`
- `sys_role`
- `sys_permission`
- `sys_user_role`
- `sys_role_permission`

### Patient

- `patient_info`

### Appointment

- `department_info`
- `doctor_info`
- `doctor_schedule`
- `appointment_record`

### Medical Record

- `medical_record`
- `medical_order`

### Pharmacy

- `drug_info`
- `drug_inventory`
- `inventory_flow`
- `dispense_record`

### Billing

- `billing_record`
- `billing_item`
- `payment_record`

### Admin

- `sys_dict_type`
- `sys_dict_item`
- `sys_operation_log`

## Initialization Scripts

All database initialization SQL files live in `infra/mysql/init`.

Docker Compose mounts that directory into MySQL:

```yaml
./infra/mysql/init:/docker-entrypoint-initdb.d
```

Start infrastructure:

```powershell
docker compose up -d mysql redis nacos
```
