package com.example.xianliu.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;


/**
 * @author libingbing
 */
public interface PayService {

    String pay(BigDecimal amount);

}
