package com.kumoco.productorder.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.util.concurrent.ThreadPoolRejectedPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

import static org.apache.camel.component.jms.JmsComponent.jmsComponentAutoAcknowledge;

/**
 * ActiveMQ Camel Configuration class
 */
@Configuration
public class JmsConfiguration {

    public static final String URL = "/test/*";
    public static final String MY_DEFAULT_PROFILE = "myDefaultProfile";
    public static final String ACTIVEMQ = "activemq";

    @Bean
    public ServletRegistrationBean camelServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), URL);
        registration.setName("CamelServlet");
        return registration;
    }

    @Bean
    CamelContextConfiguration contextConfiguration(@Value("${activemq.user}") String user,
                                                   @Value("${activemq.password}") String password,
                                                   @Value("${activemq.url-dev}") String url) {
        return new CamelContextConfiguration() {

            @Override
            public void beforeApplicationStart(CamelContext context) {

                ThreadPoolProfile threadPoolProfile = new ThreadPoolProfile();
                threadPoolProfile.setId(MY_DEFAULT_PROFILE);
                threadPoolProfile.setPoolSize(10);
                threadPoolProfile.setMaxPoolSize(15);
                threadPoolProfile.setMaxQueueSize(250);
                threadPoolProfile.setKeepAliveTime(25L);
                threadPoolProfile.setRejectedPolicy(ThreadPoolRejectedPolicy.Abort);
                context.getExecutorServiceManager().registerThreadPoolProfile(threadPoolProfile);
                ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
                context.addComponent(ACTIVEMQ, jmsComponentAutoAcknowledge(connectionFactory));
            }

            @Override
            public void afterApplicationStart(CamelContext camelContext) {
            }
        };
    }
}
