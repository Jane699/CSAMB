package team.oheck.commom;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class DateTimeUtil {
    private static final SimpleDateFormat dateTimeF = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

    /**
     * now time
     * @return now time
     */
    public static String now() {
        return dateTimeF.format(new Date());
    }
}
