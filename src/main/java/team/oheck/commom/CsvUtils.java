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
 * CSV文件处理工具，基于commons-csv
 */
public class CsvUtils {
    /**
     * 写入到CSV文件
     *
     * @param path  写出文件路径
     * @param title 头部
     * @param data  数据
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
     * 读取CSV文件
     *
     * @param path 读入文件路径
     * @return CSV文件内容
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
     * 二维List转为二维String数组
     *
     * @param table 二维List对象
     * @return 二维String数组对象
     */
    public static String[][] listTableToArrayTable(List<List<String>> table) {
        List<String[]> res = new ArrayList<>();
        for (List<String> row : table) {
            res.add(row.toArray(new String[0]));
        }
        return (String[][]) res.toArray();
    }
}
