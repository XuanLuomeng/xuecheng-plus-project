package com.xuecheng.orders.service;

import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
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
     * @return XcPayRecord
     * @description 查询支付记录号
     * @Param [payNo 交易记录号]
     * @author LuoXuanwei
     * @date 2024/3/25 13:17
     */
    public XcPayRecord getPayRecordByPayno(String payNo);

    /**
     * @return com.xuecheng.orders.model.dto.PayRecordDto 支付记录信息
     * @description 请求支付宝查询支付结果
     * @Param [payNo 支付记录id]
     * @author LuoXuanwei
     * @date 2024/3/26 0:03
     */
    public PayRecordDto queryPayResult(String payNo);

    /**
     * @return void
     * @description 保存支付状态
     * @Param [payStatusDto]
     * @author LuoXuanwei
     * @date 2024/3/26 0:54
     */
    public void saveAliPayStatus(PayStatusDto payStatusDto);

    /**
    * @description 发送通知结果
    * @Param [mqMessage]
    * @return void
    * @author LuoXuanwei
    * @date 2024/3/26 21:00
    */
    public void notifyPayResult(MqMessage mqMessage);
}
