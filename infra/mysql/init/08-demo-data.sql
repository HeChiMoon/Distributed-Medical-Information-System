USE dmis_patient;

INSERT INTO patient_info (
  id, patient_no, name, gender, birthday, id_card, phone, address, blood_type,
  allergy_history, medical_history, status
)
VALUES
  (101, 'P-DEMO-001', '王晓晨', 'FEMALE', '1992-04-16', 'DEMO-ID-001', '13800001001', '杭州市西湖区文三路 88 号', 'A', '青霉素过敏', '既往有慢性咽炎病史，本次用于门诊全流程演示。', 'ACTIVE'),
  (102, 'P-DEMO-002', '李建国', 'MALE', '1978-09-03', 'DEMO-ID-002', '13800001002', '上海市浦东新区张江路 66 号', 'O', '无明确药物过敏史', '高血压 5 年，规律服药，适合慢病复诊演示。', 'ACTIVE')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  phone = VALUES(phone),
  address = VALUES(address),
  allergy_history = VALUES(allergy_history),
  medical_history = VALUES(medical_history),
  status = VALUES(status);

INSERT INTO patient_contact (id, patient_id, contact_name, relationship, contact_phone, is_primary)
VALUES
  (101, 101, '王明', '父亲', '13900001001', 1),
  (102, 102, '李思思', '女儿', '13900001002', 1)
ON DUPLICATE KEY UPDATE
  contact_name = VALUES(contact_name),
  relationship = VALUES(relationship),
  contact_phone = VALUES(contact_phone),
  is_primary = VALUES(is_primary);

INSERT INTO patient_visit_summary (id, patient_id, last_visit_time, visit_count, last_diagnosis, last_doctor_name)
VALUES
  (101, 101, NOW() - INTERVAL 2 DAY, 3, '上呼吸道感染', '张敏'),
  (102, 102, NOW() - INTERVAL 7 DAY, 8, '高血压复诊', '陈立')
ON DUPLICATE KEY UPDATE
  last_visit_time = VALUES(last_visit_time),
  visit_count = VALUES(visit_count),
  last_diagnosis = VALUES(last_diagnosis),
  last_doctor_name = VALUES(last_doctor_name);

USE dmis_appointment;

INSERT INTO department_info (id, dept_code, dept_name, status)
VALUES
  (101, 'DEPT-GP', '全科门诊', 'ENABLED'),
  (102, 'DEPT-CARD', '心内科', 'ENABLED')
ON DUPLICATE KEY UPDATE
  dept_name = VALUES(dept_name),
  status = VALUES(status);

INSERT INTO doctor_info (id, doctor_no, doctor_name, dept_id, title, phone, status)
VALUES
  (101, 'DOC-001', '张敏', 101, '主任医师', '13700001001', 'ENABLED'),
  (102, 'DOC-002', '陈立', 102, '副主任医师', '13700001002', 'ENABLED')
ON DUPLICATE KEY UPDATE
  doctor_name = VALUES(doctor_name),
  dept_id = VALUES(dept_id),
  title = VALUES(title),
  phone = VALUES(phone),
  status = VALUES(status);

INSERT INTO doctor_schedule (
  id, doctor_id, dept_id, schedule_date, time_slot, max_number, current_number, status
)
VALUES
  (101, 101, 101, CURDATE() + INTERVAL 1 DAY, '09:00-10:00', 20, 1, 'OPEN'),
  (102, 102, 102, CURDATE() + INTERVAL 1 DAY, '10:00-11:00', 15, 1, 'OPEN')
ON DUPLICATE KEY UPDATE
  max_number = VALUES(max_number),
  current_number = VALUES(current_number),
  status = VALUES(status);

INSERT INTO appointment_record (
  id, appointment_no, patient_id, doctor_id, dept_id, schedule_id, appointment_date, time_slot, status, source
)
VALUES
  (101, 'APT-DEMO-001', 101, 101, 101, 101, CURDATE() + INTERVAL 1 DAY, '09:00-10:00', 'COMPLETED', 'WEB'),
  (102, 'APT-DEMO-002', 102, 102, 102, 102, CURDATE() + INTERVAL 1 DAY, '10:00-11:00', 'PENDING', 'WEB')
ON DUPLICATE KEY UPDATE
  patient_id = VALUES(patient_id),
  doctor_id = VALUES(doctor_id),
  dept_id = VALUES(dept_id),
  schedule_id = VALUES(schedule_id),
  appointment_date = VALUES(appointment_date),
  time_slot = VALUES(time_slot),
  status = VALUES(status),
  source = VALUES(source);

USE dmis_record;

