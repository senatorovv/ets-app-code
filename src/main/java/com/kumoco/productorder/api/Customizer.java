package com.kumoco.productorder.api;

import lombok.extern.log4j.Log4j2;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class Customizer {

    @Bean
    TomcatConnectorCustomizer headerRejectionCustomizer() {

        log.info("Setting the property reject illegal header to false");

        return (connector) ->
                ((AbstractHttp11Protocol<?>)connector.getProtocolHandler()).setRejectIllegalHeader(false);
    }
}
