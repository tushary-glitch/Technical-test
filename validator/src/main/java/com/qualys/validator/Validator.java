package com.qualys.validator;

import com.qualys.validator.engine.*;
import com.qualys.validator.model.Record;
import com.qualys.validator.rules.*;

import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.util.Map;

public class Validator {

        private final List<ValidationRule> rules = new ArrayList<>();
        private final ValidationContext context = new ValidationContext();

        public Validator() {
                // Register Rules
                rules.add(new SchemaRule());
                rules.add(new DataLogicRule());
                rules.add(new FinancialRule());
                rules.add(new SecurityRule());
                rules.add(new CrossRecordRule());
        }

        public void validate(Record record, int lineNumber) {
                context.incrementTotal();
                ValidationResult result = new ValidationResult(lineNumber);

                for (ValidationRule rule : rules) {
                        rule.validate(record, context, result);
                }

                context.captureErrors(record, result);
        }

        public void printConsoleSummary() {
                System.out.println("Processing Complete.");
                System.out.println("Total: " + context.getTotalRecords());
                System.out.println("Invalid: " + context.getInvalidRecords());
                System.out.println("See validation_report.html for details.");
        }

        public void generateHtmlReport(String filename) {
                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
                html.append(
                                "<meta http-equiv=\"Content-Security-Policy\" content=\"script-src 'unsafe-inline' https://cdn.jsdelivr.net;\">");
                html.append("<title>Validation Report Dashboard</title>");
                html.append("<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>");
                html.append("<style>");
                html.append(
                                ":root { --primary: #2c3e50; --secondary: #34495e; --accent: #3498db; --bg: #f4f6f9; --card-bg: #ffffff; --text: #333; --border: #e0e0e0; }");
                html.append(
                                "body { font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background: var(--bg); color: var(--text); margin: 0; padding: 0; line-height: 1.6; }");
                html.append(".container { max-width: 1400px; margin: 0 auto; padding: 20px; }");
                html.append(
                                "header { background: var(--primary); color: white; padding: 1rem 0; box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin-bottom: 2rem; }");
                html.append(
                                "header .container { display: flex; justify-content: space-between; align-items: center; padding: 0 20px; }");
                html.append("h1 { margin: 0; font-size: 1.5rem; }");
                html.append(".timestamp { font-size: 0.9rem; opacity: 0.8; }");

                // KPI Cards
                html.append(
                                ".kpi-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin-bottom: 2rem; }");
                html.append(
                                ".card { background: var(--card-bg); border-radius: 8px; padding: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); border-left: 4px solid var(--accent); }");
                html.append(
                                ".card h3 { margin: 0 0 10px 0; font-size: 0.85rem; color: #7f8c8d; text-transform: uppercase; letter-spacing: 0.5px; }");
                html.append(".card .value { font-size: 1.8rem; font-weight: 700; color: var(--primary); }");
                html.append(
                                ".card.danger { border-left-color: #e74c3c; } .card.success { border-left-color: #27ae60; } .card.warning { border-left-color: #f39c12; }");

                // Charts Section
                html.append(
                                ".charts-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 20px; margin-bottom: 2rem; }");
                html.append(
                                ".chart-container { background: var(--card-bg); padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); position: relative; height: 300px; }");
                html.append(
                                ".chart-container h3 { margin-top: 0; margin-bottom: 15px; font-size: 1rem; border-bottom: 1px solid var(--border); padding-bottom: 10px; }");

                // Risk Insights
                html.append(
                                ".insights { background: #fff3cd; border: 1px solid #ffeeba; color: #856404; padding: 15px; border-radius: 8px; margin-bottom: 2rem; }");
                html.append(
                                ".insights h3 { margin-top: 0; font-size: 1.1rem; display: flex; align-items: center; gap: 10px; }");
                html.append(".insights ul { margin: 10px 0 0 0; padding-left: 20px; }");

                // Detailed Sections
                html.append(
                                "details { background: var(--card-bg); margin-bottom: 15px; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); overflow: hidden; }");
                html.append(
                                "summary { padding: 15px 20px; cursor: pointer; font-weight: 600; display: flex; justify-content: space-between; align-items: center; background: #f8f9fa; transition: background 0.2s; }");
                html.append("summary:hover { background: #e9ecef; }");
                html.append("summary::-webkit-details-marker { display: none; }");
                html.append(
                                ".badge { padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; color: white; min-width: 25px; text-align: center; }");
                html.append(
                                ".badge.high { background: #e74c3c; } .badge.medium { background: #f39c12; } .badge.low { background: #3498db; }");

                // Tables
                html.append(".table-wrapper { overflow-x: auto; padding: 0; }");
                html.append("table { width: 100%; border-collapse: collapse; font-size: 0.9rem; }");
                html.append("th, td { padding: 12px 20px; text-align: left; border-bottom: 1px solid var(--border); }");
                html.append("th { background: #f8f9fa; color: #495057; font-weight: 600; position: sticky; top: 0; }");
                html.append("tr:last-child td { border-bottom: none; }");
                html.append("tr:hover { background-color: #f8f9fa; }");

                html.append(
                                "footer { text-align: center; padding: 2rem; color: #7f8c8d; font-size: 0.85rem; margin-top: 2rem; border-top: 1px solid var(--border); }");
                html.append("</style></head><body>");

                // Header
                html.append("<header><div class='container'><h1>Validation Dashboard</h1><span class='timestamp'>Generated: ")
                                .append(java.time.LocalDateTime.now()).append("</span></div></header>");

                html.append("<div class='container'>");

                // Calculate Scores
                int total = context.getTotalRecords();
                int valid = context.getValidRecords();
                int invalid = context.getInvalidRecords();
                double qualityScore = total > 0 ? (double) valid / total * 100 : 0;

                Map<ValidationError.Severity, Integer> sevCounts = context.getSeverityCounts();
                int high = sevCounts.getOrDefault(ValidationError.Severity.HIGH, 0);
                int medium = sevCounts.getOrDefault(ValidationError.Severity.MEDIUM, 0);
                int low = sevCounts.getOrDefault(ValidationError.Severity.LOW, 0);

                // Heuristic Security Score (100 - weighted penalty)
                double penalty = (high * 5.0 + medium * 2.0 + low) / (total > 0 ? total : 1) * 10;
                double securityScore = Math.max(0, 100 - penalty);

                // Executive Summary (KPIs)
                html.append("<div class='kpi-grid'>");
                html.append(kpiCard("Total Records", String.valueOf(total), ""));
                html.append(kpiCard("Valid Records", String.valueOf(valid), "success"));
                html.append(kpiCard("Invalid Records", String.valueOf(invalid), invalid > 0 ? "danger" : "success"));
                html.append(kpiCard("Quality Score", String.format("%.1f%%", qualityScore),
                                qualityScore > 90 ? "success" : (qualityScore > 70 ? "warning" : "danger")));
                html.append(kpiCard("Security Score", String.format("%.1f", securityScore),
                                securityScore > 90 ? "success" : "warning"));
                html.append("</div>");

                // Risk Insights
                html.append(generateInsights(context, qualityScore, securityScore));

                // Charts Row
                html.append("<div class='charts-row'>");
                html.append("<div class='chart-container'><h3>Category Distribution</h3><canvas id='catChart'></canvas></div>");
                html.append("<div class='chart-container'><h3>Severity Breakdown</h3><canvas id='sevChart'></canvas></div>");
                html.append("</div>");

                // Detailed Breakdown
                html.append("<h2>Detailed Diagnostics</h2>");

                // VALID RECORDS SECTION
                List<String> validRecs = context.getValidRecordSummaries();
                int validCount = validRecs.size();
                html.append("<details open>");
                html.append("<summary>Valid Records (Sample)");
                html.append("<span class='badge' style='background:#27ae60'>").append(context.getValidRecords())
                                .append("</span>");
                html.append("</summary>");

                if (validCount > 0) {
                        html.append("<div class='table-wrapper'><table><thead><tr><th>Record Summary</th></tr></thead><tbody>");
                        validRecs.forEach(s -> html.append("<tr><td>").append(escapeHtml(s)).append("</td></tr>"));
                        if (context.getValidRecords() > validRecs.size()) {
                                html.append("<tr><td><i>... and " + (context.getValidRecords() - validRecs.size())
                                                + " more valid records not shown (limit 5000)</i></td></tr>");
                        }
                        html.append("</tbody></table></div>");
                } else {
                        html.append("<div style='padding:20px;color:#7f8c8d;'>No valid records processed.</div>");
                }
                html.append("</details>");
                for (ValidationError.Category cat : ValidationError.Category.values()) {
                        List<ValidationFailure> errors = context.getDetailedErrors().get(cat);
                        int count = context.getCategoryCounts().getOrDefault(cat, 0);

                        html.append("<details ").append(count > 0 ? "open" : "").append(">");
                        html.append("<summary>").append(cat.toString()).append(" Issues");
                        html.append("<span class='badge ").append(count > 0 ? "high" : "low").append("'>").append(count)
                                        .append("</span>");
                        html.append("</summary>");

                        if (count > 0) {
                                html.append("<div class='table-wrapper'><table><thead><tr><th>Order ID</th><th>Line</th><th>Severity</th><th>Description</th></tr></thead><tbody>");
                                // Limit to 5000 for display
                                errors.stream().limit(5000)
                                                .forEach(f -> {
                                                        html.append("<tr>");
                                                        html.append("<td>").append(escapeHtml(f.getOrderId()))
                                                                        .append("</td>");
                                                        html.append("<td>").append(f.getLineNumber()).append("</td>");
                                                        html.append("<td><span class='badge ")
                                                                        .append(f.getError().getSeverity().toString()
                                                                                        .toLowerCase())
                                                                        .append("'>").append(f.getError().getSeverity())
                                                                        .append("</span></td>");
                                                        html.append("<td>")
                                                                        .append(escapeHtml(f.getError().getMessage()))
                                                                        .append("</td>");
                                                        html.append("</tr>");
                                                });
                                if (errors.size() > 5000) {
                                        html.append("<tr><td colspan='4'><i>And ").append(errors.size() - 5000)
                                                        .append(" more...</i></td></tr>");
                                }
                                html.append("</tbody></table></div>");
                        } else {
                                html.append("<div style='padding:20px;color:#7f8c8d;'>No issues detected in this category.</div>");
                        }
                        html.append("</details>");
                }

                html.append("<footer>Qualys Validator 2.0 &bull; Confidential &bull; Generated Automatically</footer>");

                // JS for Charts
                html.append("<script>");
                html.append("const ctxCat = document.getElementById('catChart').getContext('2d');");
                html.append("new Chart(ctxCat, { type: 'doughnut', data: { labels: ['VALID',");
                for (ValidationError.Category c : ValidationError.Category.values())
                        html.append("'").append(c).append("',");
                html.append("], datasets: [{ data: [");
                html.append(context.getValidRecords()).append(",");
                for (ValidationError.Category c : ValidationError.Category.values())
                        html.append(context.getCategoryCounts().getOrDefault(c, 0)).append(",");
                html.append(
                                "], backgroundColor: ['#27ae60', '#3498db', '#e74c3c', '#f1c40f', '#9b59b6', '#2ecc71'] }] }, options: { maintainAspectRatio: false } });");

                html.append("const ctxSev = document.getElementById('sevChart').getContext('2d');");
                html.append(
                                "new Chart(ctxSev, { type: 'bar', data: { labels: ['High', 'Medium', 'Low'], datasets: [{ label: 'Count', data: [");
                html.append(high).append(",").append(medium).append(",").append(low);
                html.append(
                                "], backgroundColor: ['#e74c3c', '#f39c12', '#3498db'] }] }, options: { maintainAspectRatio: false, scales: { y: { beginAtZero: true } } } });");
                html.append("</script>");

                // Close HTML
                html.append("</div></body></html>");

                try (PrintWriter out = new PrintWriter(filename)) {
                        out.println(html.toString());
                        System.out.println("Enhanced HTML Report generated: " + filename);
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        private String kpiCard(String title, String value, String cls) {
                return "<div class='card " + cls + "'><h3>" + title + "</h3><div class='value'>" + value
                                + "</div></div>";
        }

        private String generateInsights(ValidationContext ctx, double qualScore, double secScore) {
                // Good Data Health Highlight
                if (qualScore > 95) {
                        return "<div class='insights' style='background:#d4edda;border-color:#c3e6cb;color:#155724'><h3>&#10004; Data Health Good</h3><ul><li><b>High Validity:</b> Over 95% of records are valid.</li></ul></div>";
                }

                if (qualScore > 99 && secScore > 90)
                        return "";

                StringBuilder sb = new StringBuilder();
                sb.append("<div class='insights'><h3>&#9888; Automated Risk Insights</h3><ul>");

                if (qualScore < 80)
                        sb.append("<li><b>High Failure Rate:</b> More than 20% of records failed validation. Investigate data ingestion source.</li>");
                if (secScore < 70)
                        sb.append("<li><b>Security Alert:</b> Significant security risks detected. Check for injection patterns or high-risk currencies.</li>");

                Integer finErr = ctx.getCategoryCounts().get(ValidationError.Category.FINANCIAL);
                if (finErr != null && finErr > 10)
                        sb.append("<li><b>Financial Integrity:</b> ").append(finErr)
                                        .append(" financial discrepancies detected. Review calculation logic.</li>");

                Integer schemaErr = ctx.getCategoryCounts().get(ValidationError.Category.SCHEMA);
                if (schemaErr != null && schemaErr > 0)
                        sb.append("<li><b>Schema Drift:</b> Unexpected fields or missing mandatory data detected.</li>");

                sb.append("</ul></div>");
                return sb.toString();
        }

        private String escapeHtml(String s) {
                if (s == null)
                        return "";
                return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
                                .replace("'", "&#39;");
        }
}
