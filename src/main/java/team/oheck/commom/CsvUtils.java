package team.oheck.commom;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * csv util
 */
public class CsvUtils {
    /**
     * Write to csv file
     *
     * @param path  path
     * @param title header
     * @param data  data
     */
    public static void toCsv(String path, Object[] title, Object[][] data) {
        File f = new File(path);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(path)) {
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            csvPrinter.printRecord(title);
            for (Object[] datum : data) {
                csvPrinter.printRecord(datum);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void toCsv(String path, Object[][] data) {
        File f = new File(path);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(path)) {
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            for (Object[] datum : data) {
                csvPrinter.printRecord(datum);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * read csv file
     *
     * @param path file path
     * @return content
     */
    public static List<List<String>> readCsv(String path) {
        List<List<String>> res = new ArrayList<>();
        try (FileReader reader = new FileReader(path); CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {
            for (CSVRecord csvRecord : csvParser) {
                List<String> row = new ArrayList<>();
                for (String s : csvRecord) {
                    row.add(s);
                }
                res.add(row);
            }
            return res;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 2D List to 2d Array
     *
     * @param table 2D List
     * @return 2D Array
     */
    public static String[][] listTableToArrayTable(List<List<String>> table) {
        List<String[]> res = new ArrayList<>();
        for (List<String> row : table) {
            res.add(row.toArray(new String[0]));
        }
        return (String[][]) res.toArray();
    }
}
