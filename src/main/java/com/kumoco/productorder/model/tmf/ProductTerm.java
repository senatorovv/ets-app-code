package com.kumoco.productorder.model.tmf;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

/**
 * Description of a productTerm linked to this product. This represent a commitment with a duration
 */
@ApiModel(description = "Description of a productTerm linked to this product. This represent a commitment with a duration")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-15T16:31:37.388570+01:00[Europe/London]")

@Validated
public class ProductTerm   {
  @JsonProperty("description")
  private String description;

  @JsonProperty("name")
  private String name;

  @JsonProperty("duration")
  private Quantity duration;

  @JsonProperty("validFor")
  private TimePeriod validFor;

  @JsonProperty("@baseType")
  private String atBaseType;

  @JsonProperty("@schemaLocation")
  private URI atSchemaLocation;

  @JsonProperty("@type")
  private String atType;

  public ProductTerm description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of the productTerm
   * @return description
  */
  @ApiModelProperty(value = "Description of the productTerm")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ProductTerm name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the productTerm
   * @return name
  */
  @ApiModelProperty(value = "Name of the productTerm")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ProductTerm duration(Quantity duration) {
    this.duration = duration;
    return this;
  }

  /**
   * Get duration
   * @return duration
  */
  @ApiModelProperty(value = "")

  @Valid

  public Quantity getDuration() {
    return duration;
  }

  public void setDuration(Quantity duration) {
    this.duration = duration;
  }

  public ProductTerm validFor(TimePeriod validFor) {
    this.validFor = validFor;
    return this;
  }

  /**
   * Get validFor
   * @return validFor
  */
  @ApiModelProperty(value = "")

  @Valid

  public TimePeriod getValidFor() {
    return validFor;
  }

  public void setValidFor(TimePeriod validFor) {
    this.validFor = validFor;
  }

  public ProductTerm atBaseType(String atBaseType) {
    this.atBaseType = atBaseType;
    return this;
  }

  /**
   * When sub-classing, this defines the super-class
   * @return atBaseType
  */
  @ApiModelProperty(value = "When sub-classing, this defines the super-class")


  public String getAtBaseType() {
    return atBaseType;
  }

  public void setAtBaseType(String atBaseType) {
    this.atBaseType = atBaseType;
  }

  public ProductTerm atSchemaLocation(URI atSchemaLocation) {
    this.atSchemaLocation = atSchemaLocation;
    return this;
  }

  /**
   * A URI to a JSON-Schema file that defines additional attributes and relationships
   * @return atSchemaLocation
  */
  @ApiModelProperty(value = "A URI to a JSON-Schema file that defines additional attributes and relationships")

  @Valid

  public URI getAtSchemaLocation() {
    return atSchemaLocation;
  }

  public void setAtSchemaLocation(URI atSchemaLocation) {
    this.atSchemaLocation = atSchemaLocation;
  }

  public ProductTerm atType(String atType) {
    this.atType = atType;
    return this;
  }

  /**
   * When sub-classing, this defines the sub-class entity name
   * @return atType
  */
  @ApiModelProperty(value = "When sub-classing, this defines the sub-class entity name")


  public String getAtType() {
    return atType;
  }

  public void setAtType(String atType) {
    this.atType = atType;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductTerm productTerm = (ProductTerm) o;
    return Objects.equals(this.description, productTerm.description) &&
        Objects.equals(this.name, productTerm.name) &&
        Objects.equals(this.duration, productTerm.duration) &&
        Objects.equals(this.validFor, productTerm.validFor) &&
        Objects.equals(this.atBaseType, productTerm.atBaseType) &&
        Objects.equals(this.atSchemaLocation, productTerm.atSchemaLocation) &&
        Objects.equals(this.atType, productTerm.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, name, duration, validFor, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductTerm {\n");
    
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    duration: ").append(toIndentedString(duration)).append("\n");
    sb.append("    validFor: ").append(toIndentedString(validFor)).append("\n");
    sb.append("    atBaseType: ").append(toIndentedString(atBaseType)).append("\n");
    sb.append("    atSchemaLocation: ").append(toIndentedString(atSchemaLocation)).append("\n");
    sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
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

