import java.io.*;
import java.util.*;

public class CSVComparator {

    public static void main(String[] args) throws IOException {
        String csvFile1 = "file1.csv";
        String csvFile2 = "file2.csv";
        String diffFile = "diff.csv";

        // Specify the column to compare by (index starting from 0)
        int compareColumnIndex = 0;

        // Compare the CSV files and generate the diff file
        compareCSVFiles(csvFile1, csvFile2, compareColumnIndex, diffFile);
    }

    public static void compareCSVFiles(String file1, String file2, int compareColumnIndex, String diffFile) throws IOException {
        List<Map<String, String>> csv1Data = readCSV(file1);
        List<Map<String, String>> csv2Data = readCSV(file2);

        // Get the headers
        Set<String> csv1Headers = csv1Data.isEmpty() ? new HashSet<>() : csv1Data.get(0).keySet();
        Set<String> csv2Headers = csv2Data.isEmpty() ? new HashSet<>() : csv2Data.get(0).keySet();

        // Track missing and mismatched columns
        Set<String> missingInFile1 = new HashSet<>(csv2Headers);
        missingInFile1.removeAll(csv1Headers);

        Set<String> missingInFile2 = new HashSet<>(csv1Headers);
        missingInFile2.removeAll(csv2Headers);

        List<String> diffRecords = new ArrayList<>();
        diffRecords.add("Row,Column,Mismatch or Missing"); // Add diff file headers

        // Compare rows by specified column
        for (Map<String, String> row1 : csv1Data) {
            String compareValue1 = row1.getOrDefault(String.valueOf(compareColumnIndex), null);
            if (compareValue1 == null) continue;

            boolean foundMatch = false;
            for (Map<String, String> row2 : csv2Data) {
                String compareValue2 = row2.getOrDefault(String.valueOf(compareColumnIndex), null);

                // If values match, compare other columns
                if (compareValue1.equals(compareValue2)) {
                    foundMatch = true;
                    for (String header : csv1Headers) {
                        String value1 = row1.get(header);
                        String value2 = row2.getOrDefault(header, "MISSING");

                        if (!Objects.equals(value1, value2)) {
                            diffRecords.add(compareValue1 + "," + header + ",Mismatch: " + value1 + " != " + value2);
                        }
                    }
                }
            }
            if (!foundMatch) {
                diffRecords.add(compareValue1 + ",N/A,Missing row in file2");
            }
        }

        // Track missing rows in file1
        for (Map<String, String> row2 : csv2Data) {
            String compareValue2 = row2.getOrDefault(String.valueOf(compareColumnIndex), null);
            boolean foundMatch = false;
            for (Map<String, String> row1 : csv1Data) {
                String compareValue1 = row1.getOrDefault(String.valueOf(compareColumnIndex), null);
                if (compareValue1 != null && compareValue1.equals(compareValue2)) {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                diffRecords.add(compareValue2 + ",N/A,Missing row in file1");
            }
        }

        // Add missing column information
        if (!missingInFile1.isEmpty()) {
            diffRecords.add("N/A,N/A,Missing columns in file1: " + missingInFile1);
        }
        if (!missingInFile2.isEmpty()) {
            diffRecords.add("N/A,N/A,Missing columns in file2: " + missingInFile2);
        }

        // Write the diff file
        writeDiffFile(diffFile, diffRecords);
    }

    // Function to read CSV into a list of maps (each map is a row with column headers as keys)
    public static List<Map<String, String>> readCSV(String fileName) throws IOException {
        List<Map<String, String>> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String[] headers = reader.readLine().split(",");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i], values.length > i ? values[i] : "");
                }
                rows.add(row);
            }
        }
        return rows;
    }

    // Function to write the diff file
    public static void writeDiffFile(String fileName, List<String> records) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String record : records) {
                writer.write(record);
                writer.newLine();
            }
        }
    }
}
