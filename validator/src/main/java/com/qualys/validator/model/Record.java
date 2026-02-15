package com.qualys.validator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public class Record {
    @JsonProperty("order_id")
    public String orderId;

    @JsonProperty("user_id")
    public String userId;

    @JsonProperty("currency")
    public String currency;

    @JsonProperty("items")
    public List<Item> items;

    @JsonProperty("discounts")
    public List<Discount> discounts;

    @JsonProperty("total_amount")
    public BigDecimal totalAmount;

    @JsonProperty("created_at")
    public String createdAt;

    @JsonProperty("updated_at")
    public String updatedAt;

    @JsonProperty("metadata")
    public Metadata metadata;

    @com.fasterxml.jackson.annotation.JsonAnySetter
    public java.util.Map<String, Object> unknownFields = new java.util.HashMap<>();
}
