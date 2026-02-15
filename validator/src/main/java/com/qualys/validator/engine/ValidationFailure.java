package com.qualys.validator.engine;

public class ValidationFailure {
    private final String orderId;
    private final int lineNumber;
    private final ValidationError error;

    public ValidationFailure(String orderId, int lineNumber, ValidationError error) {
        this.orderId = orderId;
        this.lineNumber = lineNumber;
        this.error = error;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public ValidationError getError() {
        return error;
    }
}
