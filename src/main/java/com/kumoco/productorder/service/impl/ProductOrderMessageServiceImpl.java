package com.kumoco.productorder.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumoco.productorder.exception.TimeoutException;
import com.kumoco.productorder.message.ProductOrderMessage;
import com.kumoco.productorder.model.tmf.ProductOrder;
import com.kumoco.productorder.model.tmf.ProductOrderCreate;
import com.kumoco.productorder.model.tmf.ProductOrderUpdate;
import com.kumoco.productorder.model.tmf.custom.RequestType;
import com.kumoco.productorder.service.ProductOrderMessageService;
import com.kumoco.productorder.util.ObjectMapperHelper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.AUTHORIZATION_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.BPI_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.COOKIES;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.CUSTOMER_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.CUSTOMER_INFO;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.FIELDS;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.FILTER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.LOCATION_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.LOCATION_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.LOCATION_ID_PARAMETER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ORDER_ITEM_REQUEST_TYPE_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.PRODUCT_ID_PARAMETER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.PRODUCT_ORDER_CREATE;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.PRODUCT_ORDER_UPDATE;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.REQUEST_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.REQUEST_TYPE;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_BSS;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_CUSTOMER_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_EDA;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_ERP;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_ID_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_TYPE_BSS_DISCONNECT;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_TYPE_BSS_EXISTING;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_TYPE_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_TYPE_BSS_SUBMIT;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_TYPE_BSS_UNSUBMITTED;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SUBSCRIBER_OCS_ID_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SUBSCRIPTION_OCS;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SUBSCRIPTION_OCS_PAID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SYSTEM_INTENT_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.VF_BILLINGACCOUNTID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.VOUCHER_ORDER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_APPLICATION_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_CORRELATION_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_INSTANCE_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_REQUEST_ID;
import static com.kumoco.productorder.util.CustomUtils.createJsonMessageFromMap;
import static com.kumoco.productorder.util.CustomUtils.getUniqueRequestId;
import static com.kumoco.productorder.util.RequestHeadersHelper.isBssWorkerRequest;
import static com.kumoco.productorder.util.RequestHeadersHelper.parseAuthHeaderIfNeeded;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

@Component
@Log4j2
public class ProductOrderMessageServiceImpl implements ProductOrderMessageService {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperHelper.setUpObjectMapper();

    @Value("${activemq.request-queue}")
    String requestQueue;

    @Value("${activemq.response-queue}")
    String responseQueue;

    @Value("${selector.request-id-filter}")
    String requestIdFilter;

    private final ConsumerTemplate consumerTemplate;

    private final ProducerTemplate producerTemplate;

    private Map<String, Object> requestHeaderMap;

    private Map<String, String> cookieMultiValueMap;

    public ProductOrderMessageServiceImpl(final ConsumerTemplate consumerTemplate, final ProducerTemplate producerTemplate) {
        this.consumerTemplate = consumerTemplate;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public ProductOrderMessage createProductOrder(final ProductOrderCreate body, final HttpServletRequest request) {


        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);

        final Map<String, Object> messageBodyRequestMap = getProductOrderJMSBodyMap(request, Optional.empty());
        messageBodyRequestMap.put(PRODUCT_ORDER_CREATE, new JSONObject(body));

        final String jsonMessage = createJsonMessageFromMap(messageBodyRequestMap);
        requestHeaderMap = populateJMSHeadersMap(request);

        switch (systemIntent) {

            case SALES_ORDER_BSS:

                requestHeaderMap.put(SALES_ORDER_TYPE_BSS_HEADER, request.getHeader(SALES_ORDER_TYPE_BSS_HEADER));

                return processMessageRequest(jsonMessage, requestHeaderMap, messageBodyRequestMap);

            case SUBSCRIPTION_OCS:
            case SALES_ORDER_EDA:
            case SALES_ORDER_ERP:
            case VOUCHER_ORDER:

                return processMessageRequest(jsonMessage, requestHeaderMap, messageBodyRequestMap);

            default:
                log.error("Not a valid request for BSS and OCS");
                return new ProductOrderMessage();
        }
    }

