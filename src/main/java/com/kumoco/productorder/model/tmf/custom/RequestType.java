package com.kumoco.productorder.model.tmf.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RequestType {

    CANCEL("CANCEL"),
    DELETE("DELETE"),
    GET("GET"),
    LIST("LIST"),
    PATCH("PATCH"),
    POST("POST");

    @Getter
    private String httpRequest;

    @Override
    public String toString() {
        return this.httpRequest;
    }
}
