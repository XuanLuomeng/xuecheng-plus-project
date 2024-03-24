package com.xuecheng.orders.service;

import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description TODO
 * @date 2024/3/24 22:24
 */
public interface OrderService {

    /**
     * @return com.xuecheng.orders.model.dto.PayRecordDto
     * @description 创建商品订单
     * @Param [userId, addOrderDto]
     * @author LuoXuanwei
     * @date 2024/3/24 22:24
     */
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);
}
