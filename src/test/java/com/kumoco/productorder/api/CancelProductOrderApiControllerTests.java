package com.kumoco.productorder.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumoco.productorder.message.CancelProductOrderMessage;
import com.kumoco.productorder.model.tmf.CancelProductOrder;
import com.kumoco.productorder.model.tmf.ProductOrderRef;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.kumoco.productorder.api.TestConstants.AUTHORIZATION;
import static com.kumoco.productorder.api.TestConstants.FILTER;
import static com.kumoco.productorder.api.TestConstants.REQUEST_QUEUE;
import static com.kumoco.productorder.api.TestConstants.REQUEST_TYPE;
import static com.kumoco.productorder.api.TestConstants.RESPONSE_QUEUE;
import static com.kumoco.productorder.api.TestConstants.VF_SALES_ORDER_ID;
import static com.kumoco.productorder.api.TestConstants.VF_SYSTEM_INTENT;
import static com.kumoco.productorder.api.TestConstants.X_APPLICATION_ID;
import static com.kumoco.productorder.api.TestConstants.X_CORRELATION_ID;
import static com.kumoco.productorder.api.TestConstants.assertErrorBodyReturned;
import static com.kumoco.productorder.api.TestConstants.getError;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_REQUEST_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;


@ExtendWith(SpringExtension.class)
@Import({BuildProperties.class})
@WebMvcTest(ProductOrderApiController.class)
public class CancelProductOrderApiControllerTests {

    final String X_REQ_ID = UUID.randomUUID().toString();
    final String X_APP_ID = UUID.randomUUID().toString();
    final String X_COR_ID = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProducerTemplate producerTemplate;

    @MockBean
    private ConsumerTemplate consumerTemplate;


    @Test
    public void shouldReturn_400_If_POST_WrongBody() throws Exception {

        final CancelProductOrder cancelProductOrder = new CancelProductOrder();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cancelProductOrder/")
                .content(objectMapper.writeValueAsString(cancelProductOrder))
                .contentType(APPLICATION_JSON)
                .headers(getPostBssCancelProductOrderHeaders())
                .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(400).isEqualTo(result.getResponse().getStatus());

    }


    @Test
    public void shouldReturn_400_If_POST_CorrectBodyMissingHeaders() throws Exception {

        final CancelProductOrder cancelProductOrder = getCancelProductOrder();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cancelProductOrder/")
                .content(objectMapper.writeValueAsString(cancelProductOrder))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .headers(getMandatoryHeaders()))
                .andReturn();

