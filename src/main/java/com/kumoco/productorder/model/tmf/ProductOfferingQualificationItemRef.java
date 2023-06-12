package com.kumoco.productorder.model.tmf;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Objects;

/**
 * It&#39;s a productOfferingQualification item that has been executed previously.
 */
@ApiModel(description = "It's a productOfferingQualification item that has been executed previously.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-15T16:31:37.388570+01:00[Europe/London]")

@Validated
public class ProductOfferingQualificationItemRef   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("href")
  private String href;

  @JsonProperty("name")
  private String name;

  @JsonProperty("productOfferingQualificationHref")
  private String productOfferingQualificationHref;

  @JsonProperty("productOfferingQualificationId")
  private String productOfferingQualificationId;

  @JsonProperty("productOfferingQualificationName")
  private String productOfferingQualificationName;

  @JsonProperty("@baseType")
  private String atBaseType;

  @JsonProperty("@schemaLocation")
  private URI atSchemaLocation;

  @JsonProperty("@type")
  private String atType;

  @JsonProperty("@referredType")
  private String atReferredType;

  public ProductOfferingQualificationItemRef id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Id of an item of a product offering qualification
   * @return id
  */
  @ApiModelProperty(required = true, value = "Id of an item of a product offering qualification")
  @NotNull


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ProductOfferingQualificationItemRef href(String href) {
    this.href = href;
    return this;
  }

  /**
   * Reference of the related entity.
   * @return href
  */
  @ApiModelProperty(value = "Reference of the related entity.")


  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public ProductOfferingQualificationItemRef name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the related entity.
   * @return name
  */
  @ApiModelProperty(value = "Name of the related entity.")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ProductOfferingQualificationItemRef productOfferingQualificationHref(String productOfferingQualificationHref) {
    this.productOfferingQualificationHref = productOfferingQualificationHref;
    return this;
  }

  /**
   * Reference of the related entity.
   * @return productOfferingQualificationHref
  */
  @ApiModelProperty(value = "Reference of the related entity.")


  public String getProductOfferingQualificationHref() {
    return productOfferingQualificationHref;
  }

  public void setProductOfferingQualificationHref(String productOfferingQualificationHref) {
    this.productOfferingQualificationHref = productOfferingQualificationHref;
  }

  public ProductOfferingQualificationItemRef productOfferingQualificationId(String productOfferingQualificationId) {
    this.productOfferingQualificationId = productOfferingQualificationId;
    return this;
  }

  /**
   * Unique identifier of a related entity.
   * @return productOfferingQualificationId
  */
  @ApiModelProperty(required = true, value = "Unique identifier of a related entity.")
  @NotNull


  public String getProductOfferingQualificationId() {
    return productOfferingQualificationId;
  }

  public void setProductOfferingQualificationId(String productOfferingQualificationId) {
    this.productOfferingQualificationId = productOfferingQualificationId;
  }

  public ProductOfferingQualificationItemRef productOfferingQualificationName(String productOfferingQualificationName) {
    this.productOfferingQualificationName = productOfferingQualificationName;
    return this;
  }

  /**
   * Name of the related entity.
   * @return productOfferingQualificationName
  */
  @ApiModelProperty(value = "Name of the related entity.")


  public String getProductOfferingQualificationName() {
    return productOfferingQualificationName;
  }

  public void setProductOfferingQualificationName(String productOfferingQualificationName) {
    this.productOfferingQualificationName = productOfferingQualificationName;
  }

  public ProductOfferingQualificationItemRef atBaseType(String atBaseType) {
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

  public ProductOfferingQualificationItemRef atSchemaLocation(URI atSchemaLocation) {
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

  public ProductOfferingQualificationItemRef atType(String atType) {
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

  public ProductOfferingQualificationItemRef atReferredType(String atReferredType) {
    this.atReferredType = atReferredType;
    return this;
  }

  /**
   * The actual type of the target instance when needed for disambiguation.
   * @return atReferredType
  */
  @ApiModelProperty(value = "The actual type of the target instance when needed for disambiguation.")


  public String getAtReferredType() {
    return atReferredType;
  }

  public void setAtReferredType(String atReferredType) {
    this.atReferredType = atReferredType;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductOfferingQualificationItemRef productOfferingQualificationItemRef = (ProductOfferingQualificationItemRef) o;
    return Objects.equals(this.id, productOfferingQualificationItemRef.id) &&
        Objects.equals(this.href, productOfferingQualificationItemRef.href) &&
        Objects.equals(this.name, productOfferingQualificationItemRef.name) &&
        Objects.equals(this.productOfferingQualificationHref, productOfferingQualificationItemRef.productOfferingQualificationHref) &&
        Objects.equals(this.productOfferingQualificationId, productOfferingQualificationItemRef.productOfferingQualificationId) &&
        Objects.equals(this.productOfferingQualificationName, productOfferingQualificationItemRef.productOfferingQualificationName) &&
        Objects.equals(this.atBaseType, productOfferingQualificationItemRef.atBaseType) &&
        Objects.equals(this.atSchemaLocation, productOfferingQualificationItemRef.atSchemaLocation) &&
        Objects.equals(this.atType, productOfferingQualificationItemRef.atType) &&
        Objects.equals(this.atReferredType, productOfferingQualificationItemRef.atReferredType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, href, name, productOfferingQualificationHref, productOfferingQualificationId, productOfferingQualificationName, atBaseType, atSchemaLocation, atType, atReferredType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductOfferingQualificationItemRef {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    productOfferingQualificationHref: ").append(toIndentedString(productOfferingQualificationHref)).append("\n");
    sb.append("    productOfferingQualificationId: ").append(toIndentedString(productOfferingQualificationId)).append("\n");
    sb.append("    productOfferingQualificationName: ").append(toIndentedString(productOfferingQualificationName)).append("\n");
    sb.append("    atBaseType: ").append(toIndentedString(atBaseType)).append("\n");
    sb.append("    atSchemaLocation: ").append(toIndentedString(atSchemaLocation)).append("\n");
    sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
    sb.append("    atReferredType: ").append(toIndentedString(atReferredType)).append("\n");
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

