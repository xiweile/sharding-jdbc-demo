package com.example.shardingjdbc.controller;

import com.example.shardingjdbc.entity.Order;
import com.example.shardingjdbc.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class ShardingTestController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建测试订单并验证分片
     */
    @GetMapping("/create/{userId}")
    public Map<String, Object> createOrder(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 创建订单
            Order order = new Order();
            order.setUserId(userId);
            order.setOrderNo("TEST-" + System.currentTimeMillis());
            order.setAmount(new BigDecimal("100.00"));
            order.setStatus(1);
            
            // 保存订单
            Order savedOrder = orderService.createOrder(order);
            
            result.put("success", true);
            result.put("orderId", savedOrder.getOrderId());
            result.put("userId", savedOrder.getUserId());
            result.put("message", "订单创建成功，分片键user_id=" + userId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * 查询用户订单并验证分片
     */
    @GetMapping("/query/{userId}")
    public Map<String, Object> queryOrders(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 查询订单
            List<Order> orders = orderService.getOrdersByUserId(userId);
            
            result.put("success", true);
            result.put("userId", userId);
            result.put("orderCount", orders.size());
            result.put("orders", orders);
            result.put("message", "查询成功，分片键user_id=" + userId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * 测试不同用户ID的分片情况
     */
    @GetMapping("/test-sharding")
    public Map<String, Object> testSharding() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 创建不同用户ID的订单，测试分库分表
            for (long userId = 1; userId <= 4; userId++) {
                Order order = new Order();
                order.setUserId(userId);
                order.setOrderNo("SHARD-TEST-" + userId + "-" + System.currentTimeMillis());
                order.setAmount(new BigDecimal(userId * 100.00));
                order.setStatus(1);
                
                orderService.createOrder(order);
            }
            
            result.put("success", true);
            result.put("message", "已创建4个不同用户ID的订单，验证分片规则：user_id % 2");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}