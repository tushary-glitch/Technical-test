package com.qualys.validator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualys.validator.model.Discount;
import com.qualys.validator.model.Item;
import com.qualys.validator.model.Record;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

public class RecordConverter {

    public static void main(String[] args) {
        String inputPath = "d:\\Qualys\\records.json";
        String outputPath = "d:\\Qualys\\source_records.csv";

        if (args.length >= 1)
            inputPath = args[0];

        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            System.err.println("File not found: " + inputPath);
            System.exit(1);
        }

        System.out.println("Converting " + inputPath + " to " + outputPath + "...");

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            // Write Header
            writer.println(
                    "order_id,user_id,currency,total_amount,created_at,updated_at,channel,total_discounts,item_sku,item_qty,item_price");

            ObjectMapper mapper = new ObjectMapper();
            JsonFactory jsonFactory = new JsonFactory();

            try (JsonParser parser = jsonFactory.createParser(inputFile)) {
                while (parser.nextToken() != null) {
                    if (parser.currentToken() == JsonToken.START_OBJECT) {
                        try {
                            Record record = mapper.readValue(parser, Record.class);
                            writeRecord(record, writer);
                        } catch (Exception e) {
                            System.err.println("Skipping malformed record: " + e.getMessage());
                        }
                    }
                }
            }
            System.out.println("Conversion complete: " + outputPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeRecord(Record record, PrintWriter writer) {
        String common = String.join(",",
                escape(record.orderId),
                escape(record.userId),
                escape(record.currency),
                escape(record.totalAmount != null ? record.totalAmount.toString() : ""),
                escape(record.createdAt),
                escape(record.updatedAt),
                escape(record.metadata != null ? record.metadata.channel : ""),
                getTotalDiscount(record));

        if (record.items == null || record.items.isEmpty()) {
            writer.println(common + ",,,");
        } else {
            for (Item item : record.items) {
                String itemData = String.join(",",
                        escape(item.sku),
                        item.qty != null ? item.qty.toString() : "",
                        item.price != null ? item.price.toString() : "");
                writer.println(common + "," + itemData);
            }
        }
    }

    private static String getTotalDiscount(Record record) {
        if (record.discounts == null || record.discounts.isEmpty())
            return "0.00";
        BigDecimal total = BigDecimal.ZERO;
        for (Discount d : record.discounts) {
            if (d.amount != null)
                total = total.add(d.amount);
        }
        return total.toString();
    }

    private static String escape(String value) {
        if (value == null)
            return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
