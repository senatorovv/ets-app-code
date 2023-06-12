package com.kumoco.productorder.service;

import com.kumoco.productorder.message.ProductOrderMessage;
import com.kumoco.productorder.model.tmf.ProductOrderCreate;
import com.kumoco.productorder.model.tmf.ProductOrderUpdate;

import javax.servlet.http.HttpServletRequest;

public interface ProductOrderMessageService {

    ProductOrderMessage retrieveProductOrder(String id,  String fields, HttpServletRequest request);

    ProductOrderMessage patchProductOrder(ProductOrderUpdate body, String id, HttpServletRequest request);

    ProductOrderMessage createProductOrder(ProductOrderCreate body, HttpServletRequest request);

    ProductOrderMessage listProductOrders(HttpServletRequest request);

    ProductOrderMessage deleteProductOrder(String id,HttpServletRequest request);

}
