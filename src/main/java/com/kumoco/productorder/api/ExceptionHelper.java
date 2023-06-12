package com.kumoco.productorder.api;

import com.kumoco.productorder.model.tmf.Error;
import com.kumoco.productorder.model.tmf.ErrorItem;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.RuntimeCamelException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;

import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ERROR_CODE_001;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ERROR_TIMEOUT;
import static com.kumoco.productorder.util.RequestHeadersHelper.addMandatoryAPIGWHeadersToResponse;

@ControllerAdvice
@Log4j2
public class ExceptionHelper {

    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
    @ExceptionHandler(RuntimeCamelException.class)
    public ResponseEntity<Error> handleCamelTimeoutErrors(final HttpServletRequest request) {

        return new ResponseEntity<>(new Error()
                .code(String.valueOf(HttpStatus.FAILED_DEPENDENCY.value()))
                .reason(HttpStatus.FAILED_DEPENDENCY.getReasonPhrase())
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
                .errorItem(Collections.singletonList(
                        new ErrorItem()
                                .code(ERROR_CODE_001)
                                .text(ERROR_TIMEOUT))),
                addMandatoryAPIGWHeadersToResponse(request),
                HttpStatus.FAILED_DEPENDENCY);
    }
}
