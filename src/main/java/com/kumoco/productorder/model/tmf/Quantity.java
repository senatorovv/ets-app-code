package com.kumoco.productorder.model.tmf;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * An amount in a given unit
 */
@ApiModel(description = "An amount in a given unit")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-15T16:31:37.388570+01:00[Europe/London]")

@Validated
public class Quantity   {
  @JsonProperty("amount")
  private Float amount = 1.0f;

  @JsonProperty("units")
  private String units;

  public Quantity amount(Float amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Numeric value in a given unit
   * @return amount
  */
  @ApiModelProperty(value = "Numeric value in a given unit")


  public Float getAmount() {
    return amount;
  }

  public void setAmount(Float amount) {
    this.amount = amount;
  }

  public Quantity units(String units) {
    this.units = units;
    return this;
  }

  /**
   * Unit
   * @return units
  */
  @ApiModelProperty(value = "Unit")


  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Quantity quantity = (Quantity) o;
    return Objects.equals(this.amount, quantity.amount) &&
        Objects.equals(this.units, quantity.units);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, units);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Quantity {\n");
    
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    units: ").append(toIndentedString(units)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

