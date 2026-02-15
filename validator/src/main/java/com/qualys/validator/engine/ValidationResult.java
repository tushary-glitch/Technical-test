package com.qualys.validator.engine;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final List<ValidationError> errors = new ArrayList<>();
    private final int lineNumber;

    public ValidationResult(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void addError(ValidationError error) {
        this.errors.add(error);
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
