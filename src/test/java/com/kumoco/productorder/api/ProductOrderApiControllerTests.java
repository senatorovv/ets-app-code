package com.kumoco.productorder.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.kumoco.productorder.message.ProductOrderMessage;
import com.kumoco.productorder.model.tmf.ProductOrder;
import com.kumoco.productorder.model.tmf.ProductOrderCreate;
import com.kumoco.productorder.model.tmf.ProductOrderItem;
import com.kumoco.productorder.model.tmf.ProductOrderUpdate;
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

import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.kumoco.productorder.api.TestConstants.ACTION_ADD;
import static com.kumoco.productorder.api.TestConstants.AUTHORIZATION;
import static com.kumoco.productorder.api.TestConstants.FILTER;
import static com.kumoco.productorder.api.TestConstants.LONG_VALUE;
import static com.kumoco.productorder.api.TestConstants.REQUEST_QUEUE;
import static com.kumoco.productorder.api.TestConstants.REQUEST_TYPE;
import static com.kumoco.productorder.api.TestConstants.RESPONSE_QUEUE;
import static com.kumoco.productorder.api.TestConstants.VF_BILLINGACCOUNTID;
import static com.kumoco.productorder.api.TestConstants.VF_BPI_ID;
import static com.kumoco.productorder.api.TestConstants.VF_CUSTOMER_ID;
import static com.kumoco.productorder.api.TestConstants.VF_LOCATION_ID;
import static com.kumoco.productorder.api.TestConstants.VF_ORDERITEM;
import static com.kumoco.productorder.api.TestConstants.VF_SALES_ORDER_ID;
import static com.kumoco.productorder.api.TestConstants.VF_SALES_ORDER_TYPE;
import static com.kumoco.productorder.api.TestConstants.VF_SUBSCRIBER_ID;
import static com.kumoco.productorder.api.TestConstants.VF_SYSTEM_INTENT;
import static com.kumoco.productorder.api.TestConstants.X_APPLICATION_ID;
import static com.kumoco.productorder.api.TestConstants.X_CORRELATION_ID;
import static com.kumoco.productorder.api.TestConstants.assertErrorBodyReturned;
import static com.kumoco.productorder.api.TestConstants.getError;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.X_REQUEST_ID;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@Import({BuildProperties.class})
@WebMvcTest(ProductOrderApiController.class)
class ProductOrderApiControllerTests {

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


    private Map<String, String> cookieMultiValueMap;

    @Test
    void shouldReturn_404_IfGET_WrongURL() throws Exception {

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/1/2")
                .contentType(APPLICATION_JSON)
                .cookie(new Cookie("CurrentUser", "Test"))
                .accept(APPLICATION_JSON)).andReturn();

