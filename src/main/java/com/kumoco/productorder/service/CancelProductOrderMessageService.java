package com.kumoco.productorder.service;

import com.kumoco.productorder.message.CancelProductOrderMessage;
import com.kumoco.productorder.model.tmf.CancelProductOrderCreate;

import javax.servlet.http.HttpServletRequest;

public interface CancelProductOrderMessageService {

     CancelProductOrderMessage createCancelProductOrder(CancelProductOrderCreate body, HttpServletRequest request);
}
