package com.example.shardingjdbc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shardingjdbc.entity.Order;
import com.example.shardingjdbc.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam Long userId, 
                                            @RequestParam BigDecimal amount) {
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setAmount(amount);
        order.setStatus(0); // 0-待支付
        
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    /**
     * 根据订单ID查询订单
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据用户ID查询订单列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * 分页查询所有订单
     */
    @GetMapping
    public ResponseEntity<Page<Order>> getAllOrders(@RequestParam(defaultValue = "1") int current,
                                                    @RequestParam(defaultValue = "10") int size) {
        Page<Order> page = new Page<>(current, size);
        Page<Order> orderPage = orderService.getAllOrders(page);
        return ResponseEntity.ok(orderPage);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId,
                                                   @RequestParam Integer status) {
        Order order = orderService.getOrderById(orderId);
        if (order != null) {
            order.setStatus(status);
            Order updatedOrder = orderService.updateOrder(order);
            return ResponseEntity.ok(updatedOrder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        boolean deleted = orderService.deleteOrder(orderId);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 批量创建测试订单
     */
    @PostMapping("/batch")
    public ResponseEntity<String> createBatchOrders(@RequestParam int count) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            Order order = new Order();
            order.setUserId((long) random.nextInt(10)); // 随机用户ID 0-9
            order.setOrderNo(generateOrderNo());
            order.setAmount(new BigDecimal(random.nextInt(1000) + 1)); // 随机金额 1-1000
            order.setStatus(random.nextInt(3)); // 随机状态 0-2
            orderService.createOrder(order);
        }
        return ResponseEntity.ok("成功创建 " + count + " 条订单");
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + new Random().nextInt(1000);
    }
}