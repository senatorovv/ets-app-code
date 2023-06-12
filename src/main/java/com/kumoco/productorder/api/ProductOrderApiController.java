package com.kumoco.productorder.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumoco.productorder.message.ProductOrderMessage;
import com.kumoco.productorder.model.tmf.ProductOrderCreate;
import com.kumoco.productorder.model.tmf.ProductOrderUpdate;
import com.kumoco.productorder.service.ProductOrderMessageService;
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

import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.LOCATION_ID_PARAMETER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ORDER_ITEM_REQUEST_TYPE_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.PRODUCT_ID_PARAMETER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_CUSTOMER_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_ID_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_TYPE_BSS_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SALES_ORDER_TYPE_BSS_UNSUBMITTED;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SUBSCRIBER_OCS_ID_HEADER;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.SYSTEM_INTENT_HEADER;
import static com.kumoco.productorder.util.CustomUtils.addInstanceIdToResponse;
import static com.kumoco.productorder.util.CustomUtils.validateListResponse;
import static com.kumoco.productorder.util.CustomUtils.validateResponse;
import static com.kumoco.productorder.util.RequestHeadersHelper.addMandatoryAPIGWHeadersToResponse;
import static com.kumoco.productorder.util.RequestHeadersHelper.getBadRequestResponse;
import static com.kumoco.productorder.util.RequestHeadersHelper.getFailedDependencyResponse;
import static com.kumoco.productorder.util.RequestHeadersHelper.hasAllMandatoryAPIGWHeaders;
import static com.kumoco.productorder.util.RequestHeadersHelper.isBssSalesOrderIntent;
import static com.kumoco.productorder.util.RequestHeadersHelper.isEdaIntent;
import static com.kumoco.productorder.util.RequestHeadersHelper.isErpIntent;
import static com.kumoco.productorder.util.RequestHeadersHelper.isOcsIntent;
import static com.kumoco.productorder.util.RequestHeadersHelper.isPatchBillingAccountRequest;
import static com.kumoco.productorder.util.RequestHeadersHelper.isPatchChangeOfferingRequest;
import static com.kumoco.productorder.util.RequestHeadersHelper.isValidAPIGWRequest;
import static com.kumoco.productorder.util.RequestHeadersHelper.isValidCreateProductOrderRequestBSS;
import static com.kumoco.productorder.util.RequestHeadersHelper.isVoucherOrderIntent;
import static java.util.Objects.nonNull;

@javax.annotation.Generated(value = "com.kumoco.productorder.codegen.v3.generators.java.SpringCodegen", date = "2020-06-01T18:11:48.790+01:00[Europe/London]")
@Controller
@Log4j2
public class ProductOrderApiController implements ProductOrderApi {

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private ProductOrderMessageService productOrderMessageService;

    public ProductOrderApiController(ObjectMapper objectMapper, HttpServletRequest request, ProductOrderMessageService productOrderMessageService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.productOrderMessageService = productOrderMessageService;
    }

    public ResponseEntity createProductOrder(@ApiParam(value = "The ProductOrder to be created" ,required=true ) @Valid @RequestBody ProductOrderCreate body,
                                                           @ApiParam(value = "Location identifier") @Valid @RequestParam(value = LOCATION_ID_PARAMETER,
                                                                   required = false) String locationId,
                                                           @ApiParam(value = "Product identifier") @Valid @RequestParam(value = PRODUCT_ID_PARAMETER,
                                                                   required = false) String bpiId,
                                                           HttpServletResponse response) {

        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);
        final String subscriberId = request.getHeader(SUBSCRIBER_OCS_ID_HEADER);

        if ((isValidAPIGWRequest(request) && hasAllMandatoryAPIGWHeaders(request)) && ((isValidCreateProductOrderRequestBSS(request, locationId, bpiId))
                || (isOcsIntent(systemIntent) && nonNull(subscriberId))
                || isEdaIntent(systemIntent)
                || isErpIntent(systemIntent)
                || isVoucherOrderIntent(systemIntent))) {

            final ProductOrderMessage productOrderMessage = productOrderMessageService.createProductOrder(body, request);
            addInstanceIdToResponse(productOrderMessage, response);

            return new ResponseEntity(validateResponse(productOrderMessage), addMandatoryAPIGWHeadersToResponse(request), productOrderMessage.getHttpStatus());
        }

