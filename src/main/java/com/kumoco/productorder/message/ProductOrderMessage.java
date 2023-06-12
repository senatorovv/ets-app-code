package com.kumoco.productorder.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kumoco.productorder.model.tmf.ProductOrder;
import com.kumoco.productorder.model.tmf.Error;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class ProductOrderMessage {

    @JsonIgnore
    private List<ProductOrder> productOrders;

    private HttpStatus httpStatus;

    private Error error;

    private JSONObject message;

    @JsonIgnore
    private Map<String, String> cookies;

    private Optional<String> correlationId;
    
    private String instanceId;
}
