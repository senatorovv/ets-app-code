package com.kumoco.productorder.message;

import com.kumoco.productorder.model.tmf.CancelProductOrder;
import com.kumoco.productorder.model.tmf.Error;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class CancelProductOrderMessage {

    private CancelProductOrder cancelProductOrder;

    private HttpStatus httpStatus;

    private Error error;

    private JSONObject message;

    private Optional<String> correlationId;
    
    private String instanceId;
}
