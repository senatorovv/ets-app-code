package com.kumoco.productorder.model.tmf;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A Product Order is a type of order which  can  be used to place an order between a customer and a service provider or between a service provider and a partner and vice versa,
 */
@ApiModel(description = "A Product Order is a type of order which  can  be used to place an order between a customer and a service provider or between a service provider and a partner and vice versa,")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-15T16:31:37.388570+01:00[Europe/London]")

@Validated
public class ProductOrder   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("href")
  private String href;

  @JsonProperty("cancellationDate")
  private ZonedDateTime cancellationDate;

  @JsonProperty("cancellationReason")
  private String cancellationReason;

  @JsonProperty("category")
  private String category;

  @JsonProperty("completionDate")
  private ZonedDateTime completionDate;

  @JsonProperty("description")
  private String description;

  @JsonProperty("expectedCompletionDate")
  private ZonedDateTime expectedCompletionDate;

  @JsonProperty("externalId")
  private String externalId;

  @JsonProperty("notificationContact")
  private String notificationContact;

  @JsonProperty("orderDate")
  private ZonedDateTime orderDate;

  @JsonProperty("priority")
  private String priority;

  @JsonProperty("requestedCompletionDate")
  private ZonedDateTime requestedCompletionDate;

  @JsonProperty("requestedStartDate")
  private ZonedDateTime requestedStartDate;

  @JsonProperty("agreement")
  @Valid
  private List<AgreementRef> agreement = null;

  @JsonProperty("billingAccount")
  private BillingAccountRef billingAccount;

  @JsonProperty("channel")
  @Valid
  private List<RelatedChannel> channel = null;

  @JsonProperty("note")
  @Valid
  private List<Note> note = null;

  @JsonProperty("orderTotalPrice")
  @Valid
  private List<OrderPrice> orderTotalPrice = null;

  @JsonProperty("payment")
  @Valid
  private List<PaymentRef> payment = null;

  @JsonProperty("productOfferingQualification")
  @Valid
  private List<ProductOfferingQualificationRef> productOfferingQualification = null;

  @JsonProperty("productOrderCharacteristic")
  @Valid
  private List<Characteristic> productOrderCharacteristic = null;

  @JsonProperty("productOrderItem")
  @Valid
  private List<ProductOrderItem> productOrderItem = new ArrayList<>();

  @JsonProperty("quote")
  @Valid
  private List<QuoteRef> quote = null;

  @JsonProperty("relatedParty")
  @Valid
  private List<RelatedParty> relatedParty = null;

  @JsonProperty("state")
  private String state;

  @JsonProperty("@baseType")
  private String atBaseType;

  @JsonProperty("@schemaLocation")
  private URI atSchemaLocation;

  @JsonProperty("@type")
  private String atType;

  public ProductOrder id(String id) {
    this.id = id;
    return this;
  }

  /**
   * ID created on repository side (OM system)
   * @return id
  */
  @ApiModelProperty(value = "ID created on repository side (OM system)")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ProductOrder href(String href) {
    this.href = href;
    return this;
  }

  /**
   * Hyperlink to access the order
   * @return href
  */
  @ApiModelProperty(value = "Hyperlink to access the order")


  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public ProductOrder cancellationDate(ZonedDateTime cancellationDate) {
    this.cancellationDate = cancellationDate;
    return this;
  }

  /**
   * Date when the order is cancelled. This is used when order is cancelled. 
   * @return cancellationDate
  */
  @ApiModelProperty(value = "Date when the order is cancelled. This is used when order is cancelled. ")

  @Valid

  public ZonedDateTime getCancellationDate() {
    return cancellationDate;
  }

  public void setCancellationDate(ZonedDateTime cancellationDate) {
    this.cancellationDate = cancellationDate;
  }

  public ProductOrder cancellationReason(String cancellationReason) {
    this.cancellationReason = cancellationReason;
    return this;
  }

  /**
   * Reason why the order is cancelled. This is used when order is cancelled. 
   * @return cancellationReason
  */
  @ApiModelProperty(value = "Reason why the order is cancelled. This is used when order is cancelled. ")


  public String getCancellationReason() {
    return cancellationReason;
  }

  public void setCancellationReason(String cancellationReason) {
    this.cancellationReason = cancellationReason;
  }

  public ProductOrder category(String category) {
    this.category = category;
    return this;
  }

  /**
   * Used to categorize the order from a business perspective that can be useful for the OM system (e.g. \"enterprise\", \"residential\", ...)
   * @return category
  */
  @ApiModelProperty(value = "Used to categorize the order from a business perspective that can be useful for the OM system (e.g. \"enterprise\", \"residential\", ...)")


  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public ProductOrder completionDate(ZonedDateTime completionDate) {
    this.completionDate = completionDate;
    return this;
  }

  /**
   * Date when the order was completed
   * @return completionDate
  */
  @ApiModelProperty(value = "Date when the order was completed")

  @Valid

  public ZonedDateTime getCompletionDate() {
    return completionDate;
  }

  public void setCompletionDate(ZonedDateTime completionDate) {
    this.completionDate = completionDate;
  }

  public ProductOrder description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of the product order
   * @return description
  */
  @ApiModelProperty(value = "Description of the product order")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ProductOrder expectedCompletionDate(ZonedDateTime expectedCompletionDate) {
    this.expectedCompletionDate = expectedCompletionDate;
    return this;
  }

  /**
   * Expected delivery date amended by the provider
   * @return expectedCompletionDate
  */
  @ApiModelProperty(value = "Expected delivery date amended by the provider")

  @Valid

  public ZonedDateTime getExpectedCompletionDate() {
    return expectedCompletionDate;
  }

  public void setExpectedCompletionDate(ZonedDateTime expectedCompletionDate) {
    this.expectedCompletionDate = expectedCompletionDate;
  }

  public ProductOrder externalId(String externalId) {
    this.externalId = externalId;
    return this;
  }

  /**
   * ID given by the consumer and only understandable by him (to facilitate his searches afterwards)
   * @return externalId
  */
  @ApiModelProperty(value = "ID given by the consumer and only understandable by him (to facilitate his searches afterwards)")


  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public ProductOrder notificationContact(String notificationContact) {
    this.notificationContact = notificationContact;
    return this;
  }

  /**
   * Contact attached to the order to send back information regarding this order
   * @return notificationContact
  */
  @ApiModelProperty(value = "Contact attached to the order to send back information regarding this order")


  public String getNotificationContact() {
    return notificationContact;
  }

  public void setNotificationContact(String notificationContact) {
    this.notificationContact = notificationContact;
  }

  public ProductOrder orderDate(ZonedDateTime orderDate) {
    this.orderDate = orderDate;
    return this;
  }

  /**
   * Date when the order was created
   * @return orderDate
  */
  @ApiModelProperty(value = "Date when the order was created")

  @Valid

  public ZonedDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(ZonedDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public ProductOrder priority(String priority) {
    this.priority = priority;
    return this;
  }

  /**
   * A way that can be used by consumers to prioritize orders in OM system (from 0 to 4 : 0 is the highest priority, and 4 the lowest)
   * @return priority
  */
  @ApiModelProperty(value = "A way that can be used by consumers to prioritize orders in OM system (from 0 to 4 : 0 is the highest priority, and 4 the lowest)")


  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public ProductOrder requestedCompletionDate(ZonedDateTime requestedCompletionDate) {
    this.requestedCompletionDate = requestedCompletionDate;
    return this;
  }

  /**
   * Requested delivery date from the requestor perspective
   * @return requestedCompletionDate
  */
  @ApiModelProperty(value = "Requested delivery date from the requestor perspective")

  @Valid

  public ZonedDateTime getRequestedCompletionDate() {
    return requestedCompletionDate;
  }

  public void setRequestedCompletionDate(ZonedDateTime requestedCompletionDate) {
    this.requestedCompletionDate = requestedCompletionDate;
  }

  public ProductOrder requestedStartDate(ZonedDateTime requestedStartDate) {
    this.requestedStartDate = requestedStartDate;
    return this;
  }

  /**
   * Order fulfillment start date wished by the requestor. This is used when, for any reason, requestor cannot allow seller to begin to operationally begin the fulfillment before a date. 
   * @return requestedStartDate
  */
  @ApiModelProperty(value = "Order fulfillment start date wished by the requestor. This is used when, for any reason, requestor cannot allow seller to begin to operationally begin the fulfillment before a date. ")

  @Valid

  public ZonedDateTime getRequestedStartDate() {
    return requestedStartDate;
  }

  public void setRequestedStartDate(ZonedDateTime requestedStartDate) {
    this.requestedStartDate = requestedStartDate;
  }

  public ProductOrder agreement(List<AgreementRef> agreement) {
    this.agreement = agreement;
    return this;
  }

  public ProductOrder addAgreementItem(AgreementRef agreementItem) {
    if (this.agreement == null) {
      this.agreement = new ArrayList<>();
    }
    this.agreement.add(agreementItem);
    return this;
  }

  /**
   * A reference to an agreement defined in the context of the product order
   * @return agreement
  */
  @ApiModelProperty(value = "A reference to an agreement defined in the context of the product order")

  @Valid

  public List<AgreementRef> getAgreement() {
    return agreement;
  }

  public void setAgreement(List<AgreementRef> agreement) {
    this.agreement = agreement;
  }

  public ProductOrder billingAccount(BillingAccountRef billingAccount) {
    this.billingAccount = billingAccount;
    return this;
  }

  /**
   * Get billingAccount
   * @return billingAccount
  */
  @ApiModelProperty(value = "")

  @Valid

  public BillingAccountRef getBillingAccount() {
    return billingAccount;
  }

  public void setBillingAccount(BillingAccountRef billingAccount) {
    this.billingAccount = billingAccount;
  }

  public ProductOrder channel(List<RelatedChannel> channel) {
    this.channel = channel;
    return this;
  }

  public ProductOrder addChannelItem(RelatedChannel channelItem) {
    if (this.channel == null) {
      this.channel = new ArrayList<>();
    }
    this.channel.add(channelItem);
    return this;
  }

  /**
   * Get channel
   * @return channel
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<RelatedChannel> getChannel() {
    return channel;
  }

  public void setChannel(List<RelatedChannel> channel) {
    this.channel = channel;
  }

  public ProductOrder note(List<Note> note) {
    this.note = note;
    return this;
  }

  public ProductOrder addNoteItem(Note noteItem) {
    if (this.note == null) {
      this.note = new ArrayList<>();
    }
    this.note.add(noteItem);
    return this;
  }

  /**
   * Get note
   * @return note
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<Note> getNote() {
    return note;
  }

  public void setNote(List<Note> note) {
    this.note = note;
  }

  public ProductOrder orderTotalPrice(List<OrderPrice> orderTotalPrice) {
    this.orderTotalPrice = orderTotalPrice;
    return this;
  }

  public ProductOrder addOrderTotalPriceItem(OrderPrice orderTotalPriceItem) {
    if (this.orderTotalPrice == null) {
      this.orderTotalPrice = new ArrayList<>();
    }
    this.orderTotalPrice.add(orderTotalPriceItem);
    return this;
  }

  /**
   * Get orderTotalPrice
   * @return orderTotalPrice
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<OrderPrice> getOrderTotalPrice() {
    return orderTotalPrice;
  }

  public void setOrderTotalPrice(List<OrderPrice> orderTotalPrice) {
    this.orderTotalPrice = orderTotalPrice;
  }

  public ProductOrder payment(List<PaymentRef> payment) {
    this.payment = payment;
    return this;
  }

  public ProductOrder addPaymentItem(PaymentRef paymentItem) {
    if (this.payment == null) {
      this.payment = new ArrayList<>();
    }
    this.payment.add(paymentItem);
    return this;
  }

  /**
   * Get payment
   * @return payment
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<PaymentRef> getPayment() {
    return payment;
  }

  public void setPayment(List<PaymentRef> payment) {
    this.payment = payment;
  }

  public ProductOrder productOfferingQualification(List<ProductOfferingQualificationRef> productOfferingQualification) {
    this.productOfferingQualification = productOfferingQualification;
    return this;
  }

  public ProductOrder addProductOfferingQualificationItem(ProductOfferingQualificationRef productOfferingQualificationItem) {
    if (this.productOfferingQualification == null) {
      this.productOfferingQualification = new ArrayList<>();
    }
    this.productOfferingQualification.add(productOfferingQualificationItem);
    return this;
  }

  /**
   * Get productOfferingQualification
   * @return productOfferingQualification
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<ProductOfferingQualificationRef> getProductOfferingQualification() {
    return productOfferingQualification;
  }

  public void setProductOfferingQualification(List<ProductOfferingQualificationRef> productOfferingQualification) {
    this.productOfferingQualification = productOfferingQualification;
  }

  public ProductOrder productOrderCharacteristic(List<Characteristic> productOrderCharacteristic) {
    this.productOrderCharacteristic = productOrderCharacteristic;
    return this;
  }

  public ProductOrder addProductOrderCharacteristicItem(Characteristic productOrderCharacteristicItem) {
    if (this.productOrderCharacteristic == null) {
      this.productOrderCharacteristic = new ArrayList<>();
    }
    this.productOrderCharacteristic.add(productOrderCharacteristicItem);
    return this;
  }

  /**
   * Get productOrderCharacteristic
   * @return productOrderCharacteristic
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<Characteristic> getProductOrderCharacteristic() {
    return productOrderCharacteristic;
  }

  public void setProductOrderCharacteristic(List<Characteristic> productOrderCharacteristic) {
    this.productOrderCharacteristic = productOrderCharacteristic;
  }

  public ProductOrder productOrderItem(List<ProductOrderItem> productOrderItem) {
    this.productOrderItem = productOrderItem;
    return this;
  }

  public ProductOrder addProductOrderItemItem(ProductOrderItem productOrderItemItem) {
    this.productOrderItem.add(productOrderItemItem);
    return this;
  }

  /**
   * Get productOrderItem
   * @return productOrderItem
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid
@Size(min=1) 
  public List<ProductOrderItem> getProductOrderItem() {
    return productOrderItem;
  }

  public void setProductOrderItem(List<ProductOrderItem> productOrderItem) {
    this.productOrderItem = productOrderItem;
  }

  public ProductOrder quote(List<QuoteRef> quote) {
    this.quote = quote;
    return this;
  }

  public ProductOrder addQuoteItem(QuoteRef quoteItem) {
    if (this.quote == null) {
      this.quote = new ArrayList<>();
    }
    this.quote.add(quoteItem);
    return this;
  }

  /**
   * Get quote
   * @return quote
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<QuoteRef> getQuote() {
    return quote;
  }

  public void setQuote(List<QuoteRef> quote) {
    this.quote = quote;
  }

  public ProductOrder relatedParty(List<RelatedParty> relatedParty) {
    this.relatedParty = relatedParty;
    return this;
  }

  public ProductOrder addRelatedPartyItem(RelatedParty relatedPartyItem) {
    if (this.relatedParty == null) {
      this.relatedParty = new ArrayList<>();
    }
    this.relatedParty.add(relatedPartyItem);
    return this;
  }

  /**
   * Get relatedParty
   * @return relatedParty
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<RelatedParty> getRelatedParty() {
    return relatedParty;
  }

  public void setRelatedParty(List<RelatedParty> relatedParty) {
    this.relatedParty = relatedParty;
  }

  public ProductOrder state(String state) {
    this.state = state;
    return this;
  }

  /**
   * Tracks the lifecycle status of the product order, such as Acknowledged, Rejected, InProgress, Pending and so on.
   * @return state
  */
  @ApiModelProperty(value = "Tracks the lifecycle status of the product order, such as Acknowledged, Rejected, InProgress, Pending and so on.")


  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public ProductOrder atBaseType(String atBaseType) {
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

  public ProductOrder atSchemaLocation(URI atSchemaLocation) {
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

  public ProductOrder atType(String atType) {
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
    ProductOrder productOrder = (ProductOrder) o;
    return Objects.equals(this.id, productOrder.id) &&
        Objects.equals(this.href, productOrder.href) &&
        Objects.equals(this.cancellationDate, productOrder.cancellationDate) &&
        Objects.equals(this.cancellationReason, productOrder.cancellationReason) &&
        Objects.equals(this.category, productOrder.category) &&
        Objects.equals(this.completionDate, productOrder.completionDate) &&
        Objects.equals(this.description, productOrder.description) &&
        Objects.equals(this.expectedCompletionDate, productOrder.expectedCompletionDate) &&
        Objects.equals(this.externalId, productOrder.externalId) &&
        Objects.equals(this.notificationContact, productOrder.notificationContact) &&
        Objects.equals(this.orderDate, productOrder.orderDate) &&
        Objects.equals(this.priority, productOrder.priority) &&
        Objects.equals(this.requestedCompletionDate, productOrder.requestedCompletionDate) &&
        Objects.equals(this.requestedStartDate, productOrder.requestedStartDate) &&
        Objects.equals(this.agreement, productOrder.agreement) &&
        Objects.equals(this.billingAccount, productOrder.billingAccount) &&
        Objects.equals(this.channel, productOrder.channel) &&
        Objects.equals(this.note, productOrder.note) &&
        Objects.equals(this.orderTotalPrice, productOrder.orderTotalPrice) &&
        Objects.equals(this.payment, productOrder.payment) &&
        Objects.equals(this.productOfferingQualification, productOrder.productOfferingQualification) &&
        Objects.equals(this.productOrderCharacteristic, productOrder.productOrderCharacteristic) &&
        Objects.equals(this.productOrderItem, productOrder.productOrderItem) &&
        Objects.equals(this.quote, productOrder.quote) &&
        Objects.equals(this.relatedParty, productOrder.relatedParty) &&
        Objects.equals(this.state, productOrder.state) &&
        Objects.equals(this.atBaseType, productOrder.atBaseType) &&
        Objects.equals(this.atSchemaLocation, productOrder.atSchemaLocation) &&
        Objects.equals(this.atType, productOrder.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, href, cancellationDate, cancellationReason, category, completionDate, description, expectedCompletionDate, externalId, notificationContact, orderDate, priority, requestedCompletionDate, requestedStartDate, agreement, billingAccount, channel, note, orderTotalPrice, payment, productOfferingQualification, productOrderCharacteristic, productOrderItem, quote, relatedParty, state, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductOrder {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    cancellationDate: ").append(toIndentedString(cancellationDate)).append("\n");
    sb.append("    cancellationReason: ").append(toIndentedString(cancellationReason)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    completionDate: ").append(toIndentedString(completionDate)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    expectedCompletionDate: ").append(toIndentedString(expectedCompletionDate)).append("\n");
    sb.append("    externalId: ").append(toIndentedString(externalId)).append("\n");
    sb.append("    notificationContact: ").append(toIndentedString(notificationContact)).append("\n");
    sb.append("    orderDate: ").append(toIndentedString(orderDate)).append("\n");
    sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
    sb.append("    requestedCompletionDate: ").append(toIndentedString(requestedCompletionDate)).append("\n");
    sb.append("    requestedStartDate: ").append(toIndentedString(requestedStartDate)).append("\n");
    sb.append("    agreement: ").append(toIndentedString(agreement)).append("\n");
    sb.append("    billingAccount: ").append(toIndentedString(billingAccount)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    note: ").append(toIndentedString(note)).append("\n");
    sb.append("    orderTotalPrice: ").append(toIndentedString(orderTotalPrice)).append("\n");
    sb.append("    payment: ").append(toIndentedString(payment)).append("\n");
    sb.append("    productOfferingQualification: ").append(toIndentedString(productOfferingQualification)).append("\n");
    sb.append("    productOrderCharacteristic: ").append(toIndentedString(productOrderCharacteristic)).append("\n");
    sb.append("    productOrderItem: ").append(toIndentedString(productOrderItem)).append("\n");
    sb.append("    quote: ").append(toIndentedString(quote)).append("\n");
    sb.append("    relatedParty: ").append(toIndentedString(relatedParty)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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