        assertThat(404).isEqualTo(result.getResponse().getStatus());
    }


    @Test
    void shouldReturn_400_If_GET_WrongSystemIntent() throws Exception {

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/1")
                .contentType(APPLICATION_JSON)
                .cookie(new Cookie("CurrentUser", "Test"))
                .header(VF_SYSTEM_INTENT,"test")
                .headers(getMandatoryHeaders())
                .accept(APPLICATION_JSON)).andReturn();

        assertThat(400).isEqualTo(result.getResponse().getStatus());
        verifyResponseHeaders(result);
    }


    //    @Test
    void shouldReturn_500_If_GET_CorrectParams_InternalServerErrorOccurs() throws Exception {

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn("garbage json".getBytes(StandardCharsets.UTF_8));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/1")
                        .contentType(APPLICATION_JSON)
                        .headers(getOcsHeaders())
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(500).isEqualTo(result.getResponse().getStatus());

    }

    @Test
    void shouldReturn_424_If_GET_CorrectParams_CantGetResponseFromWorker() throws Exception {

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/1")
                        .contentType(APPLICATION_JSON)
                        .headers(getOcsHeaders())
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(424).isEqualTo(result.getResponse().getStatus());

    }


    @Test
    void shouldReturn_200_If_GET_CorrectOCSParams() throws Exception {

        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/1")
                .contentType(APPLICATION_JSON)
                .headers(getOcsHeaders())
                .cookie(new Cookie("CurrentUser", "Test"))
                .accept(APPLICATION_JSON)).andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "GET"),
                entry(VF_SUBSCRIBER_ID, "123456"),
                entry(VF_SYSTEM_INTENT, "subscription")
        );

        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();

    }

    @Test
    void shouldReturn_200_If_GET_CorrectBSSParams() throws Exception {

        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/1")
                .contentType(APPLICATION_JSON)
                .cookie(new Cookie("CurrentUser", "Test"))
                .headers(getBssHeaders())
                .accept(APPLICATION_JSON)).andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "GET"),
                entry(VF_CUSTOMER_ID, "333"),
                entry(VF_SYSTEM_INTENT, "salesorder")
        );

        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();

    }

    @Test
    void shouldReturn_400_If_GET_CorrectBSSParamsAndMissingMandatoryHeaders() throws Exception {

        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/1")
                .contentType(APPLICATION_JSON)
                .cookie(new Cookie("CurrentUser", "Test"))
                .header(VF_SYSTEM_INTENT,"salesorder")
                .header(AUTHORIZATION, "Bearer blah")
                .header(VF_CUSTOMER_ID, "333")
                .header(X_REQUEST_ID, X_REQ_ID)
                .accept(APPLICATION_JSON)).andReturn();

        assertThat(400).isEqualTo(result.getResponse().getStatus());

        verifyNoInteractions(producerTemplate);
        assertThat(result.getResponse().getHeader(X_REQUEST_ID)).isEqualTo(X_REQ_ID);

    }


    @Test
    void shouldReturn_400_If_LIST_WrongSystemIntentHeaders() throws Exception {

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/")
                .contentType(APPLICATION_JSON)
                .header(VF_SYSTEM_INTENT,"test")
                .headers(getMandatoryHeaders())
                .cookie(new Cookie("CurrentUser", "Test"))
                .accept(APPLICATION_JSON)).andReturn();

        assertThat(400).isEqualTo(result.getResponse().getStatus());
        verifyResponseHeaders(result);
    }

    @Test
    void shouldReturn_412_If_LIST_CorrectParams_InternalServerErrorOccurs() throws Exception {

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn("garbage json".getBytes(StandardCharsets.UTF_8));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/")
                        .contentType(APPLICATION_JSON)
                        .headers(getOcsHeaders())
                        .accept(APPLICATION_JSON)).andReturn();
        assertThat(412).isEqualTo(result.getResponse().getStatus());

    }

    @Test
    void shouldReturn_424_If_LIST_CorrectParams_CantGetResponseFromWorker() throws Exception {

        final MvcResult result =   mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/")
                        .contentType(APPLICATION_JSON)
                        .headers(getOcsHeaders())
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(424).isEqualTo(result.getResponse().getStatus());
        verifyResponseHeaders(result);

    }

    @Test
    void shouldReturn_200_If_LIST_CorrectOCSParams() throws Exception {

        final ProductOrderMessage productOrderMessage = getProductOrderMessage(HttpStatus.OK);
        productOrderMessage.setCookies(ImmutableMap.of("Test1", "value1", "Test2", "Value2"));
        productOrderMessage.setProductOrders(singletonList(new ProductOrder()));
        productOrderMessage.setHttpStatus(HttpStatus.OK);

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/")
                .contentType(APPLICATION_JSON)
                .headers(getOcsHeaders())
                .cookie(new Cookie("CurrentUser", "Test"))
                .accept(APPLICATION_JSON)).andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "LIST"),
                entry(VF_SYSTEM_INTENT, "subscription")
        );

        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();
        verifyResponseHeaders(result);
    }

    @Test
    void shouldReturn_200_If_LIST_CorrectBSSParams() throws Exception {

        final ProductOrderMessage productOrderMessage = getProductOrderMessage(HttpStatus.OK);

        productOrderMessage.setCookies(ImmutableMap.of("Test1", "value1", "Test2", "Value2"));
        productOrderMessage.setProductOrders(singletonList(new ProductOrder()));
        productOrderMessage.setHttpStatus(HttpStatus.OK);

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/")
                .contentType(APPLICATION_JSON)
                .headers(getBssHeaders())
                .cookie(new Cookie("CurrentUser", "Test"))
                .accept(APPLICATION_JSON)).andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "LIST"),
                entry(VF_SYSTEM_INTENT, "salesorder")
        );

        assertThat(productOrderMessage.getCookies()).isNotEmpty();
        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();
        verifyResponseHeaders(result);
    }


    @Test
    void shouldReturn_400_If_POST_WrongBody() throws Exception {

        final ProductOrderCreate productOrderCreate = new ProductOrderCreate();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/")
                        .content(objectMapper.writeValueAsString(productOrderCreate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .header(VF_SYSTEM_INTENT,"test")
                        .headers(getMandatoryHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(400).isEqualTo(result.getResponse().getStatus());

    }


    @Test
    void shouldReturn_400_If_POST_CorrectBody_MissingHeaders() throws Exception {

        final ProductOrderCreate productOrderCreate = getProductOrderCreate();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/")
                        .content(objectMapper.writeValueAsString(productOrderCreate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .header(VF_SYSTEM_INTENT,"test")
                        .headers(getMandatoryHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(400).isEqualTo(result.getResponse().getStatus());
        verifyResponseHeaders(result);
    }

    @Test
    void shouldReturn_412_If_POST_CorrectParams_InternalServerErrorOccurs() throws Exception {

        final ProductOrderCreate productOrderCreate = getProductOrderCreate();

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn("garbage json".getBytes(StandardCharsets.UTF_8));

        final MvcResult result =   mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/")
                        .content(objectMapper.writeValueAsString(productOrderCreate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPostOcsHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(412).isEqualTo(result.getResponse().getStatus());

    }

    @Test
    void shouldReturn_424_If_POST_CorrectParams_CantGetResponseFromWorker() throws Exception {

        final ProductOrderCreate productOrderCreate = getProductOrderCreate();

        final MvcResult result =   mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/")
                        .content(objectMapper.writeValueAsString(productOrderCreate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPostOcsHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(424).isEqualTo(result.getResponse().getStatus());
        verifyResponseHeaders(result);
    }


    @Test
    void shouldReturn_200_If_POST_CorrectBSSParamsAndHeaders_Existing() throws Exception {

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);
        final ArgumentCaptor<String> bodyCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), bodyCapture.capture(), workerHeaderCapture.capture());

        final ProductOrderCreate productOrderCreate = getProductOrderCreate();
        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();
        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/?productOrder.productOrderItem.product.id=9182&productOrder.productOrderItem.product.place.id=1223")
                        .content(objectMapper.writeValueAsString(productOrderCreate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPostBssHeadersExisting("salesorder"))
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        final Map<String, Object> bodyCaptureValue = objectMapper.readValue(bodyCapture.getValue(), Map.class);
        assertThat(bodyCaptureValue)
                .contains(
                        entry("Authorization", "Bearer blah"),
                        entry("Customer-Info", "DistributionChannel=App, Customer=\"9159953048513681988\""));

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(VF_SALES_ORDER_TYPE, "existing"),
                entry(REQUEST_TYPE, "POST"),
                entry(VF_SYSTEM_INTENT, "salesorder")
        );

        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();
        verifyResponseHeaders(result);
    }

    @Test
    void shouldReturn_200_If_POST_CorrectVoucherParamsAndHeaders_Existing() throws Exception {

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);
        final ArgumentCaptor<String> bodyCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), bodyCapture.capture(), workerHeaderCapture.capture());

        final ProductOrderCreate productOrderCreate = getProductOrderCreate();
        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();
        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/?productOrder.productOrderItem.product.place.id=1223")
                        .content(objectMapper.writeValueAsString(productOrderCreate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPostBssHeadersExisting("voucherorder"))
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        final Map<String, Object> bodyCaptureValue = objectMapper.readValue(bodyCapture.getValue(), Map.class);
        assertThat(bodyCaptureValue)
                .contains(
                        entry("Authorization", "Bearer blah"),
                        entry("Customer-Info", "DistributionChannel=App, Customer=\"9159953048513681988\""))
                .containsKeys("X-Request-ID", "productOrderCreate", "cookies");


        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "POST"),
                entry(VF_SYSTEM_INTENT, "voucherorder")
        );

        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();
        verifyResponseHeaders(result);
    }

    @Test
    void shouldReturn_200_If_POST_CorrectOcsParamsAndHeaders() throws Exception {

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);
        final ArgumentCaptor<String> bodyCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), bodyCapture.capture(), workerHeaderCapture.capture());

        final ProductOrderCreate productOrderCreate = getProductOrderCreate();
        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();
        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/?productOrder.productOrderItem.product.place.id=1223")
                        .content(objectMapper.writeValueAsString(productOrderCreate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getOcsHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        final Map<String, Object> bodyCaptureValue = objectMapper.readValue(bodyCapture.getValue(), Map.class);
        assertThat(bodyCaptureValue)
                .contains(
                        entry("Authorization", "Bearer blah"))
                .containsKeys("X-Request-ID", "productOrderCreate")
                .doesNotContainKey("Customer-Info");

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "POST"),
                entry(VF_SYSTEM_INTENT, "subscription")
        );

        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();
        verifyResponseHeaders(result);
    }


    @Test
    void shouldReturn_200_If_POST_CorrectBSSParamsAndHeaders_Submit() throws Exception {

        final ProductOrderCreate productOrderCreate = getProductOrderCreate();

        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/?productOrder.productOrderItem.product.place.id=1223")
                        .content(objectMapper.writeValueAsString(productOrderCreate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPostBssHeadersSubmit())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(VF_SALES_ORDER_TYPE, "submit"),
                entry(REQUEST_TYPE, "POST"),
                entry(VF_SYSTEM_INTENT, "salesorder")
        );
        verifyResponseHeaders(result);
    }

    @Test
    void shouldReturn_200_If_POST_CorrectBSSParamsAndHeaders_Disconnect() throws Exception {

        final ProductOrderCreate productOrderCreate = getProductOrderCreate();

        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/?productOrder.productOrderItem.product.place.id=1223")
                        .content(objectMapper.writeValueAsString(productOrderCreate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPostBssHeadersDisconnect())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "POST"),
                entry(VF_SALES_ORDER_TYPE, "disconnect"),
                entry(VF_SYSTEM_INTENT, "salesorder")
        );

        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();
        verifyResponseHeaders(result);
    }


    @Test
    void shouldReturn_200_If_POST_CorrectOCSParamsAndHeaders() throws Exception {

        final ProductOrderCreate productOrderCreate = getProductOrderCreate();

        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/")
                        .content(objectMapper.writeValueAsString(productOrderCreate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPostOcsHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "POST"),
                entry(VF_SYSTEM_INTENT, "subscription")
        );

        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();
        verifyResponseHeaders(result);

    }


    @Test
    void shouldReturn_200_If_POST_CorrectEDAParamsAndHeaders() throws Exception {

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);
        final ArgumentCaptor<String> bodyCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), bodyCapture.capture(), workerHeaderCapture.capture());

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(getSuccessfulProductOrderMessage()));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/")
                        .content(objectMapper.writeValueAsString(getProductOrderCreate()))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPostEdaHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);


        assertThat(result.getResponse().getCookies()).hasSize(0);

        final Map<String, Object> bodyCaptureValue = objectMapper.readValue(bodyCapture.getValue(), Map.class);
        assertThat(bodyCaptureValue)
                .contains(
                        entry("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Im5PbzNaRHJPRFhFSzFqS1doWHNsSFJfS1hFZyIsImtpZCI6Im5PbzNaRHJPRFhFSzFqS1doWHNsSFJfS1hFZyJ9.eyJhdWQiOiJodHRwczovL3NpdDAwMWE2ZTRmYTIwNjY3MTJjOTVkZXZhb3MuY2xvdWRheC5keW5hbWljcy5jb20iLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC9jMTljZTMxMi0xZmIzLTQ3ZjItOWRiMS05MGI0MDQxZTY1NmUvIiwiaWF0IjoxNjIxNzU3NzYxLCJuYmYiOjE2MjE3NTc3NjEsImV4cCI6MTYyMTc2MTY2MSwiYWlvIjoiRTJaZ1lLZ0lhNWl5Y2YzYUo1UHVQODV1clhYNER3QT0iLCJhcHBpZCI6Ijk0YjNhOTU0LTkyM2ItNDgzNi05NDUxLTk1MGYwMzEzZjdlZCIsImFwcGlkYWNyIjoiMSIsImlkcCI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0L2MxOWNlMzEyLTFmYjMtNDdmMi05ZGIxLTkwYjQwNDFlNjU2ZS8iLCJvaWQiOiIzN2NjZGUxYi1iZDUyLTQzMDEtYWMxMC1kYThjZmE5MjMzMGEiLCJyaCI6IjAuQVNFQUV1T2N3Yk1mOGtlZHNaQzBCQjVsYmxTcHM1UTdralpJbEZHVkR3TVQ5LTBoQUFBLiIsInN1YiI6IjM3Y2NkZTFiLWJkNTItNDMwMS1hYzEwLWRhOGNmYTkyMzMwYSIsInRpZCI6ImMxOWNlMzEyLTFmYjMtNDdmMi05ZGIxLTkwYjQwNDFlNjU2ZSIsInV0aSI6InREa1kzaFAweEUyY0ZTU2xLdzNmQUEiLCJ2ZXIiOiIxLjAifQ.jIYYhTv9KoPJO5s5TY6swUFXGTvuKaiKD9sgfp3CkE6DuCWfffKOuJKNF3IB9Z2txBNcXKnxhwUrmQJWM5TCB7Woc27QGNZy_SaTdzYSUSieMk6CUfa1_vNWWJQvCmp-XKSAVyOiOlYg9fZIKnnqIa0lPHZwHu6y-QIRp-BjYIKdyrjxBARf2AGlrDra_iD1ixwZERgfwjPhAwzTAhhNZ_L913OdqaLzRXPQuDsyGLtkAglnOxMkWLMoPmWmZD6V5sFFWbBryn37NBfJNhyok36NaXKnZR5WP_jPgM6sBsiffdfY1jeH41HlAJO_7avnMINIbVdIUM-_NM9Kj2PzyA")
                       )
                .containsKeys("X-Request-ID", "productOrderCreate")
                .doesNotContainKeys("Customer-Info");

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "POST"),
                entry(VF_SYSTEM_INTENT, "provisioning")
        );

        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();
        verifyResponseHeaders(result);
    }

    @Test
    void shouldReturn_200_If_POST_CorrectERPParamsAndHeaders() throws Exception {

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);
        final ArgumentCaptor<String> bodyCapture = ArgumentCaptor.forClass(String.class);
        doNothing().when(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), bodyCapture.capture(), workerHeaderCapture.capture());

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(getSuccessfulProductOrderMessage()));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/")
                        .content(objectMapper.writeValueAsString(getProductOrderCreate()))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPostErpHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        final Map<String, Object> bodyCaptureValue = objectMapper.readValue(bodyCapture.getValue(), Map.class);
        assertThat(bodyCaptureValue)
                .contains(
                        entry("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Im5PbzNaRHJPRFhFSzFqS1doWHNsSFJfS1hFZyIsImtpZCI6Im5PbzNaRHJPRFhFSzFqS1doWHNsSFJfS1hFZyJ9.eyJhdWQiOiJodHRwczovL3NpdDAwMWE2ZTRmYTIwNjY3MTJjOTVkZXZhb3MuY2xvdWRheC5keW5hbWljcy5jb20iLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC9jMTljZTMxMi0xZmIzLTQ3ZjItOWRiMS05MGI0MDQxZTY1NmUvIiwiaWF0IjoxNjIxNzU3NzYxLCJuYmYiOjE2MjE3NTc3NjEsImV4cCI6MTYyMTc2MTY2MSwiYWlvIjoiRTJaZ1lLZ0lhNWl5Y2YzYUo1UHVQODV1clhYNER3QT0iLCJhcHBpZCI6Ijk0YjNhOTU0LTkyM2ItNDgzNi05NDUxLTk1MGYwMzEzZjdlZCIsImFwcGlkYWNyIjoiMSIsImlkcCI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0L2MxOWNlMzEyLTFmYjMtNDdmMi05ZGIxLTkwYjQwNDFlNjU2ZS8iLCJvaWQiOiIzN2NjZGUxYi1iZDUyLTQzMDEtYWMxMC1kYThjZmE5MjMzMGEiLCJyaCI6IjAuQVNFQUV1T2N3Yk1mOGtlZHNaQzBCQjVsYmxTcHM1UTdralpJbEZHVkR3TVQ5LTBoQUFBLiIsInN1YiI6IjM3Y2NkZTFiLWJkNTItNDMwMS1hYzEwLWRhOGNmYTkyMzMwYSIsInRpZCI6ImMxOWNlMzEyLTFmYjMtNDdmMi05ZGIxLTkwYjQwNDFlNjU2ZSIsInV0aSI6InREa1kzaFAweEUyY0ZTU2xLdzNmQUEiLCJ2ZXIiOiIxLjAifQ.jIYYhTv9KoPJO5s5TY6swUFXGTvuKaiKD9sgfp3CkE6DuCWfffKOuJKNF3IB9Z2txBNcXKnxhwUrmQJWM5TCB7Woc27QGNZy_SaTdzYSUSieMk6CUfa1_vNWWJQvCmp-XKSAVyOiOlYg9fZIKnnqIa0lPHZwHu6y-QIRp-BjYIKdyrjxBARf2AGlrDra_iD1ixwZERgfwjPhAwzTAhhNZ_L913OdqaLzRXPQuDsyGLtkAglnOxMkWLMoPmWmZD6V5sFFWbBryn37NBfJNhyok36NaXKnZR5WP_jPgM6sBsiffdfY1jeH41HlAJO_7avnMINIbVdIUM-_NM9Kj2PzyA"))
                .containsKeys("X-Request-ID", "productOrderCreate")
                .doesNotContainKeys("Customer-Info");

        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "POST"),
                entry(VF_SYSTEM_INTENT, "salesrecord")
        );

        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();
        verifyResponseHeaders(result);

    }

    @Test
    void shouldReturn_200_If_POST_CorrectVoucherOrderParamsAndHeaders() throws Exception {

        final ProductOrderCreate productOrderCreate = getProductOrderCreate();

        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/")
                        .content(objectMapper.writeValueAsString(productOrderCreate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPostVoucherOrderHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "POST"),
                entry(VF_SYSTEM_INTENT, "voucherorder")
        );

        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();
        verifyResponseHeaders(result);

    }

    @Test
    void shouldReturn_400_If_PATCH_WrongBody() throws Exception {

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/productOrder/1/")
                        .contentType(APPLICATION_JSON)
                        .header(VF_SYSTEM_INTENT,"test")
                        .headers(getMandatoryHeaders())
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(400).isEqualTo(result.getResponse().getStatus());

    }


