package com.kumoco.productorder.model.tmf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * action to be performed on the product
 */
public enum OrderItemActionType {
  
  ADD("add"),
  
  MODIFY("modify"),
  
  DELETE("delete"),
  
  NOCHANGE("noChange");

  private String value;

  OrderItemActionType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static OrderItemActionType fromValue(String value) {
    for (OrderItemActionType b : OrderItemActionType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    return null;
  }
}

