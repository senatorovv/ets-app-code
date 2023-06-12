package com.kumoco.productorder.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumoco.productorder.model.tmf.Error;
import com.kumoco.productorder.model.tmf.ErrorItem;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;

import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ACCEPT;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.APPLICATION_JSON;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.AUTHORIZATION_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.BEARER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.CHANGEORDER_ITEM;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.COMMA_SPACE;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ERROR_BAD_REQUEST;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ERROR_CODE_002;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ERROR_CODE_003;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ERROR_NOT_ACCEPTABLE;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.EXTERNAL_AUTHORIZATION;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.HYPHEN_SPACE;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ORDER_ITEM_REQUEST_TYPE_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.REGEX_PERIOD;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.REGEX_WHITESPACE;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_BSS;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_CUSTOMER_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_EDA;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_ERP;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_ID_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_TYPE_BSS_ANONYMOUS;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_TYPE_BSS_DISCONNECT;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_TYPE_BSS_EXISTING;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_TYPE_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_TYPE_BSS_SUBMIT;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SUBSCRIPTION_OCS;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SYSTEM_INTENT_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.VF_BILLINGACCOUNTID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.VOUCHER_ORDER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_APPLICATION_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_CORRELATION_ID;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_REQUEST_ID;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.isEmpty;

@Log4j2
@UtilityClass
public class RequestHeadersHelper {

    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private static final ObjectMapper mapper = ObjectMapperHelper.setUpObjectMapper();

    public static boolean isValidAPIGWRequest(final HttpServletRequest request) {

        final String accept = request.getHeader(ACCEPT);
        final String authorization = request.getHeader(AUTHORIZATION_HEADER);

        return APPLICATION_JSON.equals(accept) && null != authorization;
    }

    public static Boolean isValidCreateProductOrderRequestBSS(final HttpServletRequest request, final String locationId, final String bpiId) {

        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);

