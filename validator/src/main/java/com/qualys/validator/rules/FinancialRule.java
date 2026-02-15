package com.qualys.validator.rules;

import com.qualys.validator.engine.*;
import com.qualys.validator.model.Discount;
import com.qualys.validator.model.Item;
import com.qualys.validator.model.Record;

import java.math.BigDecimal;

public class FinancialRule implements ValidationRule {

    @Override
    public void validate(Record record, ValidationContext context, ValidationResult result) {
        if (record.items == null)
            return; // Handled by SchemaRule

        BigDecimal itemsTotal = BigDecimal.ZERO;
        for (Item item : record.items) {
            if (item.price == null || item.qty == null)
                continue;

            // Negativity check
            if (item.qty <= 0)
                addError(result, "Item qty <= 0", ValidationError.Severity.HIGH);
            if (item.price.compareTo(BigDecimal.ZERO) < 0)
                addError(result, "Item price < 0", ValidationError.Severity.HIGH);

            // Outlier check
            if (item.qty > 10000)
                addError(result, "Qty outlier > 10,000", ValidationError.Severity.MEDIUM);
            if (item.price.compareTo(new BigDecimal("10000000")) > 0)
                addError(result, "Price outlier > 10,000,000", ValidationError.Severity.MEDIUM);

            itemsTotal = itemsTotal.add(item.price.multiply(BigDecimal.valueOf(item.qty)));
        }

        BigDecimal discountsTotal = BigDecimal.ZERO;
        if (record.discounts != null) {
            for (Discount discount : record.discounts) {
                if (discount.amount == null)
                    continue;
                if (discount.amount.compareTo(BigDecimal.ZERO) < 0)
                    addError(result, "Discount amount < 0", ValidationError.Severity.HIGH);
                discountsTotal = discountsTotal.add(discount.amount);
            }
        }

        // Integrity Check
        BigDecimal calculatedTotal = itemsTotal.subtract(discountsTotal);
        if (record.totalAmount != null) {
            if (calculatedTotal.subtract(record.totalAmount).abs().compareTo(new BigDecimal("0.01")) > 0) {
                addError(result, "Calculated " + calculatedTotal + " != Declared " + record.totalAmount,
                        ValidationError.Severity.HIGH);
            }
            if (record.totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                addError(result, "Negative Total Amount", ValidationError.Severity.HIGH);
            }
        }

        if (discountsTotal.compareTo(itemsTotal) > 0) {
            addError(result, "Discount > Gross Amount", ValidationError.Severity.HIGH);
        }
    }

    private void addError(ValidationResult result, String msg, ValidationError.Severity severity) {
        result.addError(new ValidationError(msg, severity, ValidationError.Category.FINANCIAL));
    }
}
