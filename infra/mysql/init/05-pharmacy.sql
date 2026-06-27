USE dmis_pharmacy;

CREATE TABLE IF NOT EXISTS drug_info (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  drug_code VARCHAR(64) NOT NULL UNIQUE,
  drug_name VARCHAR(128) NOT NULL,
  specification VARCHAR(128),
  unit VARCHAR(32),
  manufacturer VARCHAR(128),
  sale_price DECIMAL(12, 2) NOT NULL DEFAULT 0,
  stock_warning_line INT NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_by BIGINT NULL,
  deleted BIT(1) NOT NULL DEFAULT b'0',
  version INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS drug_inventory (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  drug_id BIGINT NOT NULL,
  batch_no VARCHAR(64) NOT NULL,
  quantity INT NOT NULL DEFAULT 0,
  locked_quantity INT NOT NULL DEFAULT 0,
  production_date DATE,
  expire_date DATE,
  warehouse_location VARCHAR(128),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_by BIGINT NULL,
  deleted BIT(1) NOT NULL DEFAULT b'0',
  version INT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_drug_batch (drug_id, batch_no)
);

CREATE TABLE IF NOT EXISTS inventory_flow (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  drug_id BIGINT NOT NULL,
  batch_no VARCHAR(64),
  flow_type VARCHAR(32) NOT NULL,
  change_quantity INT NOT NULL,
  before_quantity INT NOT NULL,
  after_quantity INT NOT NULL,
  biz_no VARCHAR(64),
  remark VARCHAR(255),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_flow_drug (drug_id)
);

CREATE TABLE IF NOT EXISTS dispense_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dispense_no VARCHAR(64) NOT NULL UNIQUE,
  patient_id BIGINT NOT NULL,
  record_id BIGINT,
  order_id BIGINT,
  drug_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  dispense_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  dispense_time DATETIME,
  pharmacist_name VARCHAR(64),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_by BIGINT NULL,
  deleted BIT(1) NOT NULL DEFAULT b'0',
  version INT NOT NULL DEFAULT 0
);
