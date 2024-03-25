package com.xuecheng.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.utils.IdWorkerUtils;
import com.xuecheng.base.utils.QRCodeUtil;
import com.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.xuecheng.orders.mapper.XcOrdersMapper;
import com.xuecheng.orders.mapper.XcPayRecordMapper;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.po.XcOrders;
import com.xuecheng.orders.model.po.XcOrdersGoods;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description TODO
 * @date 2024/3/24 22:23
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    XcOrdersMapper ordersMapper;

    @Autowired
    XcOrdersGoodsMapper ordersGoodsMapper;

    @Autowired
    XcPayRecordMapper payRecordMapper;

    @Value("${pay.qrcodeurl}")
    String qrcodeurl;

    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {

        //插入订单表,订单主表,订单明细表
        XcOrders orders = saveXcOrders(userId, addOrderDto);

        //插入支付记录
        XcPayRecord payRecord = createPayRecord(orders);
        Long payNo = payRecord.getPayNo();

        //生成二维码
        QRCodeUtil qrCodeUtil = new QRCodeUtil();
        //支付二维码的url
        String url = String.format(qrcodeurl, payNo);
        //二维码图片
        String qrCode = null;
        try {
            qrCode = qrCodeUtil.createQRCode(url, 200, 200);
        } catch (IOException e) {
            XueChengPlusException.cast("生成二维码出错");
        }

        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        payRecordDto.setQrcode(qrCode);

        return payRecordDto;
    }

    @Override
    public XcPayRecord getPayRecordByPayno(String payNo) {
        XcPayRecord xcPayRecord = payRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
        return xcPayRecord;
    }

    /**
     * @return com.xuecheng.orders.model.po.XcPayRecord
     * @description 保存支付记录
     * @Param [orders]
     * @author LuoXuanwei
     * @date 2024/3/24 22:48
     */
    public XcPayRecord createPayRecord(XcOrders orders) {

        //订单id
        Long orderId = orders.getId();
        XcOrders xcOrders = ordersMapper.selectById(orderId);

        //如果此订单不存在不能添加支付记录
        if (xcOrders == null) {
            XueChengPlusException.cast("订单不存在");
        }
        //订单状态
        String status = xcOrders.getStatus();
        //支付成功
        if ("601002".equals(status)) {
            XueChengPlusException.cast("此订单已支付");
        }

        //订单支付记录
        XcPayRecord xcPayRecord = new PayRecordDto();
        //支付记录号，将来传给第三方支付平台
        xcPayRecord.setPayNo(IdWorkerUtils.getInstance().nextId());
        xcPayRecord.setOrderId(orderId);
        xcPayRecord.setOrderName(xcOrders.getOrderName());
        xcPayRecord.setTotalPrice(xcOrders.getTotalPrice());
        xcPayRecord.setCurrency("CNY");
        xcPayRecord.setCreateDate(LocalDateTime.now());
        //未支付
        xcPayRecord.setStatus("601001");
        xcPayRecord.setUserId(xcOrders.getUserId());
        int insert = payRecordMapper.insert(xcPayRecord);
        if (insert <= 0) {
            XueChengPlusException.cast("插入支付记录失败");
        }

        return xcPayRecord;
    }

    /**
     * @return com.xuecheng.orders.model.po.XcOrders
     * @description 保存订单信息
     * @Param [userId, addOrderDto]
     * @author LuoXuanwei
     * @date 2024/3/24 22:32
     */
    public XcOrders saveXcOrders(String userId, AddOrderDto addOrderDto) {
        //插入订单表，订单主表，订单明细表
        //进行幂等性判断，同一个选课记录只能有一个订单
        XcOrders xcOrders = getOrderByBusinessId(addOrderDto.getOutBusinessId());
        if (xcOrders != null) {
            return xcOrders;
        }

        //插入订单主表
        xcOrders = new XcOrders();
        //使用雪花算法生成订单号
        xcOrders.setId(IdWorkerUtils.getInstance().nextId());
        xcOrders.setTotalPrice(addOrderDto.getTotalPrice());
        xcOrders.setCreateDate(LocalDateTime.now());
        //未支付
        xcOrders.setStatus("600001");
        xcOrders.setUserId(userId);
        //订单类型
        xcOrders.setOrderType("60201");
        xcOrders.setOrderName(addOrderDto.getOrderName());
        xcOrders.setOrderDetail(addOrderDto.getOrderDetail());
        xcOrders.setOrderDescrip(addOrderDto.getOrderDescrip());
        //如果是选课这里记录选课表的id
        xcOrders.setOutBusinessId(addOrderDto.getOutBusinessId());

        int insert = ordersMapper.insert(xcOrders);
        if (insert <= 0) {
            XueChengPlusException.cast("添加订单失败");
        }
        //订单id
        Long orderId = xcOrders.getId();
        //插入订单明细表
        //将前端传入的明细json串转成List
        String orderDetailJson = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoods = JSON.parseArray(orderDetailJson, XcOrdersGoods.class);
        //遍历xcOrderGoods插入订单明细表
        xcOrdersGoods.forEach(goods -> {
            goods.setOrderId(orderId);
            ordersGoodsMapper.insert(goods);
        });

        return xcOrders;
    }

    /**
     * @return com.xuecheng.orders.model.po.XcOrders
     * @description 根据业务id查询订单, 业务id是选课记录表中的主键
     * @Param [businessId]
     * @author LuoXuanwei
     * @date 2024/3/24 22:34
     */
    public XcOrders getOrderByBusinessId(String businessId) {
        XcOrders orders = ordersMapper.selectOne(new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId, businessId));
        return orders;
    }
}
