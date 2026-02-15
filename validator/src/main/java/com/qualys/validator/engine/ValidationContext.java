package com.qualys.validator.engine;

import java.util.*;
import java.time.LocalDateTime;

public class ValidationContext {
    private final Set<String> seenOrderIds = new HashSet<>();
    // Map<UserId, List<Timestamp>> to detect velocity
    private final Map<String, List<LocalDateTime>> userActivity = new HashMap<>();

    // Stats
    private int totalRecords = 0;
    private int validRecords = 0;
    private int invalidRecords = 0;

    private final Map<ValidationError.Category, List<ValidationFailure>> detailedErrors = new EnumMap<>(
            ValidationError.Category.class);

    // Valid Records limit to avoid memory issues
    private final List<String> validRecordSummaries = new ArrayList<>();
    private static final int MAX_VALID_RECORDS = 5000;

    // Metrics for Dashboard
    private final Map<ValidationError.Severity, Integer> severityCounts = new EnumMap<>(ValidationError.Severity.class);
    private final Map<ValidationError.Category, Integer> categoryCounts = new EnumMap<>(ValidationError.Category.class);

    public ValidationContext() {
        for (ValidationError.Category cat : ValidationError.Category.values()) {
            detailedErrors.put(cat, new ArrayList<>());
            categoryCounts.put(cat, 0);
        }
        for (ValidationError.Severity sev : ValidationError.Severity.values()) {
            severityCounts.put(sev, 0);
        }
    }

    public void incrementTotal() {
        totalRecords++;
    }

    public void incrementValid() {
        validRecords++;
    }

    public void incrementInvalid() {
        invalidRecords++;
    }

    public Set<String> getSeenOrderIds() {
        return seenOrderIds;
    }

    public Map<String, List<LocalDateTime>> getUserActivity() {
        return userActivity;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getValidRecords() {
        return validRecords;
    }

    public int getInvalidRecords() {
        return invalidRecords;
    }

    public Map<ValidationError.Severity, Integer> getSeverityCounts() {
        return severityCounts;
    }

    public Map<ValidationError.Category, Integer> getCategoryCounts() {
        return categoryCounts;
    }

    public List<String> getValidRecordSummaries() {
        return validRecordSummaries;
    }

    public void captureErrors(com.qualys.validator.model.Record record, ValidationResult result) {
        if (result.hasErrors()) {
            incrementInvalid();
            for (ValidationError error : result.getErrors()) {
                String oid = record.orderId != null ? record.orderId : "N/A";
                ValidationFailure failure = new ValidationFailure(oid, result.getLineNumber(), error);

                detailedErrors.get(error.getCategory()).add(failure);

                // Track stats
                severityCounts.merge(error.getSeverity(), 1, Integer::sum);
                categoryCounts.merge(error.getCategory(), 1, Integer::sum);
            }
        } else {
            incrementValid();
            if (validRecordSummaries.size() < MAX_VALID_RECORDS) {
                String summary = String.format("Line %d | Order: %s | User: %s | Amt: %s %s",
                        result.getLineNumber(),
                        record.orderId != null ? record.orderId : "N/A",
                        record.userId != null ? record.userId : "N/A",
                        record.totalAmount != null ? record.totalAmount : "0.00",
                        record.currency != null ? record.currency : "");
                validRecordSummaries.add(summary);
            }
        }
    }

    public Map<ValidationError.Category, List<ValidationFailure>> getDetailedErrors() {
        return detailedErrors;
    }
}
