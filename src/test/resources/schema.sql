-- 创建t_order_0表
CREATE TABLE IF NOT EXISTS t_order_0 (
  order_id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  order_no VARCHAR(64) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  status INT NOT NULL,
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL
);

-- 创建t_order_1表
CREATE TABLE IF NOT EXISTS t_order_1 (
  order_id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  order_no VARCHAR(64) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  status INT NOT NULL,
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL
);