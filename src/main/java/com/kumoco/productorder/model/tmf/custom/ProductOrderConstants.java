package com.kumoco.productorder.model.tmf.custom;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductOrderConstants {

    public static final String SYSTEM_INTENT_HEADER = "vf-system-intent";

    public static final String SALES_ORDER_BSS = "salesorder";

    public static final String SUBSCRIPTION_OCS = "subscription";

    public static final String SALES_ORDER_EDA = "provisioning";

    public static final String SALES_ORDER_ERP= "salesrecord";

    public static final String SALES_ORDER_TYPE_BSS_HEADER = "vf-sales-order-type";

    public static final String SALES_ORDER_CUSTOMER_BSS_HEADER = "vf-customer-id";

    public static final String ORDER_ITEM_REQUEST_TYPE_BSS_HEADER = "vf-orderitem";

    public static final String LOCATION_BSS_HEADER = "vf-location-id";

    public static final String SALES_ORDER_TYPE_BSS_SUBMIT = "submit";

    public static final String SALES_ORDER_TYPE_BSS_UNSUBMITTED = "unsubmitted";

    public static final String SALES_ORDER_TYPE_BSS_DISCONNECT = "disconnect";

    public static final String LOCATION_ID_PARAMETER = "productOrder.productOrderItem.product.place.id";

    public static final String PRODUCT_ID_PARAMETER = "productOrder.productOrderItem.product.id";

    public static final String SALES_ORDER_TYPE_BSS_EXISTING = "existing";

    public static final String SALES_ORDER_ID_BSS_HEADER = "vf-sales-order-id";

    public static final String ACCEPT = "Accept";

    public static final String APPLICATION_JSON = "application/json";

    public static final String REQUEST_TYPE = "requestType";

    public static final String SUBSCRIBER_OCS_ID_HEADER = "vf-subscriber-id";

    public static final String SUBSCRIPTION_OCS_PAID = "vf-subscription-paid";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String CUSTOMER_INFO = "Customer-Info";

    public static final String X_REQUEST_ID = "X-Request-ID";

    public static final String X_CORRELATION_ID = "X-Correlation-ID";

    public static final String X_APPLICATION_ID = "X-Application-ID";

    public static final String ID = "id";

    public static final String FIELDS = "fields";

    public static final String PRODUCT_ORDER_CREATE = "productOrderCreate";

    public static final String PRODUCT_ORDER_UPDATE = "productOrderUpdate";

    public static final String LOCATION_ID = "locationId";

    public static final String CUSTOMER_ID = "customerId";

    public static final String COOKIES = "cookies";

    public static final String SALES_ORDER_ID = "salesOrderId";

    public static final String PRODUCT_ORDER_CANCEL = "productOrderCancel";

    public static final String SALES_ORDER_TYPE_BSS_ANONYMOUS = "anonymous";

    public static final String REQUEST_ID = "{requestId}";

    public static final String VOUCHER_ORDER = "voucherorder";

    public static final String VF_BILLINGACCOUNTID = "vf-billingaccountid";

    public static final String CHANGEORDER_ITEM = "changeorderItem";

    public static final String BPI_ID = "bpiId";

    public static final String BEARER = "Bearer";

    public static final String EXTERNAL_AUTHORIZATION = "externalAuthorization";

    public static final String REGEX_WHITESPACE = "\\s+";

    public static final String REGEX_PERIOD = "\\.";
    
    public static final String X_INSTANCE_ID = "X-Instance-ID";
    
    public static final String FILTER = "filter";

    public static final String HYPHEN = "-";

    public static final String ERROR_TIMEOUT = "Timeout";

    public static final String ERROR_CODE_001 = "001";

    public static final String ERROR_BAD_REQUEST = "Invalid request";

    public static final String ERROR_CODE_002 = "002";

    public static final String ERROR_NOT_ACCEPTABLE = "406 Not Acceptable";

    public static final String ERROR_CODE_003 = "003";

    public static final String COMMA_SPACE = ", ";

    public static final String HYPHEN_SPACE = " - ";
}