        return isBssSalesOrderIntent(systemIntent) && isValidCreateProductOrderSalesOrderType(request, locationId, bpiId);
    }

    private static boolean isValidCreateProductOrderSalesOrderType(final HttpServletRequest request, final String locationId, final String bpiId) {

        final String salesOrderId = request.getHeader(SALES_ORDER_ID_BSS_HEADER);
        final String salesOrderType = request.getHeader(SALES_ORDER_TYPE_BSS_HEADER);
        final String customerId = request.getHeader(SALES_ORDER_CUSTOMER_BSS_HEADER);

        return SALES_ORDER_TYPE_BSS_ANONYMOUS.equals(salesOrderType)
                || (SALES_ORDER_TYPE_BSS_EXISTING.equals(salesOrderType) && null != customerId && null != locationId)
                || (SALES_ORDER_TYPE_BSS_SUBMIT.equals(salesOrderType) && null != customerId && nonNull(salesOrderId))
                || ((SALES_ORDER_TYPE_BSS_DISCONNECT).equals(salesOrderType) && null != salesOrderId);
    }

    public static boolean isPatchBillingAccountRequest(final String systemIntent, final String salesOrderOrderItem, final HttpServletRequest request) {

        final String salesOrderId = request.getHeader(SALES_ORDER_ID_BSS_HEADER);
        final String billingAccountId = request.getHeader(VF_BILLINGACCOUNTID);

        return systemIntent.equals(SALES_ORDER_BSS) && nonNull(salesOrderOrderItem) && nonNull(salesOrderId) && nonNull(billingAccountId);
    }

    public static boolean isPatchChangeOfferingRequest(final String systemIntent, final HttpServletRequest request) {

        final String orderItem = request.getHeader(ORDER_ITEM_REQUEST_TYPE_BSS_HEADER);

        return SALES_ORDER_BSS.equals(systemIntent) && CHANGEORDER_ITEM.equals(orderItem);
    }

    public static boolean isBssSalesOrderIntent(final String systemIntent) {

        return SALES_ORDER_BSS.equals(systemIntent);
    }

    public static boolean isOcsIntent(final String systemIntent) {
        return SUBSCRIPTION_OCS.equals(systemIntent);
    }

    public static boolean isEdaIntent(final String systemIntent) {
        return SALES_ORDER_EDA.equals(systemIntent);
    }

    public static boolean isErpIntent(final String systemIntent) {

        return SALES_ORDER_ERP.equals(systemIntent);
    }

    public static boolean isVoucherOrderIntent(final String systemIntent) {

        return VOUCHER_ORDER.equals(systemIntent);
    }

    public static boolean isBssWorkerRequest(final HttpServletRequest request) {

        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);

        return isBssSalesOrderIntent(systemIntent) || isVoucherOrderIntent(systemIntent);
    }

    public static String parseJwtToken(String authHeader) {

        if (!isEmpty(authHeader) && authHeader.startsWith(BEARER)) {

            String token = authHeader.split(REGEX_WHITESPACE)[1];
            String[] chunks = token.split(REGEX_PERIOD);

            if (chunks.length >= 2) {

                log.debug("Found JWT token with headers etc - extracting embedded token");
                final String payload = new String(DECODER.decode(chunks[1]));
                JSONObject jsonObject = new JSONObject(payload);

                // If missing embedded token within the JSON token, just return original token
                return jsonObject.optString(EXTERNAL_AUTHORIZATION, authHeader);
            }
        }

        return authHeader;
    }

    public static String parseAuthHeaderIfNeeded(HttpServletRequest request) {

        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);

        return isErpIntent(systemIntent) || isEdaIntent(systemIntent)
                ? parseJwtToken(request.getHeader(AUTHORIZATION_HEADER))
                : request.getHeader(AUTHORIZATION_HEADER);
    }

    public static HttpHeaders addMandatoryAPIGWHeadersToResponse(final HttpServletRequest request) {

        final HttpHeaders headers = new HttpHeaders();

        if (nonNull(request.getHeader(X_REQUEST_ID))) {

            headers.add(X_REQUEST_ID, request.getHeader(X_REQUEST_ID));

        } if (nonNull(request.getHeader(X_APPLICATION_ID))) {

            headers.add(X_APPLICATION_ID, request.getHeader(X_APPLICATION_ID));

        } if (nonNull(request.getHeader(X_CORRELATION_ID))) {

            headers.add(X_CORRELATION_ID, request.getHeader(X_CORRELATION_ID));
        }

        return headers;
    }

    public static boolean hasAllMandatoryAPIGWHeaders(final HttpServletRequest request) {

        return nonNull(request.getHeader(X_REQUEST_ID)) && nonNull(request.getHeader(X_APPLICATION_ID)) && nonNull(request.getHeader(X_CORRELATION_ID)) ? true : false;
    }

    public static Object getBadRequestResponse(final HttpServletRequest request) {

        getAll400Headers(request);

        return new Error()
            .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
            .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
            .errorItem(Collections.singletonList(
                    new ErrorItem()
                            .code(ERROR_CODE_002)
                            .text(ERROR_BAD_REQUEST)));
    }

    private void getAll400Headers(final HttpServletRequest request) {

        final Enumeration<String> headerNames = request.getHeaderNames();

        StringBuilder sb = new StringBuilder();

        while(headerNames.hasMoreElements()) {

            String headerName = headerNames.nextElement();
            sb.append(headerName + HYPHEN_SPACE + request.getHeader(headerName) + COMMA_SPACE);
        }
        log.info("The 400 validation has failed with the given request with headers {} ", sb.deleteCharAt(sb.lastIndexOf(COMMA_SPACE)));
    }

    //Temporary PROD fix due to freeze release
    public static Object getFailedDependencyResponse(final HttpServletRequest request) {

        getAll424Headers(request);

        return new Error()
                .code(String.valueOf(HttpStatus.FAILED_DEPENDENCY.value()))
                .reason(HttpStatus.FAILED_DEPENDENCY.getReasonPhrase())
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
                .errorItem(Collections.singletonList(
                        new ErrorItem()
                                .code(ERROR_CODE_003)
                                .text(ERROR_NOT_ACCEPTABLE)));
    }

//    Duplication @TODO JH
    private void getAll424Headers(final HttpServletRequest request) {

        final Enumeration<String> headerNames = request.getHeaderNames();

        StringBuilder sb = new StringBuilder();

        while(headerNames.hasMoreElements()) {

            String headerName = headerNames.nextElement();
            sb.append(headerName + HYPHEN_SPACE + request.getHeader(headerName) + COMMA_SPACE);
        }
        log.info("The 424 validation has failed with the given request with headers {} ", sb.deleteCharAt(sb.lastIndexOf(COMMA_SPACE)));
    }
}
