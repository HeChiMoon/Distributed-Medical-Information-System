USE dmis_record;

CREATE TABLE IF NOT EXISTS medical_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  record_no VARCHAR(64) NOT NULL UNIQUE,
  patient_id BIGINT NOT NULL,
  appointment_id BIGINT,
  doctor_id BIGINT NOT NULL,
  chief_complaint VARCHAR(500),
  present_illness VARCHAR(1000),
  past_history VARCHAR(1000),
  physical_exam VARCHAR(1000),
  diagnosis VARCHAR(500),
  advice VARCHAR(500),
  record_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  version INT NOT NULL DEFAULT 0,
  KEY idx_record_patient (patient_id)
);

CREATE TABLE IF NOT EXISTS medical_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL UNIQUE,
  record_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  doctor_id BIGINT NOT NULL,
  order_type VARCHAR(32) NOT NULL,
  order_content VARCHAR(500) NOT NULL,
  dosage VARCHAR(128),
  frequency VARCHAR(128),
  duration_days INT,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  version INT NOT NULL DEFAULT 0,
  KEY idx_order_record (record_id),
  KEY idx_order_patient (patient_id)
);
