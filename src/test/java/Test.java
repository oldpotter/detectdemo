import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Date;

import static java.lang.System.out;

/**
 * Created by Administrator on 2017/5/24 0024.
 */
public class Test {

    public static void main(String[] args) {
//        out.println("pid:" +getPID());
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        try {
            testFileLock();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getPID() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        return pid;
    }


    public static final String DIR = "E:\\test_lock";
    public static final String PATH_LOG = DIR + File.separator + "log.txt";

    public static void testFileLock() throws FileNotFoundException {

        File[] files = new File(DIR).listFiles();
        for (File f :
                files) {
            //跳过文件夹 和 log文件
            if (!f.isFile() || f.getName() == "log.txt") {
                continue;
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(f, "rw");
            FileChannel fileChannel = randomAccessFile.getChannel();
            FileLock fileLock = null;
            String filename = f.getName();

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


            try {
                FileWriter fw = new FileWriter(PATH_LOG, true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(randomAccessFile.readLine());
                bw.write(",pid:" + getPID());
                bw.write('\n');
                bw.flush();
                bw.close();
                fw.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            //删除文件
            f.deleteOnExit();

            //休息
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //回收
            if (fileLock != null) {
                try {
                    fileLock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
