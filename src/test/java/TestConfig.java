import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Administrator on 2017/6/5 0005.
 */
public class TestConfig {
    public static void main(String[] args){
        System.out.println("path:" + args[0]);
        String configPath = args[0];
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configPath));
            String s;
            s = properties.getProperty("a");
            System.out.println("a=" + s);

            s = properties.getProperty("b");
            System.out.println("b=" + s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
