package com.smartcampus.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for formatting and rendering clean ASCII tabular data
 * directly to the standard output console.
 */
public class TableFormatter {
    private final List<String> headers;
    private final List<List<String>> rows;

    public TableFormatter(String... headers) {
        this.headers = Arrays.asList(headers);
        this.rows = new ArrayList<>();
    }

    public void addRow(String... cells) {
        if (cells.length != headers.size()) {
            throw new IllegalArgumentException("Row cell count must match header count");
        }
        rows.add(Arrays.asList(cells));
    }

    public void print() {
        int columns = headers.size();
        int[] colWidths = new int[columns];

        for (int i = 0; i < columns; i++) {
            colWidths[i] = headers.get(i).length();
        }

        for (List<String> row : rows) {
            for (int i = 0; i < columns; i++) {
                String val = row.get(i) != null ? row.get(i) : "";
                if (val.length() > colWidths[i]) {
                    colWidths[i] = val.length();
                }
            }
        }

        // Add padding
        for (int i = 0; i < columns; i++) {
            colWidths[i] += 2;
        }

        String border = buildSeparator(colWidths, '+', '-');
        System.out.println(border);

        // Print header
        printRow(headers, colWidths);
        System.out.println(buildSeparator(colWidths, '+', '='));

        // Print rows
        for (List<String> row : rows) {
            printRow(row, colWidths);
        }

        System.out.println(border);
    }

    private void printRow(List<String> cells, int[] colWidths) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < cells.size(); i++) {
            String cell = cells.get(i) != null ? cells.get(i) : "";
            sb.append(" ").append(cell);
            int padding = colWidths[i] - cell.length() - 1;
            for (int p = 0; p < padding; p++) {
                sb.append(" ");
            }
            sb.append("|");
        }
        System.out.println(sb);
    }

    private String buildSeparator(int[] colWidths, char junction, char line) {
        StringBuilder sb = new StringBuilder();
        sb.append(junction);
        for (int w : colWidths) {
            for (int i = 0; i < w; i++) {
                sb.append(line);
            }
            sb.append(junction);
        }
        return sb.toString();
    }
}