        return new ResponseEntity(getBadRequestResponse(request), addMandatoryAPIGWHeadersToResponse(request), HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity deleteProductOrder(@ApiParam(value = "Identifier of the ProductOrder",required=true) @PathVariable("id") String id) {

        final String subscriberId = request.getHeader(SUBSCRIBER_OCS_ID_HEADER);
        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);

        if (isValidAPIGWRequest(request) && (nonNull(subscriberId) && isOcsIntent(systemIntent)) && hasAllMandatoryAPIGWHeaders(request)) {

            final ProductOrderMessage productOrderMessage  = productOrderMessageService.deleteProductOrder(id, request);

            return new ResponseEntity(addMandatoryAPIGWHeadersToResponse(request), productOrderMessage.getHttpStatus());
        }

        return new ResponseEntity(getBadRequestResponse(request), addMandatoryAPIGWHeadersToResponse(request), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity listProductOrder(@ApiParam(value = "Comma-separated properties to be provided in response") @Valid @RequestParam(value = "fields", required = false) String fields
            ,@ApiParam(value = "Requested index for start of resources to be provided in response") @Valid @RequestParam(value = "offset", required = false) Integer offset
            ,@ApiParam(value = "Requested number of resources to be provided in response") @Valid @RequestParam(value = "limit", required = false) Integer limit, HttpServletResponse response) {

        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);
        final String customerId = request.getHeader(SALES_ORDER_CUSTOMER_BSS_HEADER);
        final String salesOrderType = request.getHeader(SALES_ORDER_TYPE_BSS_HEADER);

        final String subscriberId = request.getHeader(SUBSCRIBER_OCS_ID_HEADER);

        if ((isValidAPIGWRequest(request) && hasAllMandatoryAPIGWHeaders(request))
                && (nonNull(customerId) && (isBssSalesOrderIntent(systemIntent) || (SALES_ORDER_TYPE_BSS_UNSUBMITTED.equals(salesOrderType)))
                || (nonNull(subscriberId) && isOcsIntent(systemIntent)))) {

            final ProductOrderMessage productOrderMessage = productOrderMessageService.listProductOrders(request);
            addInstanceIdToResponse(productOrderMessage, response);

            return new ResponseEntity(validateListResponse(productOrderMessage), addMandatoryAPIGWHeadersToResponse(request), productOrderMessage.getHttpStatus());
        }

        return new ResponseEntity(getBadRequestResponse(request), addMandatoryAPIGWHeadersToResponse(request), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity patchProductOrder(@ApiParam(value = "The ProductOrder to be updated" ,required=true )  @Valid @RequestBody ProductOrderUpdate body
            ,@ApiParam(value = "Identifier of the ProductOrder",required=true) @PathVariable("id") String id, HttpServletResponse response) {

        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);
        final String salesOrderOrderItem = request.getHeader(ORDER_ITEM_REQUEST_TYPE_BSS_HEADER);
        final String customerId = request.getHeader(SALES_ORDER_CUSTOMER_BSS_HEADER);
        final String subscriberId = request.getHeader(SUBSCRIBER_OCS_ID_HEADER);

        if ((isValidAPIGWRequest(request) && hasAllMandatoryAPIGWHeaders(request))
                && ((isBssSalesOrderIntent(systemIntent) && nonNull(customerId) && nonNull(salesOrderOrderItem))
                || (isOcsIntent(systemIntent) && nonNull(subscriberId))
                || isPatchBillingAccountRequest(systemIntent, salesOrderOrderItem, request)
                || isPatchChangeOfferingRequest(systemIntent, request))) {

            final ProductOrderMessage productOrderMessage = productOrderMessageService.patchProductOrder(body, id, request);
            addInstanceIdToResponse(productOrderMessage, response);

            return new ResponseEntity(validateResponse(productOrderMessage), addMandatoryAPIGWHeadersToResponse(request), productOrderMessage.getHttpStatus());
        }

        return new ResponseEntity(getFailedDependencyResponse(request), addMandatoryAPIGWHeadersToResponse(request), HttpStatus.FAILED_DEPENDENCY);
    }

    public ResponseEntity retrieveProductOrder(@ApiParam(value = "Identifier of the ProductOrder",required=true) @PathVariable("id") String id
            ,@ApiParam(value = "Comma-separated properties to provide in response") @Valid @RequestParam(value = "fields", required = false) String fields, HttpServletResponse response) {

        final String systemIntent = request.getHeader(SYSTEM_INTENT_HEADER);
        final String customerId = request.getHeader(SALES_ORDER_ID_BSS_HEADER);

        if ((isValidAPIGWRequest(request) && hasAllMandatoryAPIGWHeaders(request))
                && (isBssSalesOrderIntent(systemIntent) || nonNull(customerId) || isOcsIntent(systemIntent))) {

            final ProductOrderMessage productOrderMessage = productOrderMessageService.retrieveProductOrder(id, fields, request);
            addInstanceIdToResponse(productOrderMessage, response);

            return new ResponseEntity(validateResponse(productOrderMessage), addMandatoryAPIGWHeadersToResponse(request), productOrderMessage.getHttpStatus());
        }

        return new ResponseEntity(getBadRequestResponse(request), addMandatoryAPIGWHeadersToResponse(request), HttpStatus.BAD_REQUEST);
    }
}
