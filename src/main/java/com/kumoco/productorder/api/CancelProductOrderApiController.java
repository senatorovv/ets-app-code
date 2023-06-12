package com.kumoco.productorder.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumoco.productorder.message.CancelProductOrderMessage;
import com.kumoco.productorder.model.tmf.CancelProductOrder;
import com.kumoco.productorder.model.tmf.CancelProductOrderCreate;
import com.kumoco.productorder.service.CancelProductOrderMessageService;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ACCEPT;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_BSS;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_ID_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SYSTEM_INTENT_HEADER;
import static com.kumoco.productorder.util.CustomUtils.addInstanceIdToResponse;
import static com.kumoco.productorder.util.CustomUtils.validateResponse;
import static com.kumoco.productorder.util.RequestHeadersHelper.addMandatoryAPIGWHeadersToResponse;
import static com.kumoco.productorder.util.RequestHeadersHelper.getBadRequestResponse;
import static com.kumoco.productorder.util.RequestHeadersHelper.hasAllMandatoryAPIGWHeaders;
import static com.kumoco.productorder.util.RequestHeadersHelper.isValidAPIGWRequest;

@javax.annotation.Generated(value = "com.kumoco.productorder.codegen.v3.generators.java.SpringCodegen", date = "2020-06-01T18:11:48.790+01:00[Europe/London]")
@Controller
@Log4j2
public class CancelProductOrderApiController implements CancelProductOrderApi {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final CancelProductOrderMessageService cancelProductOrderMessageService;

    public CancelProductOrderApiController(ObjectMapper objectMapper, HttpServletRequest request,
                                           CancelProductOrderMessageService cancelProductOrderMessageService) {

        this.objectMapper = objectMapper;
        this.request = request;
        this.cancelProductOrderMessageService = cancelProductOrderMessageService;
    }

    public ResponseEntity createCancelProductOrder(@ApiParam(value = "The ProductOrder to be cancelled", required = true)
                                                                 @Valid @RequestBody CancelProductOrderCreate body, HttpServletResponse response) {

        final String accept = request.getHeader(ACCEPT);
        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);
        final String salesOrderId = request.getHeader(SALES_ORDER_ID_BSS_HEADER);

        if (isValidAPIGWRequest(request) && hasAllMandatoryAPIGWHeaders(request) &&
                (systemIntent.equals(SALES_ORDER_BSS) && null != salesOrderId)) {

            final CancelProductOrderMessage cancelProductOrderMessage = cancelProductOrderMessageService.createCancelProductOrder(body, request);
            addInstanceIdToResponse(cancelProductOrderMessage, response);

            return new ResponseEntity(validateResponse(cancelProductOrderMessage), addMandatoryAPIGWHeadersToResponse(request), cancelProductOrderMessage.getHttpStatus());
        }

        return new ResponseEntity(getBadRequestResponse(request), addMandatoryAPIGWHeadersToResponse(request), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<CancelProductOrder>> listCancelProductOrder(@ApiParam(value = "Comma-separated properties to be provided in response") @Valid @RequestParam(value = "fields", required = false) String fields
, @ApiParam(value = "Requested index for start of resources to be provided in response") @Valid @RequestParam(value = "offset", required = false) Integer offset
, @ApiParam(value = "Requested number of resources to be provided in response") @Valid @RequestParam(value = "limit", required = false) Integer limit
) {
        return new ResponseEntity<List<CancelProductOrder>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<CancelProductOrder> retrieveCancelProductOrder(@ApiParam(value = "Identifier of the CancelProductOrder",required=true) @PathVariable("id") String id
,@ApiParam(value = "Comma-separated properties to provide in response") @Valid @RequestParam(value = "fields", required = false) String fields
) {

        return new ResponseEntity<CancelProductOrder>(HttpStatus.NOT_IMPLEMENTED);
    }
}
