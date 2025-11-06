package com.example.shardingjdbc.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shardingjdbc.entity.Order;
import com.example.shardingjdbc.mapper.OrderMapper;
import com.example.shardingjdbc.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order createOrder(Order order) {
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        orderMapper.insert(order);
        return order;
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderMapper.selectByOrderId(orderId);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderMapper.selectByUserId(userId);
    }

    @Override
    public Page<Order> getAllOrders(Page<Order> page) {
        return orderMapper.selectPage(page, null);
    }

    @Override
    public Order updateOrder(Order order) {
        order.setUpdateTime(new Date());
        orderMapper.updateById(order);
        return order;
    }

    @Override
    public boolean deleteOrder(Long orderId) {
        return orderMapper.deleteById(orderId) > 0;
    }
}