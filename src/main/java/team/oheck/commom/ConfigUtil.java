package team.oheck.commom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static Properties props;

    public static Properties getProp(){
        if (props != null) {
            return props;
        }
        InputStream is = ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties");
        props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return props;
    }
}
