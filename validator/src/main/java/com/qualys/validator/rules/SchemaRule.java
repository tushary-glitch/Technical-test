package com.qualys.validator.rules;

import com.qualys.validator.engine.*;
import com.qualys.validator.model.Record;

import java.util.Map;

public class SchemaRule implements ValidationRule {

    @Override
    public void validate(Record record, ValidationContext context, ValidationResult result) {
        // 1. Mandatory Fields
        if (record.orderId == null)
            addError(result, "Missing order_id", ValidationError.Severity.HIGH);
        if (record.userId == null)
            addError(result, "Missing user_id", ValidationError.Severity.HIGH);
        if (record.currency == null)
            addError(result, "Missing currency", ValidationError.Severity.HIGH);
        if (record.items == null || record.items.isEmpty())
            addError(result, "Items missing or empty", ValidationError.Severity.HIGH);
        if (record.totalAmount == null)
            addError(result, "Missing total_amount", ValidationError.Severity.HIGH);
        if (record.createdAt == null)
            addError(result, "Missing created_at", ValidationError.Severity.HIGH);
        if (record.updatedAt == null)
            addError(result, "Missing updated_at", ValidationError.Severity.HIGH);
        if (record.metadata == null || record.metadata.channel == null)
            addError(result, "Missing metadata.channel", ValidationError.Severity.MEDIUM);

        // 2. Schema Drift / Unknown Fields
        if (record.unknownFields != null && !record.unknownFields.isEmpty()) {
            for (String key : record.unknownFields.keySet()) {
                addError(result, "Unknown field detected: " + key, ValidationError.Severity.MEDIUM);
            }
        }
    }

    private void addError(ValidationResult result, String msg, ValidationError.Severity severity) {
        result.addError(new ValidationError(msg, severity, ValidationError.Category.SCHEMA));
    }
}
