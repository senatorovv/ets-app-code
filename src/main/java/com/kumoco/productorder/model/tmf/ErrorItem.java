package com.kumoco.productorder.model.tmf;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.Objects;

/**
 * errorItem object
 */
@ApiModel(description = "errorItem object")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-15T16:31:37.388570+01:00[Europe/London]")

@Validated
public class ErrorItem   {
  @JsonProperty("code")
  private String code;

  @JsonProperty("text")
  private String text;

  @JsonProperty("severity")
  private String severity;

  @JsonProperty("characteristic")
  private Characteristic characteristic;

  public ErrorItem code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Application relevant detail, defined in the API or a common list.
   * @return code
  */
  @ApiModelProperty(value = "Application relevant detail, defined in the API or a common list.")


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public ErrorItem text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Error text
   * @return text
  */
  @ApiModelProperty(value = "Error text")


  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public ErrorItem severity(String severity) {
    this.severity = severity;
    return this;
  }

  /**
   * More details and corrective actions related to the error which can be shown to a client user.
   * @return severity
  */
  @ApiModelProperty(value = "More details and corrective actions related to the error which can be shown to a client user.")


  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

  public ErrorItem characteristic(Characteristic characteristic) {
    this.characteristic = characteristic;
    return this;
  }

  /**
   * Get characteristic
   * @return characteristic
  */
  @ApiModelProperty(value = "")

  @Valid

  public Characteristic getCharacteristic() {
    return characteristic;
  }

  public void setCharacteristic(Characteristic characteristic) {
    this.characteristic = characteristic;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorItem errorItem = (ErrorItem) o;
    return Objects.equals(this.code, errorItem.code) &&
        Objects.equals(this.text, errorItem.text) &&
        Objects.equals(this.severity, errorItem.severity) &&
        Objects.equals(this.characteristic, errorItem.characteristic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, text, severity, characteristic);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorItem {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    severity: ").append(toIndentedString(severity)).append("\n");
    sb.append("    characteristic: ").append(toIndentedString(characteristic)).append("\n");
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

