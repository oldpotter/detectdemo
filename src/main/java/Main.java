import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.PatternFilenameFilter;
import sun.misc.IOUtils;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/5/18 0018.
 */


public class Main {

    public static void main(String[] args) {

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new FileTask(), 0, Config.lDetectInterval);
    }
}

/**
 * 检索文件夹
 */
class FileTask extends TimerTask {
    @Override
    public void run() {
        File sourceDir = new File(Config.sourceFilePath);
        Pattern pattern = Pattern.compile("^.*.dat");
        FilenameFilter filter = new PatternFilenameFilter(pattern);
        File[] files = sourceDir.listFiles(filter);
        System.out.println("发现了" + files.length + "个探测文件");
        FileProcess.process(files);
    }
}

/**
 * 解析源文件
 */
class FileProcess {

    /**
     * 文件转化为字节数组
     *
     * @param file
     * @return
     */
    public static byte[] getBytesFromFile(File file) {
        byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            // log.error("helper:get bytes from file process error!");
            e.printStackTrace();
        }
        return ret;
    }

    /**
     *
     * @param files
     */
    public static void process(File[] files) {
        for (File file : files) {
            try {
                byte[] bytes = getBytesFromFile(file);
                //删除原始文件
                System.out.println("file:" + file.getName() + "删除" + (file.delete() ? "成功" : "不成功"));
                System.out.println("file:" + file.getName() + " bytes:" + Tools.byte2HexStr(bytes));
                LittleEndianDataInputStream inputStream = new LittleEndianDataInputStream(new ByteArrayInputStream(bytes));
                while (inputStream.available() >= 24) {
                    //放到detectpacket
                    DetectPacket detectPacket = new DetectPacket();
                    detectPacket.setTime(inputStream.readInt());
                    detectPacket.setSendCode(inputStream.readInt());
                    detectPacket.setReverse(inputStream.readInt());
                    detectPacket.setJsonSize(inputStream.readInt());
                    detectPacket.setIp(inputStream.readInt());
                    detectPacket.setPort(inputStream.readShort());
                    detectPacket.setTtl(inputStream.readByte());
                    detectPacket.setConnectType(inputStream.readByte());
                    detectPacket.setDatalength(inputStream.readInt());
                    detectPacket.setFileName(file.getName());
                    System.out.println(detectPacket.toString());
                    //发包
                    Global.sendPacketPool.submit(new ScratchTask(detectPacket));
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

