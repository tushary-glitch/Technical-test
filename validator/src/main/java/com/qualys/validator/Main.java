package com.qualys.validator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualys.validator.model.Record;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar parser.jar <path-to-records.json>");
            System.exit(1);
        }

        String filePath = args[0];
        File file = new File(filePath);

        if (!file.exists()) {
            System.err.println("File not found: " + filePath);
            System.exit(1);
        }

        boolean exportCsv = args.length > 1 && args[1].equalsIgnoreCase("--csv");
        String csvFile = "output.csv";
        java.io.PrintWriter csvWriter = null;

        if (exportCsv) {
            try {
                CsvExporter.initCsv(csvFile);
                csvWriter = new java.io.PrintWriter(new java.io.FileWriter(csvFile, true));
                System.out.println("Exporting CSV to: " + csvFile);
            } catch (IOException e) {
                System.err.println("Failed to initialize CSV exporter: " + e.getMessage());
            }
        }

        Validator validator = new Validator();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory();

        try (JsonParser parser = jsonFactory.createParser(file)) {
            int lineNumber = 0;

            while (parser.nextToken() != null) {
                lineNumber++;
                try {
                    if (parser.currentToken() == JsonToken.START_OBJECT) {
                        Record record = mapper.readValue(parser, Record.class);
                        validator.validate(record, lineNumber);

                        if (csvWriter != null) {
                            CsvExporter.exportToCsv(record, csvWriter);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Line " + lineNumber + ": Failed to parse JSON - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (csvWriter != null)
                csvWriter.close();
        }

        validator.printConsoleSummary();
        validator.generateHtmlReport("validation_report.html");
    }
}
