package com.qualys.validator.rules;

import com.qualys.validator.engine.*;
import com.qualys.validator.model.Record;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

public class DataLogicRule implements ValidationRule {

    private static final Set<String> ALLOWED_CURRENCIES = Set.of("USD", "EUR", "INR");

    @Override
    public void validate(Record record, ValidationContext context, ValidationResult result) {
        // IDs
        if (record.orderId != null && !record.orderId.matches("O-\\d+")) {
            addError(result, "Invalid order_id format", ValidationError.Category.SCHEMA);
        }
        if (record.userId != null && !record.userId.matches("U-\\d+")) {
            addError(result, "Invalid user_id format", ValidationError.Category.SCHEMA);
        }

        // Currency
        if (record.currency != null) {
            if (!ALLOWED_CURRENCIES.contains(record.currency)) {
                addError(result, "Invalid currency: " + record.currency, ValidationError.Category.BUSINESS);
            }
        }

        // Timestamps
        if (record.createdAt != null && record.updatedAt != null) {
            try {
                LocalDateTime created = LocalDateTime.parse(record.createdAt, DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime updated = LocalDateTime.parse(record.updatedAt, DateTimeFormatter.ISO_DATE_TIME);

                if (updated.isBefore(created)) {
                    addError(result, "updated_at before created_at", ValidationError.Category.BUSINESS);
                }
                if (created.isAfter(LocalDateTime.now().plusDays(1))) {
                    addError(result, "created_at in future", ValidationError.Category.BUSINESS);
                }
            } catch (DateTimeParseException e) {
                addError(result, "Malformed timestamp format", ValidationError.Category.SCHEMA);
            }
        }
    }

    private void addError(ValidationResult result, String msg, ValidationError.Category category) {
        result.addError(new ValidationError(msg, ValidationError.Severity.MEDIUM, category));
    }
}
