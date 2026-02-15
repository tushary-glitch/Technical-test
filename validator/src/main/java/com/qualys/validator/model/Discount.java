package com.qualys.validator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class Discount {
    @JsonProperty("type")
    public String type;

    @JsonProperty("amount")
    public BigDecimal amount;
}
