package com.kumoco.productorder.util;

import com.kumoco.productorder.message.CancelProductOrderMessage;
import com.kumoco.productorder.message.ProductOrderMessage;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.HYPHEN;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_INSTANCE_ID;

@Log4j2
public class CustomUtils {

    public static Object validateResponse(final CancelProductOrderMessage cancelProductOrderMessage ) {

        return null == cancelProductOrderMessage.getError() ? cancelProductOrderMessage.getCancelProductOrder() : cancelProductOrderMessage.getError();
    }

    public static Object validateResponse(final ProductOrderMessage productOrderMessage) {

        if (productOrderMessage.getProductOrders().size() == 1) {

            return null == productOrderMessage.getError() ? productOrderMessage.getProductOrders().stream().findFirst() : productOrderMessage.getError();

        } else {

            return null == productOrderMessage.getError() ? productOrderMessage.getProductOrders() : productOrderMessage.getError();
        }
    }

    public static Object validateListResponse(final ProductOrderMessage productOrderMessage) {

        return null == productOrderMessage.getError() ? productOrderMessage.getProductOrders() : productOrderMessage.getError();
    }

    public static String getUniqueRequestId(final String requestId) {

        return requestId + HYPHEN + UUID.randomUUID();
    }

    public static String createJsonMessageFromMap(final Map<String, Object> map) {

        JSONObject json = new JSONObject();
        map.entrySet().stream().forEach(e ->
        {
            try {
                json.put(String.valueOf(e.getKey()), e.getValue());
            } catch (JSONException ex) {
                log.debug("Failed to generate json object for " + e + ", Rest request - ");
            }
        });
        log.debug("Generated JSON - length:" + json.length() + ", request object : " + json);

        return log.traceExit(String.valueOf(json));
    }

    public static void addInstanceIdToResponse(final CancelProductOrderMessage cancelProductOrderMessage, final HttpServletResponse response) {

        if (cancelProductOrderMessage.getInstanceId() != null) {

            response.addHeader(X_INSTANCE_ID, cancelProductOrderMessage.getInstanceId());
        }
    }

    public static void addInstanceIdToResponse(final ProductOrderMessage productOrderMessage, final HttpServletResponse response) {

        if (productOrderMessage.getInstanceId() != null) {

            response.addHeader(X_INSTANCE_ID, productOrderMessage.getInstanceId());
        }
    }
}
