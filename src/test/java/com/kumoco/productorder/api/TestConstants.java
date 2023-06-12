package com.kumoco.productorder.api;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumoco.productorder.model.tmf.Error;
import com.kumoco.productorder.model.tmf.ErrorItem;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class TestConstants {

    public static final String  REQUEST_QUEUE = "activemq://productorder-request-queue";

    public static final String RESPONSE_QUEUE = "activemq://productorder-response-queue";

    public static final String VF_SYSTEM_INTENT = "vf-system-intent";

    public static final String AUTHORIZATION = "Authorization";

    public static final String VF_SUBSCRIBER_ID = "vf-subscriber-id";

    public static final String VF_CUSTOMER_ID = "vf-customer-id";

    public static final String VF_SALES_ORDER_TYPE = "vf-sales-order-type";

    public static final String VF_LOCATION_ID = "vf-location-id";

    public static final String VF_BPI_ID = "bpiId";

    public static final String VF_SALES_ORDER_ID = "vf-sales-order-id";

    public static final String VF_ORDERITEM = "vf-orderitem";

    public static final String VF_BILLINGACCOUNTID = "vf-billingaccountid";
    public static final String REQUEST_TYPE = "requestType";
    public static final String X_CORRELATION_ID = "X-Correlation-ID";
    public static final String X_APPLICATION_ID = "X-Application-ID";

    public static Error getError() {

        return new com.kumoco.productorder.model.tmf.Error()
                .status("badstatus")
                .code("badcode")
                .message("badmsg")
                .errorItem(singletonList(new ErrorItem().code("baditemcode")));
    }

    public static  void assertErrorBodyReturned(final MvcResult result, final ObjectMapper objectMapper) throws com.fasterxml.jackson.core.JsonProcessingException, UnsupportedEncodingException {

        final com.kumoco.productorder.model.tmf.Error error = objectMapper.readValue(result.getResponse().getContentAsString(), com.kumoco.productorder.model.tmf.Error.class);
        assertThat(error).isEqualTo(getError());
    }

    public static final Long LONG_VALUE = 11111111111111111L;

    public static final ZonedDateTime START_DATE_TIME = ZonedDateTime.parse("2021-04-13T04:44:15Z");
    public static final ZonedDateTime END_DATE_TIME = ZonedDateTime.parse("2021-07-13T04:44:14Z");

    public static final String ACTION_ADD = "add";
    
    public static final String FILTER = "filter";


}
