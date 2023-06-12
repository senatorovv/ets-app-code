package com.kumoco.productorder.exception;

import lombok.extern.log4j.Log4j2;
import org.apache.camel.RuntimeCamelException;

import java.util.Map;

@Log4j2
public class TimeoutException extends RuntimeCamelException {

    private static final long serialVersionUID = 7607128449240157466L;

    public TimeoutException(final Map<String, Object> paramMap) {

        super();
        log.error("Returning 424 HTTP Status code - the timeout has occurred for the request with ## headers [{}] ", paramMap);

    }
}