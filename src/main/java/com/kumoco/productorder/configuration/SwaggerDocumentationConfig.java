package com.kumoco.productorder.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@javax.annotation.Generated(value = "com.kumoco.productorder.codegen.v3.generators.java.SpringCodegen", date = "2020-06-01T18:11:48.790+01:00[Europe/London]")
@Configuration
public class SwaggerDocumentationConfig {

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Product Ordering")
            .description("**TMF API Reference : TMF 622 - Product Ordering Management**  **Release : 19.0 - June 2019**  The Product Ordering API provides a standardized mechanism for placing a product order with all of the necessary order parameters. The API consists of a simple set of operations that interact with CRM/Order Negotiation systems in a consistent manner. A product order is created based on a product offer that is defined in a catalog. The product offer identifies the product or set of products that are available to a customer, and includes characteristics such as pricing, product options and market. This API provide a task based resource to request order cancellation.  The product order references the product offer and identifies any specific requests made by the customer.  **Product Order resource** A Product Order is a type of order which can be used to place an order between a customer and a service provider or between a service provider and a partner and vice versa. Main Product Order attributes are its identifier, state, priority category (mass market, Enterprise, etc.) related dates (start, completion, etc.), related billing account, related parties and order items. Main Order Items (aka order lines) attributes are the ordered offering and product characteristics with the related action to be performed (e.g. add or delete the products), state, location information for delivery, order item price and price alteration.  Product Order API performs the following operations on product order :     -Retrieval of a product order or a collection of product orders depending on filter criteria     -Partial update of a product order (including updating rules)    -Creation of a product order (including default values and creation rules)    -Deletion of product order (for administration purposes)     -Notification of events on product order.  **cancelProductOrder resource** This resource is used to request a product order cancellation. Product Order API performs the following operations on product order :     -Retrieval of a cancel product order or a collection of cancel product orders     -Creation of a cancel product order     -Notification of events on cancel product order.   Copyright © TM Forum 2019. All Rights Reserved   ")
            .license("")
            .licenseUrl("http://unlicense.org")
            .termsOfServiceUrl("")
            .version("4.0.0")
            .contact(new Contact("","", ""))
            .build();
    }

    @Bean
    public Docket customImplementation(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                    .apis(RequestHandlerSelectors.basePackage("com.kumoco.productorder.api"))
                    .build()
                .directModelSubstitute(org.threeten.bp.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(org.threeten.bp.OffsetDateTime.class, java.util.Date.class)
                .apiInfo(apiInfo());
    }

}
