package com.example.shardingjdbc;

import com.example.shardingjdbc.entity.Order;
import com.example.shardingjdbc.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(classes = ShardingJdbcTestApplication.class)
@Import(ShardingJdbcTestConfig.class)
@Transactional
@Rollback
public class ShardingJdbcDirectTest {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDirectSharding() {
        // 创建表
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS t_order_0 (" +
                "order_id BIGINT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "order_no VARCHAR(64) NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "status INT NOT NULL, " +
                "create_time TIMESTAMP NOT NULL, " +
                "update_time TIMESTAMP NOT NULL" +
                ")");
                
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS t_order_1 (" +
                "order_id BIGINT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "order_no VARCHAR(64) NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "status INT NOT NULL, " +
                "create_time TIMESTAMP NOT NULL, " +
                "update_time TIMESTAMP NOT NULL" +
                ")");
        
        // 创建订单
        Long userId = 1L;
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo("TEST001");
        order.setAmount(new BigDecimal("100.00"));
        order.setStatus(0);
        
        Order createdOrder = orderService.createOrder(order);
        System.out.println("创建订单: " + createdOrder);
        
        // 查询订单
        List<Order> orders = orderService.getOrdersByUserId(userId);
        System.out.println("用户 " + userId + " 的订单数量: " + orders.size());
        orders.forEach(System.out::println);
    }
}