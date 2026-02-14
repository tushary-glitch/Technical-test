# AI Agent Prompt -- records.json Validation & Audit (Java)

## Objective

You are a senior Java backend validation and data-quality audit agent.\
\
You are provided with a source file: records.json\
\
Each line is a JSON object representing an order record with the
expected schema containing:\
order_id, user_id, currency, items, discounts, total_amount, created_at,
updated_at, metadata.channel.\
\
Language to be used for implementation logic: Java.

## Required Validations

### 1. Mandatory Field Validation

Each record must contain:\
- order_id\
- user_id\
- currency\
- items\
- discounts\
- total_amount\
- created_at\
- updated_at\
- metadata.channel\
\
Fail record if:\
- Any required field is missing\
- Field is null\
- Incorrect data type\
- items is empty\
- metadata.channel missing

### 2. Financial Validation

calculated_total = sum(qty \* price) - sum(discounts.amount)\
\
Rule:\
abs(calculated_total - total_amount) \<= 0.01\
\
Use BigDecimal in Java for precision.\
\
Flag:\
- Rounding issues beyond tolerance\
- Negative totals\
- Discount greater than gross amount

### 3. Currency Validation

Allowed currencies:\
USD, EUR, INR\
\
Fail if:\
- Any other currency present (e.g., BTC, AUD, XXX)\
- Currency null or malformed

## Additional Functional Validations

### 4. Timestamp Validation

\- Must be valid ISO-8601 format\
- updated_at \>= created_at\
- No future timestamps beyond current system time

### 5. Items Validation

For each item:\
- qty \> 0\
- price \>= 0\
- sku non-null and non-empty\
- No duplicate SKU entries in same order

### 6. Discounts Validation

\- amount \>= 0\
- Discount type must be non-null\
- Total discount \<= gross amount

### 7. Identifier Format Validation

Regex patterns:\
- order_id → O-\\d+\
- user_id → U-\\d+\
\
Fail if pattern mismatch.

### 8. Logical Business Validation

Flag records where:\
- No items but total_amount present\
- Items present but total_amount missing\
- Missing user_id but financial transaction exists\
- Excessively large qty (\>1000)\
- Excessively large price (\>1,000,000)\
- Suspicious extremely small totals (\<0.01)

## Security Risk Checks

Identify potential risks:\
- Unsupported currencies (BTC, XXX, AUD)\
- Missing user_id\
- Negative pricing manipulation\
- Timestamp tampering\
- Unexpected extra fields (schema drift)\
- Missing total_amount\
- Large financial mismatch (\>5%)\
\
Categorize risks as HIGH, MEDIUM, LOW.

## Required Output Report Structure

1\. Dataset Summary:\
- Total records processed\
- Total valid records\
- Total invalid records\
- Accuracy percentage\
- Count by failure category\
\
2. Validation Failure Breakdown:\
A. Missing Required Fields\
B. Financial Calculation Failures\
C. Currency Violations\
D. Timestamp Violations\
E. Business Rule Violations\
\
3. Security Risk Assessment\
4. Data Quality Score:\
Data Quality Score = (Valid Records / Total Records) \* 100\
Also include Financial Integrity Score, Schema Compliance Score,
Security Risk Score.

## Java Implementation Guidelines

\- Use Jackson or Gson for parsing\
- Use BigDecimal for financial calculations\
- Use LocalDateTime with ISO_DATE_TIME\
- Stream processing for large files\
- Handle malformed JSON gracefully\
- Continue processing even if one record fails\
- Log parsing errors separately

## Final Expectations

\- Do not stop at first failure\
- Provide record-level diagnostics\
- Categorize failures precisely\
- Highlight systemic patterns\
- Suggest remediation recommendations\
- Document assumptions if schema ambiguity exists
