package com.example.shardingjdbc;

import com.example.shardingjdbc.entity.Order;
import com.example.shardingjdbc.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
 
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@SpringBootTest(classes = ShardingJdbcApplication.class)
@Transactional
class ShardingJdbcApplicationTests {

    @Autowired
    private OrderService orderService;
   
    @Test
    void contextLoads() {
    }

    @Test
    void testCreateOrder() {
        Random random = new Random();
        
        // 创建10个订单，测试分片功能
        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.setUserId((long) random.nextInt(10)); // 随机用户ID 0-9
            order.setOrderNo("TEST" + System.currentTimeMillis() + i);
            order.setAmount(new BigDecimal(random.nextInt(1000) + 1)); // 随机金额 1-1000
            order.setStatus(random.nextInt(3)); // 随机状态 0-2
            
            Order createdOrder = orderService.createOrder(order);
            System.out.println("创建订单: " + createdOrder);
        }
    }

    @Test
    void testGetOrderById() {
        // 先创建一个订单
        Order order = new Order();
        order.setUserId(1L);
        order.setOrderNo("TEST_GET_BY_ID");
        order.setAmount(new BigDecimal("100.00"));
        order.setStatus(0);
        
        Order createdOrder = orderService.createOrder(order);
        System.out.println("创建订单: " + createdOrder);
        
        // 根据ID查询订单
        Order foundOrder = orderService.getOrderById(createdOrder.getOrderId());
        System.out.println("查询订单: " + foundOrder);
    }

    @Test
    void testGetOrdersByUserId() {
        // 为特定用户创建几个订单
        Long userId = 2L;
        // 查询该用户的所有订单
        List<Order> orders = orderService.getOrdersByUserId(userId);
        System.out.println("用户 " + userId + " 的订单数量: " + orders.size());
        orders.forEach(System.out::println);
    }
 

    @Test
    void testUpdateOrder() {
        // 创建一个订单
        Order order = new Order();
        order.setUserId(3L);
        order.setOrderNo("TEST_UPDATE");
        order.setAmount(new BigDecimal("200.00"));
        order.setStatus(0);
        
        Order createdOrder = orderService.createOrder(order);
        System.out.println("创建订单: " + createdOrder);
        
        // 只更新非分片键字段（不更新orderId和userId）
        Order updateOrder = new Order();
        updateOrder.setOrderId(createdOrder.getOrderId());
        updateOrder.setOrderNo("UPDATED_ORDER");
        updateOrder.setAmount(new BigDecimal("300.00"));
        updateOrder.setStatus(1);
        updateOrder.setCreateTime(createdOrder.getCreateTime());
        updateOrder.setUpdateTime(new java.util.Date());
        
        Order updatedOrder = orderService.updateOrder(updateOrder);
        System.out.println("更新订单: " + updatedOrder);
    }

    @Test
    void testDeleteOrder() {
        // 创建一个订单
        Order order = new Order();
        order.setUserId(4L);
        order.setOrderNo("TEST_DELETE");
        order.setAmount(new BigDecimal("300.00"));
        order.setStatus(0);
        
        Order createdOrder = orderService.createOrder(order);
        System.out.println("创建订单: " + createdOrder);
        
        // 删除订单
        boolean deleted = orderService.deleteOrder(createdOrder.getOrderId());
        System.out.println("删除结果: " + deleted);
        
        // 验证删除
        Order foundOrder = orderService.getOrderById(createdOrder.getOrderId());
        System.out.println("查询删除后的订单: " + foundOrder);
    }
}