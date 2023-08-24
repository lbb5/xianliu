package com.example.xianliu.service.impl;

import com.example.xianliu.service.PayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author libingbing
 */
@Service
public class PayServiceImpl implements PayService {
    private Logger logger = LoggerFactory.getLogger(PayService.class);


    //模拟支付成功
    @Override
    public String pay(BigDecimal amount) {
        logger.info("付款成功");
        return "付款成功，金额:"+amount;
    }
}
