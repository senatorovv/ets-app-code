package com.kumoco.productorder.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumoco.productorder.exception.TimeoutException;
import com.kumoco.productorder.message.CancelProductOrderMessage;
import com.kumoco.productorder.model.tmf.CancelProductOrder;
import com.kumoco.productorder.model.tmf.CancelProductOrderCreate;
import com.kumoco.productorder.model.tmf.custom.RequestType;
import com.kumoco.productorder.service.CancelProductOrderMessageService;
import com.kumoco.productorder.util.ObjectMapperHelper;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.AUTHORIZATION_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.CUSTOMER_INFO;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.FILTER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.PRODUCT_ORDER_CANCEL;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.REQUEST_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.REQUEST_TYPE;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_ID_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SYSTEM_INTENT_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_APPLICATION_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_CORRELATION_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_INSTANCE_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_REQUEST_ID;
import static com.kumoco.productorder.util.CustomUtils.createJsonMessageFromMap;
import static com.kumoco.productorder.util.CustomUtils.getUniqueRequestId;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

@Component
@Log4j2
public class CancelProductOrderMessageServiceImpl implements CancelProductOrderMessageService {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperHelper.setUpObjectMapper();

    @Value("${activemq.request-queue}")
    String requestQueue;

    @Value("${activemq.response-queue}")
    String responseQueue;

    @Value("${selector.request-id-filter}")
    String requestIdFilter;

    private final ConsumerTemplate consumerTemplate;

    private final ProducerTemplate producerTemplate;

    public CancelProductOrderMessageServiceImpl(ConsumerTemplate consumerTemplate, ProducerTemplate producerTemplate) {
        this.consumerTemplate = consumerTemplate;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public CancelProductOrderMessage createCancelProductOrder(final CancelProductOrderCreate body, final HttpServletRequest request) {

        Map<String, Object> messageBodyRequestMap = getJMSBodyMap(request);
        messageBodyRequestMap.put(PRODUCT_ORDER_CANCEL, new JSONObject(body));
        messageBodyRequestMap.put(SALES_ORDER_ID_BSS_HEADER, request.getHeader(SALES_ORDER_ID_BSS_HEADER));

        final String jsonMessage = createJsonMessageFromMap(messageBodyRequestMap);
        final byte[] message = processMessageRequest(jsonMessage, getHeadersMap(request), messageBodyRequestMap);

        return getProductOrderMessageToJson(message);
    }

    private byte[] processMessageRequest(final String jsonMessage, final Map<String, Object> headersMap, final Map<String, Object> paramMap)  {

        final LocalTime request = LocalTime.now();

        log.info("Received client request: [{}] ## headers [{}]", paramMap, headersMap);

        final String filter = getUniqueRequestId(paramMap.get(X_REQUEST_ID).toString());
        final String messageFilter = requestIdFilter.replace(REQUEST_ID, filter);
        headersMap.put(FILTER, filter);

        producerTemplate.sendBodyAndHeaders(requestQueue, jsonMessage, headersMap);

        final byte[] responseMessage = Optional.ofNullable(consumerTemplate.receiveBody(responseQueue + messageFilter, 25000L, byte[].class))
                .orElseThrow(() -> new TimeoutException(paramMap));

        log.info("The worker responded in [{}] milliseconds for the request with ## headers [{}] and message ID [{}]", ChronoUnit.MILLIS.between(request, LocalTime.now()), paramMap, filter);

        return responseMessage;
    }

    private CancelProductOrderMessage getProductOrderMessageToJson(final byte[] message) {

        try {
            CancelProductOrderMessage cancelProductOrderMessage = OBJECT_MAPPER.readValue(message, CancelProductOrderMessage.class);
            checkIfResourceListIsEmpty(cancelProductOrderMessage);
            
            return cancelProductOrderMessage;
        } catch (JsonProcessingException e) {
            log.error("Unable to process worker response: [{}] ", message, e);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unable to process worker message", e);
        } catch (IOException e) {
            log.error("Unable to process worker response: [{}] ", message, e);
            throw new ResponseStatusException(PRECONDITION_FAILED, "Unable to process worker message", e);
        }
    }

    private Map<String, Object> getJMSBodyMap(final HttpServletRequest request) {

        Map<String, Object> map = new HashMap<>();

        map.put(CUSTOMER_INFO, request.getHeader(CUSTOMER_INFO));
        map.put(AUTHORIZATION_HEADER, request.getHeader(AUTHORIZATION_HEADER));
        map.put(X_REQUEST_ID, request.getHeader(X_REQUEST_ID));
        map.put(X_CORRELATION_ID, request.getHeader(X_CORRELATION_ID));
        map.put(X_APPLICATION_ID, request.getHeader(X_APPLICATION_ID));
        map.put(X_INSTANCE_ID, request.getHeader(X_INSTANCE_ID));

        return map;
    }

    private Map<String, Object> getHeadersMap(final HttpServletRequest request) {

        Map<String, Object> headersMap = new HashMap<>();
        headersMap.put(REQUEST_TYPE, RequestType.CANCEL.toString());
        headersMap.put(SYSTEM_INTENT_HEADER, request.getHeader(SYSTEM_INTENT_HEADER));

        return headersMap;
    }

    private void checkIfResourceListIsEmpty(CancelProductOrderMessage cancelProductOrderMessage) {

        if (null == cancelProductOrderMessage.getCancelProductOrder()) {
            cancelProductOrderMessage.setCancelProductOrder(new CancelProductOrder());
        }
    }
}

