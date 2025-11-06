package com.example.shardingjdbc;

import com.example.shardingjdbc.entity.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest(classes = ShardingJdbcApplication.class)
@Transactional
@Rollback
public class SimpleShardingTest {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleShardingTest.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private JdbcTemplate jdbcTemplate0;
    
    @Autowired
    private JdbcTemplate jdbcTemplate1;

    private final RowMapper<Order> orderRowMapper = new RowMapper<Order>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setOrderId(rs.getLong("order_id"));
            order.setUserId(rs.getLong("user_id"));
            order.setOrderNo(rs.getString("order_no"));
            order.setAmount(rs.getBigDecimal("amount"));
            order.setStatus(rs.getInt("status"));
            order.setCreateTime(rs.getTimestamp("create_time"));
            order.setUpdateTime(rs.getTimestamp("update_time"));
            return order;
        }
    };

    @Test
    public void testManualSharding() {
        logger.info("=== 开始测试手动分片逻辑 ===");
        
        // 在两个数据库中都创建表
        String createTableSql = "CREATE TABLE IF NOT EXISTS t_order_0 (" +
                "order_id BIGINT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "order_no VARCHAR(64) NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "status INT NOT NULL, " +
                "create_time TIMESTAMP NOT NULL, " +
                "update_time TIMESTAMP NOT NULL" +
                ")";
                
        String createTableSql1 = "CREATE TABLE IF NOT EXISTS t_order_1 (" +
                "order_id BIGINT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "order_no VARCHAR(64) NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "status INT NOT NULL, " +
                "create_time TIMESTAMP NOT NULL, " +
                "update_time TIMESTAMP NOT NULL" +
                ")";
        
        jdbcTemplate0.execute(createTableSql);
        jdbcTemplate0.execute(createTableSql1);
        jdbcTemplate1.execute(createTableSql);
        jdbcTemplate1.execute(createTableSql1);
        
        logger.info("=== 创建表完成 ===");
        
        // 模拟分片逻辑：根据user_id分库，根据order_id分表
        Long userId1 = 1L;
        Long userId2 = 2L;
        
        // 创建订单
        Order order1 = new Order();
        order1.setOrderId(1L);
        order1.setUserId(userId1);
        order1.setOrderNo("TEST001");
        order1.setAmount(new BigDecimal("100.00"));
        order1.setStatus(0);
        order1.setCreateTime(new java.util.Date());
        order1.setUpdateTime(new java.util.Date());
        
        Order order2 = new Order();
        order2.setOrderId(2L);
        order2.setUserId(userId1);
        order2.setOrderNo("TEST002");
        order2.setAmount(new BigDecimal("200.00"));
        order2.setStatus(0);
        order2.setCreateTime(new java.util.Date());
        order2.setUpdateTime(new java.util.Date());
        
        Order order3 = new Order();
        order3.setOrderId(3L);
        order3.setUserId(userId2);
        order3.setOrderNo("TEST003");
        order3.setAmount(new BigDecimal("300.00"));
        order3.setStatus(0);
        order3.setCreateTime(new java.util.Date());
        order3.setUpdateTime(new java.util.Date());
        
        Order order4 = new Order();
        order4.setOrderId(4L);
        order4.setUserId(userId2);
        order4.setOrderNo("TEST004");
        order4.setAmount(new BigDecimal("400.00"));
        order4.setStatus(0);
        order4.setCreateTime(new java.util.Date());
        order4.setUpdateTime(new java.util.Date());
        
        logger.info("=== 开始插入订单 ===");
        
        // 手动分片插入
        insertOrderWithSharding(order1);
        insertOrderWithSharding(order2);
        insertOrderWithSharding(order3);
        insertOrderWithSharding(order4);
        
        logger.info("=== 开始查询订单 ===");
        
        // 查询用户1的订单
        List<Order> user1Orders = getOrdersByUserId(userId1);
        logger.info("用户 {} 的订单数量: {}", userId1, user1Orders.size());
        user1Orders.forEach(order -> logger.info("订单: {}", order));
        
        // 查询用户2的订单
        List<Order> user2Orders = getOrdersByUserId(userId2);
        logger.info("用户 {} 的订单数量: {}", userId2, user2Orders.size());
        user2Orders.forEach(order -> logger.info("订单: {}", order));
        
        logger.info("=== 测试完成 ===");
    }
    
    // 添加一个main方法，用于直接运行测试
    public static void main(String[] args) {
        // 创建Spring上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("com.example.shardingjdbc");
        context.refresh();
        
        // 直接创建测试实例
        SimpleShardingTest test = new SimpleShardingTest();
        test.jdbcTemplate0 = context.getBean("jdbcTemplate0", JdbcTemplate.class);
        test.jdbcTemplate1 = context.getBean("jdbcTemplate1", JdbcTemplate.class);
        
        // 运行测试方法
        test.testManualSharding();
        
        // 关闭上下文
        context.close();
    }
    
    private void insertOrderWithSharding(Order order) {
        // 根据user_id分库
        JdbcTemplate dbTemplate = order.getUserId() % 2 == 0 ? jdbcTemplate1 : jdbcTemplate0;
        // 根据order_id分表
        String tableSuffix = order.getOrderId() % 2 == 0 ? "1" : "0";
        String tableName = "t_order_" + tableSuffix;
        
        String sql = "INSERT INTO " + tableName + " (order_id, user_id, order_no, amount, status, create_time, update_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        dbTemplate.update(sql, order.getOrderId(), order.getUserId(), order.getOrderNo(), 
                          order.getAmount(), order.getStatus(), order.getCreateTime(), order.getUpdateTime());
        
        logger.info("插入订单到表 {}: {}", tableName, order.getOrderNo());
    }
    
    private List<Order> getOrdersByUserId(Long userId) {
        // 根据user_id分库
        JdbcTemplate dbTemplate = userId % 2 == 0 ? jdbcTemplate1 : jdbcTemplate0;
        
        // 查询所有可能的表
        List<Order> orders = dbTemplate.query(
            "SELECT * FROM t_order_0 WHERE user_id = ?", 
            orderRowMapper, userId);
        
        orders.addAll(dbTemplate.query(
            "SELECT * FROM t_order_1 WHERE user_id = ?", 
            orderRowMapper, userId));
        
        return orders;
    }
}