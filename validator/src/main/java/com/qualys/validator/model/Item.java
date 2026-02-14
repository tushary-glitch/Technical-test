package com.qualys.validator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class Item {
    @JsonProperty("sku")
    public String sku;

    @JsonProperty("qty")
    public Integer qty;

    @JsonProperty("price")
    public BigDecimal price;
}
