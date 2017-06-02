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
    * */
    public static String sourceFilePath = "/home/CloudUser/DetectIP/";

    //输出文件目录
    /*
    * sourceFilePath + File.separator + "response"
    * "/home/CloudUser/PacketInfo"
    *
    * */
    public static String outputFilePath = "/home/CloudUser/PacketInfo";

    //发包线程数
    public static int threadCount = 1000;
}