INSERT INTO medical_record (
  id, record_no, patient_id, appointment_id, doctor_id, chief_complaint, present_illness,
  past_history, physical_exam, diagnosis, advice, record_status
)
VALUES
  (101, 'MR-DEMO-001', 101, 101, 101, '咽痛、咳嗽 2 天', '无明显发热，轻度咽部充血，夜间咳嗽加重。', '慢性咽炎病史。', '体温 36.8℃，咽部充血，双肺呼吸音清。', '上呼吸道感染', '多饮水，注意休息，按医嘱用药，症状加重及时复诊。', 'FINALIZED'),
  (102, 'MR-DEMO-002', 102, 102, 102, '血压控制复查', '近一周晨起血压 145/92mmHg 左右，无胸闷胸痛。', '高血压 5 年。', '血压 146/90mmHg，心肺查体未见明显异常。', '原发性高血压', '继续监测血压，低盐饮食，必要时调整降压方案。', 'DRAFT')
ON DUPLICATE KEY UPDATE
  chief_complaint = VALUES(chief_complaint),
  present_illness = VALUES(present_illness),
  past_history = VALUES(past_history),
  physical_exam = VALUES(physical_exam),
  diagnosis = VALUES(diagnosis),
  advice = VALUES(advice),
  record_status = VALUES(record_status);

INSERT INTO medical_order (
  id, order_no, record_id, patient_id, doctor_id, order_type, order_content, dosage, frequency, duration_days, status
)
VALUES
  (101, 'ORD-DEMO-001', 101, 101, 101, 'DRUG', '布洛芬缓释胶囊', '0.3g', 'bid', 3, 'ACTIVE'),
  (102, 'ORD-DEMO-002', 102, 102, 102, 'EXAM', '动态血压监测', '1 次', 'once', 1, 'ACTIVE')
ON DUPLICATE KEY UPDATE
  order_content = VALUES(order_content),
  dosage = VALUES(dosage),
  frequency = VALUES(frequency),
  duration_days = VALUES(duration_days),
  status = VALUES(status);

USE dmis_pharmacy;

INSERT INTO drug_info (
  id, drug_code, drug_name, specification, unit, manufacturer, sale_price, stock_warning_line, status
)
VALUES
  (101, 'DRUG-IBU-001', '布洛芬缓释胶囊', '0.3g*20粒', '盒', 'DMIS 制药', 18.50, 20, 'ENABLED'),
  (102, 'DRUG-AML-001', '苯磺酸氨氯地平片', '5mg*14片', '盒', 'DMIS 制药', 26.00, 15, 'ENABLED'),
  (103, 'DRUG-VIT-001', '维生素C片', '0.1g*100片', '瓶', 'DMIS 制药', 12.00, 30, 'ENABLED')
ON DUPLICATE KEY UPDATE
  drug_name = VALUES(drug_name),
  specification = VALUES(specification),
  unit = VALUES(unit),
  manufacturer = VALUES(manufacturer),
  sale_price = VALUES(sale_price),
  stock_warning_line = VALUES(stock_warning_line),
  status = VALUES(status);

INSERT INTO drug_inventory (
  id, drug_id, batch_no, quantity, locked_quantity, production_date, expire_date, warehouse_location
)
VALUES
  (101, 101, 'BATCH-IBU-202606', 120, 0, CURDATE() - INTERVAL 30 DAY, CURDATE() + INTERVAL 18 MONTH, 'A-01'),
  (102, 102, 'BATCH-AML-202606', 80, 0, CURDATE() - INTERVAL 45 DAY, CURDATE() + INTERVAL 24 MONTH, 'A-02'),
  (103, 103, 'BATCH-VIT-202606', 200, 0, CURDATE() - INTERVAL 20 DAY, CURDATE() + INTERVAL 12 MONTH, 'B-01')
ON DUPLICATE KEY UPDATE
  quantity = VALUES(quantity),
  locked_quantity = VALUES(locked_quantity),
  production_date = VALUES(production_date),
  expire_date = VALUES(expire_date),
  warehouse_location = VALUES(warehouse_location);

INSERT INTO inventory_flow (
  id, drug_id, batch_no, flow_type, change_quantity, before_quantity, after_quantity, biz_no, remark
)
VALUES
  (101, 101, 'BATCH-IBU-202606', 'IN', 120, 0, 120, 'SEED-IN-001', '演示初始化入库'),
  (102, 102, 'BATCH-AML-202606', 'IN', 80, 0, 80, 'SEED-IN-002', '演示初始化入库'),
  (103, 103, 'BATCH-VIT-202606', 'IN', 200, 0, 200, 'SEED-IN-003', '演示初始化入库')
ON DUPLICATE KEY UPDATE
  change_quantity = VALUES(change_quantity),
  before_quantity = VALUES(before_quantity),
  after_quantity = VALUES(after_quantity),
  remark = VALUES(remark);