    @Override
    public ProductOrderMessage listProductOrders(final HttpServletRequest request) {


        final Map<String, Object> messageBodyRequestMap = getProductOrderJMSBodyMap(request, Optional.empty());
        messageBodyRequestMap.put(SUBSCRIBER_OCS_ID_HEADER, request.getHeader(SUBSCRIBER_OCS_ID_HEADER));

        final String jsonMessage = createJsonMessageFromMap(messageBodyRequestMap);
        final String salesOrderType = request.getHeader(SALES_ORDER_TYPE_BSS_HEADER);

        requestHeaderMap = new HashMap<>();

        requestHeaderMap.put(SYSTEM_INTENT_HEADER, request.getHeader(SYSTEM_INTENT_HEADER));
        requestHeaderMap.put(REQUEST_TYPE, RequestType.LIST.toString());

        if (SALES_ORDER_TYPE_BSS_UNSUBMITTED.equals(salesOrderType)) {
            requestHeaderMap.put(SALES_ORDER_TYPE_BSS_HEADER, salesOrderType);
        }

        return processMessageRequest(jsonMessage, requestHeaderMap, messageBodyRequestMap);
    }

    @Override
    public ProductOrderMessage deleteProductOrder(final String id, final HttpServletRequest request) {

        final Map<String, Object> messageBodyRequestMap = getProductOrderJMSBodyMap(request, Optional.of(id));
        final String jsonMessage = createJsonMessageFromMap(messageBodyRequestMap);

        requestHeaderMap = populateJMSHeadersMap(request);
        requestHeaderMap.put(SUBSCRIBER_OCS_ID_HEADER, request.getHeader(SUBSCRIBER_OCS_ID_HEADER));

        return processMessageRequest(jsonMessage, requestHeaderMap, messageBodyRequestMap);
    }

    @Override
    public ProductOrderMessage retrieveProductOrder(final String id, final String fields, final HttpServletRequest request) {


        final Map<String, Object> messageBodyRequestMap = getProductOrderJMSBodyMap(request, Optional.of(id));

        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);
        final String jsonMessage = createJsonMessageFromMap(messageBodyRequestMap);

        requestHeaderMap = populateJMSHeadersMap(request);

        switch (systemIntent) {
            case SALES_ORDER_BSS:

                requestHeaderMap.put(SALES_ORDER_CUSTOMER_BSS_HEADER, request.getHeader(SALES_ORDER_CUSTOMER_BSS_HEADER));
                break;

            case SUBSCRIPTION_OCS:

                requestHeaderMap.put(SUBSCRIBER_OCS_ID_HEADER, request.getHeader(SUBSCRIBER_OCS_ID_HEADER));
                break;

            default:
                log.error("Not a valid request for BSS and OCS");
                return new ProductOrderMessage();
        }

