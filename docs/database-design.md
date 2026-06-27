# 数据库设计草案

## 数据库划分

- `dmis_auth`
- `dmis_patient`
- `dmis_appointment`
- `dmis_record`
- `dmis_pharmacy`
- `dmis_billing`
- `dmis_admin`

## 公共字段

核心业务表默认包含：

- `id bigint primary key`
- `created_at datetime`
- `updated_at datetime`
- `created_by bigint`
- `updated_by bigint`
- `deleted tinyint`
- `version int`

## 首期核心表

- `sys_user`, `sys_role`, `sys_permission`, `sys_user_role`, `sys_role_permission`
- `patient_info`, `patient_contact`, `patient_visit_summary`
- `department_info`, `doctor_info`, `doctor_schedule`, `appointment_record`
- `medical_record`, `medical_order`
- `drug_info`, `drug_inventory`, `inventory_flow`, `dispense_record`
- `billing_record`, `billing_item`, `payment_record`
- `sys_dict_type`, `sys_dict_item`, `sys_operation_log`
