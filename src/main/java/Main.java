import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.PatternFilenameFilter;
import sun.misc.IOUtils;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/5/18 0018.
 */
public class Main {
    public static Properties properties = new Properties();

    public static void main(String[] args) {
        String configPath = args[0];
        try {
            properties.load(new FileInputStream(configPath));
            Config.sourceFilePath = properties.getProperty("DETECT_IP_DIR");
            Config.outputFilePath = properties.getProperty("PACKET_INFO_DIR");
            Config.debug = Boolean.valueOf(properties.getProperty("DEBUG"));
            Config.detectInterval = Integer.valueOf(properties.getProperty("DETECT_INTERVAL"));
            Config.detectThreadNum = Integer.valueOf(properties.getProperty("DETECT_THREAD_NUM"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new FileTask(), 0, Config.detectInterval);
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
        FileProcess.process(files);
    }
}

/**
 * 解析源文件
 */
class FileProcess {

    /**
     * 处理文件
     *
     * @param files
     */
    public static void process(File[] files) {
        for (File file : files) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                FileChannel fileChannel = randomAccessFile.getChannel();
                FileLock fileLock = null;

                //文件加锁
                try {
                    fileLock = fileChannel.tryLock();
                    //上锁失败
                    if (fileLock == null) {
                        continue;
                    }
                } catch (Exception e) {
                    continue;
                }

                randomAccessFile.seek(0);
                //一次性全部读取
                byte[] bytes = new byte[(int) randomAccessFile.length()];
                randomAccessFile.read(bytes);

                LittleEndianDataInputStream inputStream = new LittleEndianDataInputStream(new ByteArrayInputStream(bytes));
                while (inputStream.available() >= 24) {
                    //放到detectpacket
                    DetectPacket detectPacket = new DetectPacket();
                    detectPacket.setTime(inputStream.readInt());
                    detectPacket.setSendCode(inputStream.readInt());
                    detectPacket.setReverse(inputStream.readInt());
                    detectPacket.setIp(inputStream.readInt());
                    detectPacket.setPort(inputStream.readShort());
                    detectPacket.setTtl(inputStream.readByte());
                    detectPacket.setConnectType(inputStream.readByte());
                    detectPacket.setDatalength(inputStream.readInt());
                    detectPacket.setFileName(file.getName());
//                    System.out.println(detectPacket.toString());
                    //发包
                    Global.sendPacketPool.submit(new ScratchTask(detectPacket));
                }

                inputStream.close();
                //回收
                fileLock.release();
                fileChannel.close();
                randomAccessFile.close();
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