//    @Test
    void shouldReturn_400_If_PATCH_CorrectBodyMissingHeaders() throws Exception {

        final ProductOrderUpdate productOrderUpdate = getProductOrderUpdate();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/productOrder/1/")
                        .content(objectMapper.writeValueAsString(productOrderUpdate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .header(VF_SYSTEM_INTENT,"test")
                        .headers(getMandatoryHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(400).isEqualTo(result.getResponse().getStatus());
        verifyResponseHeaders(result);
    }

    @Test
    void shouldReturn_424_If_PATCH_CorrectParams_InternalServerErrorOccurs() throws Exception {

        final ProductOrderUpdate productOrderUpdate = getProductOrderUpdate();

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(String.class))).thenReturn("garbage json");

        final MvcResult result =mockMvc.perform(MockMvcRequestBuilders.patch("/productOrder/1/")
                        .content(objectMapper.writeValueAsString(productOrderUpdate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPatchBssHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(424).isEqualTo(result.getResponse().getStatus());

    }

    @Test
    void shouldReturn_424_If_PATCH_CorrectParams_CantGetResponseFromWorker() throws Exception {

        final ProductOrderUpdate productOrderUpdate = getProductOrderUpdate();

        final MvcResult result =mockMvc.perform(MockMvcRequestBuilders.patch("/productOrder/1/")
                        .content(objectMapper.writeValueAsString(productOrderUpdate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPatchBssHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(424).isEqualTo(result.getResponse().getStatus());
        verifyResponseHeaders(result);

    }


    @Test
    void shouldReturn_200_If_PATCH_CorrectBSSParams() throws Exception {

        final ProductOrderUpdate productOrderUpdate = getProductOrderUpdate();

        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/productOrder/1/")
                        .content(objectMapper.writeValueAsString(productOrderUpdate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPatchBssHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "PATCH"),
                entry(VF_CUSTOMER_ID, "test"),
                entry(VF_ORDERITEM, "test"),
                entry(VF_SYSTEM_INTENT, "salesorder")
        );
        verifyResponseHeaders(result);
    }

    @Test
    void shouldReturn_200_If_PATCH_BillingAccount_CorrectBSSParams() throws Exception {

        final ProductOrderUpdate productOrderUpdate = getProductOrderUpdate();

        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/productOrder/1/")
                        .content(objectMapper.writeValueAsString(productOrderUpdate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPatchBssBillingAccountHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "PATCH"),
                entry(VF_ORDERITEM, "test"),
                entry(VF_SYSTEM_INTENT, "salesorder")
        );
        verifyResponseHeaders(result);
    }

    @Test
    void shouldReturn_200_If_PATCH_CorrectOCSParams() throws Exception {

        final ProductOrderUpdate productOrderUpdate = getProductOrderUpdate();

        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/productOrder/1/")
                        .content(objectMapper.writeValueAsString(productOrderUpdate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPatchOcsHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(200).isEqualTo(result.getResponse().getStatus());


        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "PATCH"),
                entry(VF_SUBSCRIBER_ID, "123456"),
                entry(VF_SYSTEM_INTENT, "subscription")

        );

        assertThat(workerHeaderCapture.getAllValues().get(0)).containsKey(FILTER);
        assertThat(workerHeaderCapture.getAllValues().get(0).get(FILTER)).isNotNull();
        verifyResponseHeaders(result);
    }


    @Test
    void shouldReturn_400_If_DELETE_MissingHeaders() throws Exception {

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/productOrder/1")
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .header(VF_SYSTEM_INTENT,"test")
                        .headers(getMandatoryHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(400).isEqualTo(result.getResponse().getStatus());
        verifyResponseHeaders(result);
    }


    @Test
    void shouldReturn_424_If_DELETE_FailedDependencyOnWorker() throws Exception {

        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(30000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/productOrder/1")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getOcsHeaders()))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.FAILED_DEPENDENCY.value());
        assertThat(result.getResponse().getContentAsString()).isNotNull();
        verifyResponseHeaders(result);
    }


    @Test
    void shouldReturn_200_If_DELETE_CorrectParams() throws Exception {

        final ProductOrderMessage workerResponse = new ProductOrderMessage();
        workerResponse.setProductOrders(singletonList(new ProductOrder()));
        workerResponse.setHttpStatus(HttpStatus.OK);

        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/productOrder/1")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getOcsHeaders()))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.getResponse().getContentAsString()).isNotNull();

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "DELETE"),
                entry(VF_SUBSCRIBER_ID, "123456"),
                entry(VF_SYSTEM_INTENT, "subscription")

        );
        verifyResponseHeaders(result);

    }

    private ProductOrderMessage getSuccessfulProductOrderMessage() throws JsonProcessingException {
        final ProductOrderMessage productOrderMessage = getProductOrderMessage(HttpStatus.OK);
        productOrderMessage.setProductOrders(singletonList(new ProductOrder()));
        productOrderMessage.setHttpStatus(HttpStatus.OK);
        productOrderMessage.setCookies(ImmutableMap.of("CurrentUser", "value1", "Age", "Value2"));

        return productOrderMessage;
    }

    private ProductOrderCreate getProductOrderCreate() {
        final ProductOrderCreate productOrderCreate = new ProductOrderCreate();

        final ProductOrderItem productOrderItem = new ProductOrderItem();
        productOrderItem.setId("123");
        productOrderItem.setAction(ACTION_ADD);
        productOrderItem.setQuantity(LONG_VALUE);
        productOrderCreate.setProductOrderItem(singletonList(productOrderItem));
        return productOrderCreate;
    }

    private ProductOrderUpdate getProductOrderUpdate() {
        final ProductOrderUpdate productOrderUpdate = new ProductOrderUpdate();

        final ProductOrderItem productOrderItem = new ProductOrderItem();
        productOrderItem.setId("123");
        productOrderItem.setAction(ACTION_ADD);
        productOrderUpdate.setProductOrderItem(singletonList(productOrderItem));
        return productOrderUpdate;
    }

    private ProductOrderMessage getProductOrderMessage(final HttpStatus httpStatus) throws JsonProcessingException {
        final ProductOrderMessage productOrderMessage = new ProductOrderMessage();
        productOrderMessage.setCorrelationId(Optional.of(X_COR_ID));
        productOrderMessage.setHttpStatus(httpStatus);
        productOrderMessage.setCookies(ImmutableMap.of("Test1", "value1", "Test2", "Value2"));
        productOrderMessage.setProductOrders(singletonList(new ProductOrder()));
        return productOrderMessage;
    }

    private HttpHeaders getOcsHeaders() {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT,"subscription");
        httpHeaders.set(AUTHORIZATION, "Bearer blah");
        httpHeaders.set(VF_SUBSCRIBER_ID, "123456");
        httpHeaders.set("Customer-Info", "DistributionChannel=App, Customer=\"9159953048513681988\"");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    private HttpHeaders getBssHeaders() {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT,"salesorder");
        httpHeaders.set(AUTHORIZATION, "Bearer blah");
        httpHeaders.set(VF_CUSTOMER_ID, "333");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    private HttpHeaders getPostBssHeadersExisting(final String intent) {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT, intent);
        httpHeaders.set(AUTHORIZATION, "Bearer blah");
        httpHeaders.set(VF_SALES_ORDER_TYPE, "existing");
        httpHeaders.set(VF_CUSTOMER_ID, "1234");
        httpHeaders.set(VF_LOCATION_ID, "4545");
        httpHeaders.set(VF_BPI_ID, "9182");
        httpHeaders.set("Customer-Info", "DistributionChannel=App, Customer=\"9159953048513681988\"");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    private HttpHeaders getPostBssHeadersSubmit() {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT,"salesorder");
        httpHeaders.set(AUTHORIZATION, "Bearer blah");
        httpHeaders.set(VF_SALES_ORDER_TYPE, "submit");
        httpHeaders.set(VF_CUSTOMER_ID, "1234");
        httpHeaders.set(VF_SALES_ORDER_ID,"4234");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    private HttpHeaders getPostBssHeadersDisconnect() {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT,"salesorder");
        httpHeaders.set(AUTHORIZATION, "Bearer blah");
        httpHeaders.set(VF_SALES_ORDER_TYPE, "disconnect");
        httpHeaders.set(VF_SALES_ORDER_ID,"4234");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    private HttpHeaders getPostOcsHeaders() {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT,"subscription");
        httpHeaders.set(VF_SUBSCRIBER_ID, "123456");
        httpHeaders.set(AUTHORIZATION, "Bearer blah");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    private HttpHeaders getPostEdaHeaders() {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT,"provisioning");
        httpHeaders.set(AUTHORIZATION, "Bearer eeyJraWQiOiI5NTc5NmFmZDU2MTA0Y2M4YjIxZDA2MjFjMzRmYzhkYyIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJBUElHV19URVNUIiwiYXVkIjpbIkVSUCIsIkFQSUdXX1RFU1QiLCI8VkZPPiJdLCJhenAiOiJBUElHV19URVNUIiwiYXV0aF90aW1lIjoxNjIxNzU4MDYwLCJzY29wZSI6WyJJTlRFTlRfc2FsZXNPcmRlck5vdGlmeVJXIiwiVE1GNjIyOlJXIiwiS0NfRVJQX1JXIl0sImlzcyI6Imh0dHBzOlwvXC92Zm8temRzLXNndy1pc2d3LXNzbC16ZW50aXR5LXNndy5hcHBzLnJob2NwLm5vbi1wcm9kLWNsb3VkMS5pdGNsb3VkLmxvY2FsLnZvZGFmb25lLm9tIiwiZXh0ZXJuYWxBdXRob3JpemF0aW9uIjoiQmVhcmVyIGV5SjBlWEFpT2lKS1YxUWlMQ0poYkdjaU9pSlNVekkxTmlJc0luZzFkQ0k2SW01UGJ6TmFSSEpQUkZoRlN6RnFTMWRvV0hOc1NGSmZTMWhGWnlJc0ltdHBaQ0k2SW01UGJ6TmFSSEpQUkZoRlN6RnFTMWRvV0hOc1NGSmZTMWhGWnlKOS5leUpoZFdRaU9pSm9kSFJ3Y3pvdkwzTnBkREF3TVdFMlpUUm1ZVEl3TmpZM01USmpPVFZrWlhaaGIzTXVZMnh2ZFdSaGVDNWtlVzVoYldsamN5NWpiMjBpTENKcGMzTWlPaUpvZEhSd2N6b3ZMM04wY3k1M2FXNWtiM2R6TG01bGRDOWpNVGxqWlRNeE1pMHhabUl6TFRRM1pqSXRPV1JpTVMwNU1HSTBNRFF4WlRZMU5tVXZJaXdpYVdGMElqb3hOakl4TnpVM056WXhMQ0p1WW1ZaU9qRTJNakUzTlRjM05qRXNJbVY0Y0NJNk1UWXlNVGMyTVRZMk1Td2lZV2x2SWpvaVJUSmFaMWxMWjBsaE5XbDVZMll6WVVvMVVIVlFPRFYxY2xoWU5FUjNRVDBpTENKaGNIQnBaQ0k2SWprMFlqTmhPVFUwTFRreU0ySXRORGd6TmkwNU5EVXhMVGsxTUdZd016RXpaamRsWkNJc0ltRndjR2xrWVdOeUlqb2lNU0lzSW1sa2NDSTZJbWgwZEhCek9pOHZjM1J6TG5kcGJtUnZkM011Ym1WMEwyTXhPV05sTXpFeUxURm1Zak10TkRkbU1pMDVaR0l4TFRrd1lqUXdOREZsTmpVMlpTOGlMQ0p2YVdRaU9pSXpOMk5qWkdVeFlpMWlaRFV5TFRRek1ERXRZV014TUMxa1lUaGpabUU1TWpNek1HRWlMQ0p5YUNJNklqQXVRVk5GUVVWMVQyTjNZazFtT0d0bFpITmFRekJDUWpWc1lteFRjSE0xVVRkcmFscEpiRVpIVmtSM1RWUTVMVEJvUVVGQkxpSXNJbk4xWWlJNklqTTNZMk5rWlRGaUxXSmtOVEl0TkRNd01TMWhZekV3TFdSaE9HTm1ZVGt5TXpNd1lTSXNJblJwWkNJNkltTXhPV05sTXpFeUxURm1Zak10TkRkbU1pMDVaR0l4TFRrd1lqUXdOREZsTmpVMlpTSXNJblYwYVNJNkluUkVhMWt6YUZBd2VFVXlZMFpUVTJ4TGR6Tm1RVUVpTENKMlpYSWlPaUl4TGpBaWZRLmpJWVloVHY5S29QSk81czVUWTZzd1VGWEdUdnVLYWlLRDlzZ2ZwM0NrRTZEdUNXZmZmS091SktORjNJQjlaMnR4Qk5jWEtueGh3VXJtUUpXTTVUQ0I3V29jMjdRR05aeV9TYVRkellTVVNpZU1rNkNVZmExX3ZOV1dKUXZDbXAtWEtTQVZ5T2lPbFlnOWZaSUtubnFJYTBsUEhad0h1NnktUUlScC1CallJS2R5cmp4QkFSZjJBR2xyRHJhX2lEMWl4d1pFUmdmd2pQaEF3elRBaGhOWl9MOTEzT2RxYUx6UlhQUXVEc3lHTHRrQWdsbk94TWtXTE1vUG1XbVpENlY1c0ZGV2JCcnluMzdOQmZKTmh5b2szNk5hWEtuWlI1V1BfalBnTTZzQnNpZmZkZlkxamVINDFIbEFKT183YXZuTUlOSWJWZElVTS1fTk05S2oyUHp5QSIsImV4cCI6MTYyMTc2MTYzMCwiaWF0IjoxNjIxNzU4MDYwfQ.Qgaj13M0X_EXanheaqQOa-gVHLWgnXz0WhOOHady3a7EW08Onl7XxiPclEeJa93gB8YbF94AIc831VZw-upPacLzIPeS0OO2v7bNAG0dvWszAzA7AQMRTy-QGbju8c9cwjFyQfqhdxoeIq-zmGgzrhGGHhaIEN-k3E9-3IjuY-YPE_3hSErpIhIY1cJBtSTix03JJWBx8k7OHuddYCDXItuFgpflTlcqbyJL719IpriLmxrgc8oC6N0iI4XTctrhhgyiWQ2hyscTQXk11rJZm3xX_4n0nzuhQQwxYOngKLyrqosLchdZ_jdm0z6h8e4T1ezCPCuO2CQpoE12dGmz1A");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    private HttpHeaders getPostErpHeaders() {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT,"salesrecord");
        httpHeaders.set(AUTHORIZATION, "Bearer eeyJraWQiOiI5NTc5NmFmZDU2MTA0Y2M4YjIxZDA2MjFjMzRmYzhkYyIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJBUElHV19URVNUIiwiYXVkIjpbIkVSUCIsIkFQSUdXX1RFU1QiLCI8VkZPPiJdLCJhenAiOiJBUElHV19URVNUIiwiYXV0aF90aW1lIjoxNjIxNzU4MDYwLCJzY29wZSI6WyJJTlRFTlRfc2FsZXNPcmRlck5vdGlmeVJXIiwiVE1GNjIyOlJXIiwiS0NfRVJQX1JXIl0sImlzcyI6Imh0dHBzOlwvXC92Zm8temRzLXNndy1pc2d3LXNzbC16ZW50aXR5LXNndy5hcHBzLnJob2NwLm5vbi1wcm9kLWNsb3VkMS5pdGNsb3VkLmxvY2FsLnZvZGFmb25lLm9tIiwiZXh0ZXJuYWxBdXRob3JpemF0aW9uIjoiQmVhcmVyIGV5SjBlWEFpT2lKS1YxUWlMQ0poYkdjaU9pSlNVekkxTmlJc0luZzFkQ0k2SW01UGJ6TmFSSEpQUkZoRlN6RnFTMWRvV0hOc1NGSmZTMWhGWnlJc0ltdHBaQ0k2SW01UGJ6TmFSSEpQUkZoRlN6RnFTMWRvV0hOc1NGSmZTMWhGWnlKOS5leUpoZFdRaU9pSm9kSFJ3Y3pvdkwzTnBkREF3TVdFMlpUUm1ZVEl3TmpZM01USmpPVFZrWlhaaGIzTXVZMnh2ZFdSaGVDNWtlVzVoYldsamN5NWpiMjBpTENKcGMzTWlPaUpvZEhSd2N6b3ZMM04wY3k1M2FXNWtiM2R6TG01bGRDOWpNVGxqWlRNeE1pMHhabUl6TFRRM1pqSXRPV1JpTVMwNU1HSTBNRFF4WlRZMU5tVXZJaXdpYVdGMElqb3hOakl4TnpVM056WXhMQ0p1WW1ZaU9qRTJNakUzTlRjM05qRXNJbVY0Y0NJNk1UWXlNVGMyTVRZMk1Td2lZV2x2SWpvaVJUSmFaMWxMWjBsaE5XbDVZMll6WVVvMVVIVlFPRFYxY2xoWU5FUjNRVDBpTENKaGNIQnBaQ0k2SWprMFlqTmhPVFUwTFRreU0ySXRORGd6TmkwNU5EVXhMVGsxTUdZd016RXpaamRsWkNJc0ltRndjR2xrWVdOeUlqb2lNU0lzSW1sa2NDSTZJbWgwZEhCek9pOHZjM1J6TG5kcGJtUnZkM011Ym1WMEwyTXhPV05sTXpFeUxURm1Zak10TkRkbU1pMDVaR0l4TFRrd1lqUXdOREZsTmpVMlpTOGlMQ0p2YVdRaU9pSXpOMk5qWkdVeFlpMWlaRFV5TFRRek1ERXRZV014TUMxa1lUaGpabUU1TWpNek1HRWlMQ0p5YUNJNklqQXVRVk5GUVVWMVQyTjNZazFtT0d0bFpITmFRekJDUWpWc1lteFRjSE0xVVRkcmFscEpiRVpIVmtSM1RWUTVMVEJvUVVGQkxpSXNJbk4xWWlJNklqTTNZMk5rWlRGaUxXSmtOVEl0TkRNd01TMWhZekV3TFdSaE9HTm1ZVGt5TXpNd1lTSXNJblJwWkNJNkltTXhPV05sTXpFeUxURm1Zak10TkRkbU1pMDVaR0l4TFRrd1lqUXdOREZsTmpVMlpTSXNJblYwYVNJNkluUkVhMWt6YUZBd2VFVXlZMFpUVTJ4TGR6Tm1RVUVpTENKMlpYSWlPaUl4TGpBaWZRLmpJWVloVHY5S29QSk81czVUWTZzd1VGWEdUdnVLYWlLRDlzZ2ZwM0NrRTZEdUNXZmZmS091SktORjNJQjlaMnR4Qk5jWEtueGh3VXJtUUpXTTVUQ0I3V29jMjdRR05aeV9TYVRkellTVVNpZU1rNkNVZmExX3ZOV1dKUXZDbXAtWEtTQVZ5T2lPbFlnOWZaSUtubnFJYTBsUEhad0h1NnktUUlScC1CallJS2R5cmp4QkFSZjJBR2xyRHJhX2lEMWl4d1pFUmdmd2pQaEF3elRBaGhOWl9MOTEzT2RxYUx6UlhQUXVEc3lHTHRrQWdsbk94TWtXTE1vUG1XbVpENlY1c0ZGV2JCcnluMzdOQmZKTmh5b2szNk5hWEtuWlI1V1BfalBnTTZzQnNpZmZkZlkxamVINDFIbEFKT183YXZuTUlOSWJWZElVTS1fTk05S2oyUHp5QSIsImV4cCI6MTYyMTc2MTYzMCwiaWF0IjoxNjIxNzU4MDYwfQ.Qgaj13M0X_EXanheaqQOa-gVHLWgnXz0WhOOHady3a7EW08Onl7XxiPclEeJa93gB8YbF94AIc831VZw-upPacLzIPeS0OO2v7bNAG0dvWszAzA7AQMRTy-QGbju8c9cwjFyQfqhdxoeIq-zmGgzrhGGHhaIEN-k3E9-3IjuY-YPE_3hSErpIhIY1cJBtSTix03JJWBx8k7OHuddYCDXItuFgpflTlcqbyJL719IpriLmxrgc8oC6N0iI4XTctrhhgyiWQ2hyscTQXk11rJZm3xX_4n0nzuhQQwxYOngKLyrqosLchdZ_jdm0z6h8e4T1ezCPCuO2CQpoE12dGmz1A");
        httpHeaders.set("Customer-Info", "DistributionChannel=App, Customer=\"9159953048513681988\"");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    private HttpHeaders getPostVoucherOrderHeaders() {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT,"voucherorder");
        httpHeaders.set(AUTHORIZATION, "Bearer blah");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    private HttpHeaders getPatchBssHeaders() {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT,"salesorder");
        httpHeaders.set(AUTHORIZATION, "Bearer blah");
        httpHeaders.set(VF_CUSTOMER_ID, "test");
        httpHeaders.set(VF_ORDERITEM, "test");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    private HttpHeaders getPatchBssChangeOfferingHeaders() {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT,"salesorder");
        httpHeaders.set(AUTHORIZATION, "Bearer blah");
        httpHeaders.set(VF_ORDERITEM, "changeorderItem");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    private HttpHeaders getPatchBssBillingAccountHeaders() {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT,"salesorder");
        httpHeaders.set(AUTHORIZATION, "Bearer blah");
        httpHeaders.set(VF_SALES_ORDER_ID, "333");
        httpHeaders.set(VF_BILLINGACCOUNTID, "4444");
        httpHeaders.set(VF_ORDERITEM, "test");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    private HttpHeaders getPatchOcsHeaders() {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(VF_SYSTEM_INTENT,"subscription");
        httpHeaders.set(AUTHORIZATION, "Bearer blah");
        httpHeaders.set(VF_SUBSCRIBER_ID, "123456");
        httpHeaders.addAll(getMandatoryHeaders());
        return httpHeaders;
    }

    @Test
    void shouldReturn_200_If_PATCH_ChangeOffering() throws Exception {

        final ProductOrderUpdate productOrderUpdate = getProductOrderUpdate();
        final ProductOrderMessage productOrderMessage = getSuccessfulProductOrderMessage();

        final ArgumentCaptor<Map<String, Object>> workerHeaderCapture = ArgumentCaptor.forClass(Map.class);

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(objectMapper.writeValueAsBytes(productOrderMessage));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/productOrder/1/")
                        .content(objectMapper.writeValueAsString(productOrderUpdate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPatchBssChangeOfferingHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        verify(producerTemplate).sendBodyAndHeaders(eq(REQUEST_QUEUE), any(Object.class), workerHeaderCapture.capture());

        assertThat(workerHeaderCapture.getAllValues().get(0)).contains(
                entry(REQUEST_TYPE, "PATCH"),
                entry(VF_ORDERITEM, "changeorderItem"),
                entry(VF_SYSTEM_INTENT, "salesorder")
        );
    }

    //    @Test
    void shouldReturn_400_If_GET_CorrectParams_WorkerErrorOccurs() throws Exception {

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(getErrorResponse());

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/1")
                        .contentType(APPLICATION_JSON)
                        .headers(getOcsHeaders())
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertErrorBodyReturned(result, objectMapper);
    }

    @Test
    void shouldReturn_400_If_LIST_CorrectParams_WorkerErrorOccurs() throws Exception {

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(getErrorResponse());

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/productOrder/")
                        .contentType(APPLICATION_JSON)
                        .headers(getOcsHeaders())
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertErrorBodyReturned(result, objectMapper);

    }

    @Test
    void shouldReturn_400_If_PATCH_CorrectParams_WorkerErrorOccurs() throws Exception {

        final ProductOrderUpdate productOrderUpdate = getProductOrderUpdate();

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(getErrorResponse());

        final MvcResult result =mockMvc.perform(MockMvcRequestBuilders.patch("/productOrder/1/")
                        .content(objectMapper.writeValueAsString(productOrderUpdate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPatchBssHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertErrorBodyReturned(result, objectMapper);
    }

    @Test
    void shouldReturn_400_If_POST_CorrectParams_WorkerErrorOccurs() throws Exception {

        final ProductOrderCreate productOrderCreate = getProductOrderCreate();

        when(consumerTemplate.receiveBody(contains(RESPONSE_QUEUE), eq(25000L), eq(byte[].class))).thenReturn(getErrorResponse());


        final MvcResult result =   mockMvc.perform(MockMvcRequestBuilders.post("/productOrder/")
                        .content(objectMapper.writeValueAsString(productOrderCreate))
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("CurrentUser", "Test"))
                        .headers(getPostOcsHeaders())
                        .accept(APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertErrorBodyReturned(result, objectMapper);
    }

    private ProductOrderMessage getCustomerMessageError(final HttpStatus httpStatus) {
        final ProductOrderMessage customerMessage = new ProductOrderMessage();

        customerMessage.setCorrelationId(Optional.of(X_COR_ID));
        customerMessage.setHttpStatus(httpStatus);
        customerMessage.setError(getError());

        return customerMessage;
    }

    private byte[] getErrorResponse() throws JsonProcessingException {

        return objectMapper.writeValueAsBytes(getCustomerMessageError(HttpStatus.BAD_REQUEST));
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
