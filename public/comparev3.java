import java.io.*;
import java.util.*;

public class CSVComparator {

    public static void main(String[] args) throws IOException {
        String file1 = "file1.csv";
        String file2 = "file2.csv";
        String missingRowsFile = "missing_rows.csv";
        String mismatchedRowsFile = "mismatched_rows.csv";
        
        // Define the unique key columns (e.g., "ID" or combination of columns like "ID,Name")
        List<String> uniqueKeyColumns = Arrays.asList("ID");  // Replace with your unique columns
        
        // Read CSV files into maps
        Map<String, Map<String, String>> map1 = readCSV(file1, uniqueKeyColumns);
        Map<String, Map<String, String>> map2 = readCSV(file2, uniqueKeyColumns);
        
        // Find missing and mismatched rows
        List<Map<String, String>> missingRows = new ArrayList<>();
        List<Map<String, String>> mismatchedRows = new ArrayList<>();
        
        // Headers for the output files (include the unique key columns)
        List<String> headers = new ArrayList<>(uniqueKeyColumns);
        headers.add("Details");  // Adding a 'Details' column for mismatched information
        
        for (String key : map1.keySet()) {
            if (!map2.containsKey(key)) {
                // Missing row in file2
                Map<String, String> missingRow = new HashMap<>();
                missingRow.putAll(map1.get(key));
                missingRows.add(missingRow);
            } else {
                // Check for mismatches
                Map<String, String> row1 = map1.get(key);
                Map<String, String> row2 = map2.get(key);
                if (columnsMismatch(row1, row2)) {
                    Map<String, String> mismatchDetails = new HashMap<>();
                    mismatchDetails.put("Key", key);
                    mismatchDetails.put("Details", getMismatchDetails(row1, row2));
                    mismatchedRows.add(mismatchDetails);
                }
            }
        }
        
        // Write results to files
        writeCSV(missingRowsFile, missingRows, headers);
        writeCSV(mismatchedRowsFile, mismatchedRows, headers);
    }

    // Read CSV file into a map of unique key to row map
    public static Map<String, Map<String, String>> readCSV(String fileName, List<String> keyColumns) throws IOException {
        Map<String, Map<String, String>> data = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        
        // Read headers
        String headerLine = reader.readLine();
        List<String> headers = parseLine(headerLine);

        String line;
        while ((line = reader.readLine()) != null) {
            List<String> values = parseLine(line);
            Map<String, String> row = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                row.put(headers.get(i), values.get(i));
            }
            // Create a unique key from the specified columns
            String key = createKey(row, keyColumns);
            data.put(key, row);
        }
        reader.close();
        return data;
    }

    // Create a unique key for a row based on specified columns
    public static String createKey(Map<String, String> row, List<String> keyColumns) {
        List<String> keyValues = new ArrayList<>();
        for (String column : keyColumns) {
            keyValues.add(row.get(column));
        }
        return String.join(":", keyValues);
    }

    // Check if two rows have mismatched columns
    public static boolean columnsMismatch(Map<String, String> row1, Map<String, String> row2) {
        for (String key : row1.keySet()) {
            if (!row1.get(key).equals(row2.get(key))) {
                return true;
            }
        }
        return false;
    }

    // Get mismatch details between two rows as a string
    public static String getMismatchDetails(Map<String, String> row1, Map<String, String> row2) {
        StringBuilder mismatchDetails = new StringBuilder();
        for (String key : row1.keySet()) {
            if (!row1.get(key).equals(row2.get(key))) {
                mismatchDetails.append(key).append(": ")
                        .append("File1: ").append(row1.get(key))
                        .append(", File2: ").append(row2.get(key)).append("; ");
            }
        }
        return mismatchDetails.toString().trim();
    }

    // Parse a line using StringTokenizer and trim spaces
    public static List<String> parseLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken().trim());
        }
        return tokens;
    }

    // Write CSV data to a file
    public static void writeCSV(String fileName, List<Map<String, String>> data, List<String> headers) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(String.join(",", headers));
        writer.newLine();
        for (Map<String, String> row : data) {
            List<String> values = new ArrayList<>();
            for (String header : headers) {
                values.add(row.getOrDefault(header, ""));
            }
            writer.write(String.join(",", values));
            writer.newLine();
        }
        writer.close();
    }
}
