package com.example.shardingjdbc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shardingjdbc.entity.Order;

import java.util.List;

public interface OrderService {
    
    /**
     * 创建订单
     * @param order 订单信息
     * @return 创建成功的订单
     */
    Order createOrder(Order order);
    
    /**
     * 根据订单ID查询订单
     * @param orderId 订单ID
     * @return 订单信息
     */
    Order getOrderById(Long orderId);
    
    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> getOrdersByUserId(Long userId);
    
    /**
     * 分页查询所有订单
     * @param page 分页参数
     * @return 分页订单列表
     */
    Page<Order> getAllOrders(Page<Order> page);
    
    /**
     * 更新订单
     * @param order 订单信息
     * @return 更新成功的订单
     */
    Order updateOrder(Order order);
    
    /**
     * 删除订单
     * @param orderId 订单ID
     * @return 是否删除成功
     */
    boolean deleteOrder(Long orderId);
}