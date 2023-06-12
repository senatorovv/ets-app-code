package com.kumoco.productorder.api;

import com.kumoco.productorder.model.tmf.Error;
import com.kumoco.productorder.model.tmf.ErrorItem;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Iterator;

import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.COMMA_SPACE;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ERROR_CODE_003;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.ERROR_NOT_ACCEPTABLE;
import static com.kumoco.productorder.model.tmf.custom.ProductOrderConstants.HYPHEN_SPACE;
import static org.springframework.http.HttpStatus.FAILED_DEPENDENCY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ControllerAdvice
@Log4j2
public class RequestExceptionalHelper extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(final HttpMediaTypeNotAcceptableException ex,
                                                                   final HttpHeaders headers, final HttpStatus status,
                                                                   final WebRequest request) {

        Error error = new Error()
                .code(String.valueOf(FAILED_DEPENDENCY.value()))
                .reason(FAILED_DEPENDENCY.getReasonPhrase())
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
                .errorItem(Collections.singletonList(
                        new ErrorItem()
                                .code(ERROR_CODE_003)
                                .text(ERROR_NOT_ACCEPTABLE)));

        getAllHeaders(request);

        return ResponseEntity.status(FAILED_DEPENDENCY)
                .contentType(APPLICATION_JSON)
                .body(error);
    }

    private void getAllHeaders(final WebRequest request) {

        final Iterator<String> headerNames = request.getHeaderNames();

        StringBuilder sb = new StringBuilder();

        while(headerNames.hasNext()) {

            String headerName = headerNames.next();
            sb.append(headerName + HYPHEN_SPACE + request.getHeader(headerName) + COMMA_SPACE);
        }
        log.info("The 424/406 validation has failed with the given headers: " + sb.deleteCharAt(sb.lastIndexOf(COMMA_SPACE)));
    }
}
