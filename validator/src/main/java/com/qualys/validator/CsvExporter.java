package com.qualys.validator;

import com.qualys.validator.model.Discount;
import com.qualys.validator.model.Item;
import com.qualys.validator.model.Record;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

public class CsvExporter {

    public static void initCsv(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(
                    "order_id,user_id,currency,total_amount,created_at,updated_at,channel,total_discounts,item_sku,item_qty,item_price");
        }
    }

    public static void exportToCsv(Record record, PrintWriter writer) {
        // Calculate total discount
        BigDecimal totalDiscount = BigDecimal.ZERO;
        if (record.discounts != null) {
            for (Discount d : record.discounts) {
                if (d.amount != null)
                    totalDiscount = totalDiscount.add(d.amount);
            }
        }

        String common = String.join(",",
                escape(record.orderId),
                escape(record.userId),
                escape(record.currency),
                escape(record.totalAmount != null ? record.totalAmount.toString() : ""),
                escape(record.createdAt),
                escape(record.updatedAt),
                escape(record.metadata != null ? record.metadata.channel : ""),
                totalDiscount.toString());

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

    private static String escape(String value) {
        if (value == null)
            return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
