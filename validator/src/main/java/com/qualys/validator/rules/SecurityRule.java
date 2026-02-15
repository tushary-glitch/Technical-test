package com.qualys.validator.rules;

import com.qualys.validator.engine.*;
import com.qualys.validator.model.Record;
import java.util.List;

public class SecurityRule implements ValidationRule {

    private static final List<String> HIGH_RISK_CURRENCIES = List.of("BTC", "XXX", "AUD", "XMR");

    @Override
    public void validate(Record record, ValidationContext context, ValidationResult result) {
        // High Risk Currency
        if (record.currency != null && HIGH_RISK_CURRENCIES.contains(record.currency)) {
            addError(result, "High Risk Currency detected: " + record.currency, ValidationError.Severity.HIGH);
        }

        // Injection checks on strings (Simplified XSS detection)
        if (hasInjection(record.orderId))
            addError(result, "Potential Injection in order_id", ValidationError.Severity.HIGH);
        if (hasInjection(record.userId))
            addError(result, "Potential Injection in user_id", ValidationError.Severity.HIGH);
        if (record.metadata != null && hasInjection(record.metadata.channel))
            addError(result, "Potential Injection in metadata", ValidationError.Severity.HIGH);
    }

    private boolean hasInjection(String value) {
        if (value == null)
            return false;
        return value.contains("<script>") || value.contains("javascript:") || value.contains("DROP TABLE")
                || value.contains("UNION SELECT");
    }

    private void addError(ValidationResult result, String msg, ValidationError.Severity severity) {
        result.addError(new ValidationError(msg, severity, ValidationError.Category.SECURITY));
    }
}
