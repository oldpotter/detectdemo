import java.io.File;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public class Config {
    //读取目录间隔时间
    public static long lDetectInterval = 3000 * 1000;

    //源文件目录
    public static String sourceFilePath = "E:\\sourceDir";

    //输出文件目录
    public static String outputFilePath = sourceFilePath + File.separator + "response";

    //发包线程数
    public static int threadCount = 1000;

    //超时
    public static int timeout = 30000;
}
