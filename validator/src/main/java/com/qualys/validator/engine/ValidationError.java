package com.qualys.validator.engine;

public class ValidationError {
    public enum Severity {
        LOW, MEDIUM, HIGH
    }

    public enum Category {
        MANDATORY, SCHEMA, FINANCIAL, BUSINESS, SECURITY
    }

    private final String message;
    private final Severity severity;
    private final Category category;

    public ValidationError(String message, Severity severity, Category category) {
        this.message = message;
        this.severity = severity;
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s", category, severity, message);
    }
}
