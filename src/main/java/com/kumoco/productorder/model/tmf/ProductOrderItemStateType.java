package com.kumoco.productorder.model.tmf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Possible values for the state of the product order item
 */
public enum ProductOrderItemStateType {
  
  ACKNOWLEDGED("acknowledged"),
  
  REJECTED("rejected"),
  
  PENDING("pending"),
  
  HELD("held"),
  
  INPROGRESS("inProgress"),
  
  CANCELLED("cancelled"),
  
  COMPLETED("completed"),
  
  FAILED("failed"),
  
  ASSESSINGCANCELLATION("assessingCancellation"),
  
  PENDINGCANCELLATION("pendingCancellation");

  private String value;

  ProductOrderItemStateType(String value) {
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
  public static ProductOrderItemStateType fromValue(String value) {
    for (ProductOrderItemStateType b : ProductOrderItemStateType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    return null;
  }
}

