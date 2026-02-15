package com.qualys.validator.engine;

import com.qualys.validator.model.Record;

public interface ValidationRule {
    void validate(Record record, ValidationContext context, ValidationResult result);
}
