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

    private final Map<ValidationError.Category, List<String>> detailedErrors = new EnumMap<>(
            ValidationError.Category.class);

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

    public void captureErrors(ValidationResult result) {
        if (result.hasErrors()) {
            incrementInvalid();
            for (ValidationError error : result.getErrors()) {
                String logMsg = "Line " + result.getLineNumber() + " [" + error.getSeverity() + "]: "
                        + error.getMessage();
                detailedErrors.get(error.getCategory()).add(logMsg);

                // Track stats
                severityCounts.merge(error.getSeverity(), 1, Integer::sum);
                categoryCounts.merge(error.getCategory(), 1, Integer::sum);
            }
        } else {
            incrementValid();
        }
    }

    public Map<ValidationError.Category, List<String>> getDetailedErrors() {
        return detailedErrors;
    }
}