        assertThat(400).isEqualTo(result.getResponse().getStatus());
        verifyResponseHeaders(result);

    }

    @Test
    public void shouldReturn_400_If_POST_CorrectBodyMissingMandatoryHeaders() throws Exception {

        final CancelProductOrder cancelProductOrder = getCancelProductOrder();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cancelProductOrder/")
                        .content(objectMapper.writeValueAsString(cancelProductOrder))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .headers(getPostBssCancelProductOrderHeaders()))
                .andReturn();

        assertThat(400).isEqualTo(result.getResponse().getStatus());

    }


    @Test
    void shouldReturn_500_If_POST_CorrectParams_InternalServerErrorOccurs() throws Exception {
        final CancelProductOrder cancelProductOrder = getCancelProductOrder();

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn("garbage json".getBytes(StandardCharsets.UTF_8));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cancelProductOrder/")
                .content(objectMapper.writeValueAsString(cancelProductOrder))
                .contentType(APPLICATION_JSON)
                .headers(getPostBssCancelProductOrderHeaders())
                .headers(getMandatoryHeaders())
                .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(500).isEqualTo(result.getResponse().getStatus());

    }

    @Test
    void shouldReturn_500_If_POST_CorrectParams_InvalidWorkerResponse() throws Exception {
        final CancelProductOrder cancelProductOrder = getCancelProductOrder();

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn("garbage message".getBytes(StandardCharsets.UTF_8));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cancelProductOrder/")
                .content(objectMapper.writeValueAsString(cancelProductOrder))
                .contentType(APPLICATION_JSON)
                .headers(getPostBssCancelProductOrderHeaders())
                .headers(getMandatoryHeaders())
                .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(result.getResponse().getContentAsString()).isBlank();
    }

    @Test
    void shouldReturn_424_If_POST_CorrectParams_CantGetResponseFromWorker() throws Exception {
        final CancelProductOrder cancelProductOrder = getCancelProductOrder();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cancelProductOrder/")
                .content(objectMapper.writeValueAsString(cancelProductOrder))
                .contentType(APPLICATION_JSON)
                .headers(getPostBssCancelProductOrderHeaders())
                .headers(getMandatoryHeaders())
                .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(424).isEqualTo(result.getResponse().getStatus());
        verifyResponseHeaders(result);
    }

    @Test
    public void shouldReturn_200_If_POST_CorrectParams() throws Exception {

        final CancelProductOrder cancelProductOrder = getCancelProductOrder();

        final CancelProductOrderMessage workerResponse = new CancelProductOrderMessage();
        workerResponse.setCancelProductOrder(new CancelProductOrder());
        workerResponse.setHttpStatus(HttpStatus.OK);
        workerResponse.setCorrelationId(Optional.of(X_COR_ID));

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(workerResponse));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cancelProductOrder/")
                .content(objectMapper.writeValueAsString(cancelProductOrder))
                .contentType(APPLICATION_JSON)
                .headers(getPostBssCancelProductOrderHeaders())
                .headers(getMandatoryHeaders())
                .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "CANCEL"),
                entry(VF_SYSTEM_INTENT, "salesorder")
        );
        
        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();
        verifyResponseHeaders(result);

    }


    @Test
    public void shouldReturn_501_If_GET_CancelProductOrder() throws Exception {

        final CancelProductOrder cancelProductOrder = getCancelProductOrder();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/cancelProductOrder/1")
                .content(objectMapper.writeValueAsString(cancelProductOrder))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(501).isEqualTo(result.getResponse().getStatus());

    }


    @Test
    public void shouldReturn_501_If_LIST_CancelProductOrder() throws Exception {

        final CancelProductOrder cancelProductOrder = getCancelProductOrder();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/cancelProductOrder/")
                .content(objectMapper.writeValueAsString(cancelProductOrder))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(501).isEqualTo(result.getResponse().getStatus());

    }


    private CancelProductOrder getCancelProductOrder() {
        final CancelProductOrder cancelProductOrder = new CancelProductOrder();

        final ProductOrderRef productOrderRef = new ProductOrderRef();
        productOrderRef.setId("12345");
        cancelProductOrder.setProductOrder(productOrderRef);
        return cancelProductOrder;
    }

    private HttpHeaders getPostBssCancelProductOrderHeaders() {

        final HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.set(VF_SYSTEM_INTENT,"salesorder");
        httpHeaders.set(VF_SALES_ORDER_ID, "test");
        httpHeaders.set(AUTHORIZATION, "Bearer blah");
        return httpHeaders;
    }

    private CancelProductOrderMessage getCustomerMessageError(final HttpStatus httpStatus) {
        final CancelProductOrderMessage customerMessage = new CancelProductOrderMessage();

        customerMessage.setCorrelationId(Optional.of(X_COR_ID));
        customerMessage.setHttpStatus(httpStatus);
        customerMessage.setError(getError());

        return customerMessage;
    }

    private byte[] getErrorResponse() throws JsonProcessingException {

        return objectMapper.writeValueAsBytes(getCustomerMessageError(HttpStatus.BAD_REQUEST));
    }

    @Test
    void shouldReturn_400_If_POST_CorrectParams_WorkerErrorOccurs() throws Exception {
        final CancelProductOrder cancelProductOrder = getCancelProductOrder();

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(getErrorResponse());

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cancelProductOrder/")
                .content(objectMapper.writeValueAsString(cancelProductOrder))
                .contentType(APPLICATION_JSON)
                .headers(getPostBssCancelProductOrderHeaders())
                .headers(getMandatoryHeaders())
                .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertErrorBodyReturned(result, objectMapper);
        verifyResponseHeaders(result);
    }

    private HttpHeaders getMandatoryHeaders() {

        final HttpHeaders httpHeadersNew = new HttpHeaders();
        httpHeadersNew.set(X_REQUEST_ID, X_REQ_ID);
        httpHeadersNew.set(X_APPLICATION_ID, X_APP_ID);
        httpHeadersNew.set(X_CORRELATION_ID, X_COR_ID);
        return httpHeadersNew;

    }

    private void verifyResponseHeaders(final MvcResult result) {

        assertThat(result.getResponse().getHeader(X_REQUEST_ID)).isEqualTo(X_REQ_ID);
        assertThat(result.getResponse().getHeader(X_APPLICATION_ID)).isEqualTo(X_APP_ID);
        assertThat(result.getResponse().getHeader(X_CORRELATION_ID)).isEqualTo(X_COR_ID);

    }

}
