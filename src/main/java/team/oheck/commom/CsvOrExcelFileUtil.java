package team.oheck.commom;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvOrExcelFileUtil {
    /**
     * read csv content to list
     *
     * @param csvFilePath CSV file path
     * @param encoding encoding string (i.e GBK
     * @return csv file content without header
     */
    public static List<String[]> readCsvFileToList(String csvFilePath, String encoding) {
        List<String[]> content = new ArrayList<>();

        try {
            CsvReader csvReader = new CsvReader(csvFilePath, ',', Charset.forName(encoding));
            csvReader.readHeaders(); // skip header
            while (csvReader.readRecord()) {
                content.add(csvReader.getValues());
            }
            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * read csv file content to array
     *
     * @param csvFilePath csv file path
     * @param encoding encoding string (i.e. GBK
     * @return 2-dim content array
     */
    public static String[][] readCsvFileToArray(String csvFilePath, String encoding) {
        List<String[]> resList = readCsvFileToList(csvFilePath, encoding);
        return resList.stream().map(l -> Arrays.stream(l).toArray(String[]::new)).toArray(String[][]::new);
    }
}
