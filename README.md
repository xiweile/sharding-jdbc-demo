# ShardingJDBC Demo

这是一个基于Spring Boot和ShardingJDBC的分库分表示例项目。 5.5.2版本

## 项目结构

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── example
│   │           └── shardingjdbc
│   │               ├── ShardingJdbcApplication.java  # 启动类
│   │               ├── config
│   │               │   ├── DataSourceConfig.java      # 数据源配置
│   │               │   └── MybatisPlusConfig.java     # MyBatis Plus配置
│   │               ├── controller
│   │               │   └── OrderController.java       # 订单控制器
│   │               ├── entity
│   │               │   └── Order.java                # 订单实体
│   │               ├── mapper
│   │               │   └── OrderMapper.java           # 订单Mapper接口
│   │               └── service
│   │                   ├── OrderService.java         # 订单服务接口
│   │                   └── impl
│   │                       └── OrderServiceImpl.java # 订单服务实现
│   └── resources
│       ├── application.yml                           # 配置文件
│       └── mapper
│           └── OrderMapper.xml                       # Mapper XML文件
└── test
    └── java
        └── com
            └── example
                └── shardingjdbc
                    └── ShardingJdbcApplicationTests.java # 测试类
```

## 分片规则

本项目实现了以下分片规则：

1. **分库策略**：根据`user_id`进行分库，使用取模算法 `user_id % 2`
   - user_id为偶数的订单存储在ds0库
   - user_id为奇数的订单存储在ds1库

2. **分表策略**：根据`order_id`进行分表，使用取模算法 `order_id % 2`
   - order_id为偶数的订单存储在t_order_0表
   - order_id为奇数的订单存储在t_order_1表

3. **实际数据节点**：`ds$->{0..1}.t_order_$->{0..1}`，即：
   - ds0.t_order_0
   - ds0.t_order_1
   - ds1.t_order_0
   - ds1.t_order_1

## 环境准备

1. 安装MySQL数据库
2. 创建两个数据库：`ds0`和`ds1`
3. 在每个数据库中执行以下SQL创建表：

```sql
-- 在ds0和ds1数据库中分别执行
CREATE TABLE t_order_0 (
  order_id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  order_no VARCHAR(64) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  status INT NOT NULL,
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL
);

CREATE TABLE t_order_1 (
  order_id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  order_no VARCHAR(64) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  status INT NOT NULL,
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL
);
```

4. 修改`application.yml`中的数据库连接信息（用户名、密码等）

## 运行项目

1. 克隆项目到本地
2. 修改数据库配置
3. 运行以下命令启动项目：

```bash
mvn spring-boot:run
```

或者直接运行`ShardingJdbcApplication`类

## API接口

项目提供以下REST API接口：

1. **创建订单**
   ```
   POST /api/orders
   参数: userId, amount
   ```

2. **根据订单ID查询订单**
   ```
   GET /api/orders/{orderId}
   ```

3. **根据用户ID查询订单列表**
   ```
   GET /api/orders/user/{userId}
   ```

4. **分页查询所有订单**
   ```
   GET /api/orders?current=1&size=10
   ```

5. **更新订单状态**
   ```
   PUT /api/orders/{orderId}/status
   参数: status
   ```

6. **删除订单**
   ```
   DELETE /api/orders/{orderId}
   ```

7. **批量创建测试订单**
   ```
   POST /api/orders/batch
   参数: count
   ```

## 测试

运行测试类`ShardingJdbcApplicationTests`中的测试方法，验证分片功能：

- `testCreateOrder`: 测试创建订单和分片功能
- `testGetOrderById`: 测试根据ID查询订单
- `testGetOrdersByUserId`: 测试根据用户ID查询订单
- `testUpdateOrder`: 测试更新订单
- `testDeleteOrder`: 测试删除订单

## 注意事项

1. 确保数据库连接信息正确
2. 确保已创建所需的数据库和表
3. 分片键（user_id和order_id）不能为null
4. 跨分片查询可能需要特殊处理
5. 分布式事务需要额外配置

## 扩展

1. 可以添加更多的分片策略，如哈希、范围等
2. 可以配置读写分离
3. 可以添加分布式事务支持
4. 可以添加更多的业务实体和分片表