INSERT INTO dispense_record (
  id, dispense_no, patient_id, record_id, order_id, drug_id, quantity, dispense_status, dispense_time, pharmacist_name
)
VALUES
  (101, 'DISP-DEMO-001', 101, 101, 101, 101, 1, 'DISPENSED', NOW() - INTERVAL 1 HOUR, '药师A')
ON DUPLICATE KEY UPDATE
  patient_id = VALUES(patient_id),
  record_id = VALUES(record_id),
  order_id = VALUES(order_id),
  drug_id = VALUES(drug_id),
  quantity = VALUES(quantity),
  dispense_status = VALUES(dispense_status),
  dispense_time = VALUES(dispense_time),
  pharmacist_name = VALUES(pharmacist_name);

USE dmis_billing;

INSERT INTO billing_record (
  id, bill_no, patient_id, appointment_id, record_id, total_amount, paid_amount, bill_status, payment_status, billing_time
)
VALUES
  (101, 'BILL-DEMO-001', 101, 101, 101, 38.50, 38.50, 'VALID', 'PAID', NOW() - INTERVAL 1 HOUR),
  (102, 'BILL-DEMO-002', 102, 102, 102, 70.00, 0.00, 'VALID', 'UNPAID', NOW() - INTERVAL 30 MINUTE)
ON DUPLICATE KEY UPDATE
  total_amount = VALUES(total_amount),
  paid_amount = VALUES(paid_amount),
  bill_status = VALUES(bill_status),
  payment_status = VALUES(payment_status),
  billing_time = VALUES(billing_time);

INSERT INTO billing_item (
  id, bill_id, item_type, item_name, unit_price, quantity, amount
)
VALUES
  (101, 101, 'REGISTRATION', '普通门诊挂号费', 20.00, 1, 20.00),
  (102, 101, 'DRUG', '布洛芬缓释胶囊', 18.50, 1, 18.50),
  (103, 102, 'REGISTRATION', '心内科专家号', 40.00, 1, 40.00),
  (104, 102, 'EXAM', '动态血压监测', 30.00, 1, 30.00)
ON DUPLICATE KEY UPDATE
  item_name = VALUES(item_name),
  unit_price = VALUES(unit_price),
  quantity = VALUES(quantity),
  amount = VALUES(amount);

INSERT INTO payment_record (
  id, bill_id, payment_no, payment_method, payment_amount, payment_time, payment_status
)
VALUES
  (101, 101, 'PAY-DEMO-001', 'WECHAT', 38.50, NOW() - INTERVAL 30 MINUTE, 'SUCCESS')
ON DUPLICATE KEY UPDATE
  payment_method = VALUES(payment_method),
  payment_amount = VALUES(payment_amount),
  payment_time = VALUES(payment_time),
  payment_status = VALUES(payment_status);

USE dmis_admin;

INSERT INTO sys_dict_type (id, dict_code, dict_name, status)
VALUES
  (101, 'DEMO_PATIENT_STATUS', '演示患者状态', 'ENABLED'),
  (102, 'DEMO_BILL_STATUS', '演示账单状态', 'ENABLED')
ON DUPLICATE KEY UPDATE
  dict_name = VALUES(dict_name),
  status = VALUES(status);

INSERT INTO sys_dict_item (id, dict_type_id, item_code, item_name, sort_no, status)
VALUES
  (101, 101, 'ACTIVE', '有效', 1, 'ENABLED'),
  (102, 101, 'INACTIVE', '停用', 2, 'ENABLED'),
  (103, 102, 'PAID', '已支付', 1, 'ENABLED'),
  (104, 102, 'UNPAID', '待支付', 2, 'ENABLED')
ON DUPLICATE KEY UPDATE
  item_name = VALUES(item_name),
  sort_no = VALUES(sort_no),
  status = VALUES(status);

INSERT INTO sys_operation_log (
  id, module_name, operation_type, operator_id, operator_name, request_uri, request_method, operation_result, operation_time
)
VALUES
  (101, '患者管理', 'CREATE', 1, 'admin', '/api/patients', 'POST', 'SUCCESS', NOW() - INTERVAL 2 HOUR),
  (102, '预约排班', 'CREATE', 1, 'admin', '/api/appointments', 'POST', 'SUCCESS', NOW() - INTERVAL 90 MINUTE),
  (103, '电子账单', 'PAY', 1, 'admin', '/api/bills/101/payments', 'POST', 'SUCCESS', NOW() - INTERVAL 30 MINUTE)
ON DUPLICATE KEY UPDATE
  operation_type = VALUES(operation_type),
  operator_name = VALUES(operator_name),
  request_uri = VALUES(request_uri),
  request_method = VALUES(request_method),
  operation_result = VALUES(operation_result),
  operation_time = VALUES(operation_time);
