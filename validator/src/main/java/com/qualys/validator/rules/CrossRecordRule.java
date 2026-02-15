package com.qualys.validator.rules;

import com.qualys.validator.engine.*;
import com.qualys.validator.model.Record;

public class CrossRecordRule implements ValidationRule {

    @Override
    public void validate(Record record, ValidationContext context, ValidationResult result) {
        if (record.orderId != null) {
            if (!context.getSeenOrderIds().add(record.orderId)) {
                result.addError(new ValidationError("Duplicate Order ID detected: " + record.orderId,
                        ValidationError.Severity.HIGH, ValidationError.Category.BUSINESS));
            }
        }
    }
}