        return processMessageRequest(jsonMessage, requestHeaderMap, messageBodyRequestMap);
    }

    @Override
    public ProductOrderMessage patchProductOrder(final ProductOrderUpdate body, final String id, final HttpServletRequest request) {

        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);
        final Map<String, Object> messageBodyRequestMap = getProductOrderJMSBodyMap(request, Optional.ofNullable(id));

        messageBodyRequestMap.put(PRODUCT_ORDER_UPDATE, new JSONObject(body));
        messageBodyRequestMap.put(ID,id);

        final String jsonMessage = createJsonMessageFromMap(messageBodyRequestMap);
        requestHeaderMap = populateJMSHeadersMap(request);

        switch (systemIntent) {

            case SALES_ORDER_BSS:

                requestHeaderMap.put(SALES_ORDER_TYPE_BSS_HEADER, request.getHeader(SALES_ORDER_TYPE_BSS_HEADER));
                requestHeaderMap.put(SALES_ORDER_CUSTOMER_BSS_HEADER, request.getHeader(SALES_ORDER_CUSTOMER_BSS_HEADER));
                requestHeaderMap.put(ORDER_ITEM_REQUEST_TYPE_BSS_HEADER, request.getHeader(ORDER_ITEM_REQUEST_TYPE_BSS_HEADER));
                requestHeaderMap.put(LOCATION_BSS_HEADER, request.getHeader(LOCATION_BSS_HEADER));
                requestHeaderMap.put(SALES_ORDER_ID_BSS_HEADER, request.getHeader(SALES_ORDER_ID_BSS_HEADER));
                requestHeaderMap.put(VF_BILLINGACCOUNTID, request.getHeader(VF_BILLINGACCOUNTID));

                return processMessageRequest(jsonMessage, requestHeaderMap, messageBodyRequestMap);

            case SUBSCRIPTION_OCS:

                requestHeaderMap.put(SUBSCRIBER_OCS_ID_HEADER, request.getHeader(SUBSCRIBER_OCS_ID_HEADER));

                return processMessageRequest(jsonMessage, requestHeaderMap, messageBodyRequestMap);

            default:
                log.error("Not a valid request for BSS and OCS");
                return new ProductOrderMessage();
        }
    }

    @SneakyThrows
    private ProductOrderMessage processMessageRequest(final String jsonMessage, final Map<String, Object> headersMap, final Map<String, Object> paramMap) {

        final LocalTime request = LocalTime.now();

        final String filter = getUniqueRequestId(paramMap.get(X_REQUEST_ID).toString());
        final String messageFilter = requestIdFilter.replace(REQUEST_ID, filter);
        headersMap.put(FILTER, filter);

        log.info("Received client request: [{}] ## headers [{}]", paramMap, headersMap);

        producerTemplate.sendBodyAndHeaders(requestQueue, jsonMessage, headersMap);

        final byte[] responseMessage = Optional.ofNullable(consumerTemplate.receiveBody(responseQueue + messageFilter, 25000L, byte[].class))
                .orElseThrow(() -> new TimeoutException(headersMap));

        log.info("The worker responded in [{}] milliseconds for the request with ## headers [{}] and message ID [{}]", ChronoUnit.MILLIS.between(request, LocalTime.now()), paramMap, filter);

        final ProductOrderMessage productOrderMessage = getProductOrderMessageFromJson(responseMessage);

        return productOrderMessage;
    }

    private Map<String, Object> getProductOrderJMSBodyMap(final HttpServletRequest request, final Optional<String> id) {

        final RequestType requestType = RequestType.valueOf(request.getMethod());
        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);

        final Map<String, Object> map = getJMSBodyMap(request);

       if (isBssWorkerRequest(request)) {

            map.put(COOKIES, addCookies(request, map));
            map.put(CUSTOMER_INFO, request.getHeader(CUSTOMER_INFO));
        }

        switch (systemIntent) {

            case SALES_ORDER_BSS:

                switch (requestType) {

                    case GET:

                        if (id.isPresent()) {

                           map.put(ID, id.get());
                           map.put(FIELDS, request.getParameter(FIELDS));

                        } else {
                            map.put(CUSTOMER_ID, request.getHeader(SALES_ORDER_CUSTOMER_BSS_HEADER));
                        }
                        break;

                    case PATCH:

                        map.put(SALES_ORDER_ID, id.get());
                        break;

                    case POST:

                        getPostRequestTypeHeaders(request, map);
                        map.put(COOKIES, addCookies(request, map));
                        break;
                }
            break;
            case SUBSCRIPTION_OCS:

                switch (requestType) {

                    case POST:

                        map.put(SUBSCRIBER_OCS_ID_HEADER, request.getHeader(SUBSCRIBER_OCS_ID_HEADER));
                        map.put(SUBSCRIPTION_OCS_PAID, request.getHeader(SUBSCRIPTION_OCS_PAID));
                        break;
                    case PATCH:

                        map.put(SUBSCRIBER_OCS_ID_HEADER, request.getHeader(SUBSCRIBER_OCS_ID_HEADER));
                        break;
                    case GET:

                        if (id.isPresent()) {
                            map.put(SUBSCRIBER_OCS_ID_HEADER, request.getHeader(SUBSCRIBER_OCS_ID_HEADER));
                        }
                        break;

                    case DELETE:

                        map.put(SUBSCRIBER_OCS_ID_HEADER, request.getHeader(SUBSCRIBER_OCS_ID_HEADER));
                        map.put(SUBSCRIPTION_OCS_PAID, id);
                        map.put(ID, id.get());
                        break;
                }
        }
        return map;
    }

    private void getPostRequestTypeHeaders(final HttpServletRequest request, final Map<String, Object> map) {

        final String bssSalesOrderType = request.getHeader(SALES_ORDER_TYPE_BSS_HEADER);

        switch(bssSalesOrderType) {

            case (SALES_ORDER_TYPE_BSS_EXISTING):

                getLocationIdAndCustomerIdFromRequest(request, map);
                getProductIdFromRequest(request, map);
                break;

            case (SALES_ORDER_TYPE_BSS_SUBMIT):

                getSalesOrderIdAndCustomerIdFromRequest(request, map);
                break;

            case (SALES_ORDER_TYPE_BSS_DISCONNECT):

                map.put(SALES_ORDER_ID_BSS_HEADER, request.getHeader(SALES_ORDER_ID_BSS_HEADER));
                break;

            default:
                log.error("The error has occurred for BSS Sales Order Type");
        }

    }

    private ProductOrderMessage getProductOrderMessageFromJson(final byte[] message) {

        try{
            final ProductOrderMessage productOrderMessage = OBJECT_MAPPER.readValue(message, ProductOrderMessage.class);
            productOrderMessage.setCorrelationId(productOrderMessage.getCorrelationId());
            productOrderMessage.setInstanceId(productOrderMessage.getInstanceId());

            checkIfProductOrderListIsEmpty(productOrderMessage);

            return productOrderMessage;
        } catch (IOException e) {
            log.error("Unable to process worker response: [{}] ", message, e);
            throw new ResponseStatusException(PRECONDITION_FAILED, "Unable to process worker message", e);
        }

    }


    private Map<String, Object> populateJMSHeadersMap(final HttpServletRequest request) {

        final Map<String, Object> headersMap = new HashMap<>();

        headersMap.put(SYSTEM_INTENT_HEADER, request.getHeader(SYSTEM_INTENT_HEADER));
        headersMap.put(REQUEST_TYPE, RequestType.valueOf(request.getMethod()).toString());

        return headersMap;
    }

    private Map<String, Object> getJMSBodyMap(final HttpServletRequest request) {

        final Map<String, Object> map = new HashMap<>();

        map.put(AUTHORIZATION_HEADER, parseAuthHeaderIfNeeded(request));
        map.put(X_APPLICATION_ID, request.getHeader(X_APPLICATION_ID));
        map.put(X_CORRELATION_ID, request.getHeader(X_CORRELATION_ID));
        map.put(X_REQUEST_ID, request.getHeader(X_REQUEST_ID));
        map.put(X_INSTANCE_ID, request.getHeader(X_INSTANCE_ID));

        return map;
    }

    private void getProductIdFromRequest(final HttpServletRequest request, final Map<String, Object> map){

        final String bpiId = request.getParameter(PRODUCT_ID_PARAMETER);

        if (null != bpiId) {
            map.put(BPI_ID, bpiId);
        }
    }

    private void getLocationIdAndCustomerIdFromRequest(final HttpServletRequest request, final Map<String, Object> map){

        final String locationId = request.getParameter(LOCATION_ID_PARAMETER);
        final String customerId = request.getHeader(SALES_ORDER_CUSTOMER_BSS_HEADER);

        if (null != locationId && null != customerId) {

            map.put(LOCATION_ID, locationId);
            map.put(CUSTOMER_ID, customerId);

        } else {
            log.error("The request for create product order is wrong. It misses either locationId or customerId or both: " +
                    locationId + ", " + customerId);
        }
    }

    private void getSalesOrderIdAndCustomerIdFromRequest(final HttpServletRequest request, final Map<String, Object> map){

        final String salesOrderId = request.getHeader(SALES_ORDER_ID_BSS_HEADER);
        final String customerId = request.getHeader(SALES_ORDER_CUSTOMER_BSS_HEADER);

        if (null != salesOrderId && null != customerId) {

            map.put(SALES_ORDER_ID, salesOrderId);
            map.put(CUSTOMER_ID, customerId);

        } else {
            log.error("The request for create product order is wrong. It misses either salesOrderId or customerId or both: " +
                    salesOrderId + ", " + customerId);
        }
    }

    private Object addCookies(final HttpServletRequest request, final Map<String, Object> map) {

        final Map<String, String> cookies = new HashMap<>();

        if(request.getCookies() != null) {
            for (final Cookie cookie : request.getCookies()) {
                cookies.put(cookie.getName(), cookie.getValue());
            }
        }

        return cookies;
    }

    private void checkIfProductOrderListIsEmpty(final ProductOrderMessage productOrderMessage) {

        if (null == productOrderMessage.getProductOrders()) {
            productOrderMessage.setProductOrders(Collections.singletonList(new ProductOrder()));
        }
    }
}
