import java.io.File;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public class Config {
    //读取目录间隔时间:30s
    public static long lDetectInterval = 1000 * 30;

    //源文件目录
    /*
    *      /home/CloudUser/DetectIP/
    *     "E:\sourceDir"
    *     /usr/test
    *     /Users/oldpotter/Desktop/DetectIP
    * */
    public static String sourceFilePath = "/Users/oldpotter/Desktop/DetectIP";

    //输出文件目录
    /*
    * sourceFilePath + File.separator + "response"
    * "/home/CloudUser/PacketInfo"
    *   /Users/oldpotter/Desktop/PacketInfo
    * */
    public static String outputFilePath = "/Users/oldpotter/Desktop/PacketInfo";

    //发包线程数
    public static int threadCount = 1000;
}
