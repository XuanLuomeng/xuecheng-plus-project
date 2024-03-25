package com.xuecheng.orders.service;

import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.po.XcPayRecord;

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

    /**
    * @description 查询支付记录号
    * @Param [payNo 交易记录号]
    * @return XcPayRecord
    * @author LuoXuanwei
    * @date 2024/3/25 13:17
    */
    public XcPayRecord getPayRecordByPayno(String payNo);
}
