import com.google.common.io.LittleEndianDataOutputStream;
import com.sun.org.apache.regexp.internal.RE;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/19 0019.
 */
public class ScratchWriteTask implements Runnable {

    private static final int RESPONSE_DATA_MAX_LENGTH = 1024 * 1024 * 2;

    private byte[] responseData = null;
    private DetectPacket detectPacket = null;
    private byte[] certificateInfo = null;

    public ScratchWriteTask(byte[] responseData, DetectPacket detectPacket, byte[] certificateInfo) {
        if (responseData != null) {
            this.responseData = responseData.length > RESPONSE_DATA_MAX_LENGTH ? Arrays.copyOf(responseData, RESPONSE_DATA_MAX_LENGTH) : responseData;
        }
        this.detectPacket = detectPacket;
        this.certificateInfo = certificateInfo;
    }

    public ScratchWriteTask(byte[] responseData, DetectPacket detectPacket) {
        this.responseData = responseData;
        this.detectPacket = detectPacket;
    }

    @Override
    public void run() {
        try {
            StringBuffer stringBuffer = new StringBuffer(detectPacket.getFileName()).insert(detectPacket.getFileName().length() - 4, "_NO");
            String newFileName = stringBuffer.toString();
            FileOutputStream fileOutputStream = new FileOutputStream(Config.outputFilePath + File.separator + newFileName, true);
            LittleEndianDataOutputStream littleEndianDataOutputStream = new LittleEndianDataOutputStream(fileOutputStream);

            //消息頭
            littleEndianDataOutputStream.writeInt((int) (System.currentTimeMillis() / 1000));//4,time
            littleEndianDataOutputStream.writeInt(detectPacket.getSendCode());//4,sendcode
            littleEndianDataOutputStream.writeShort(detectPacket.getReverse());//2,reverse
            littleEndianDataOutputStream.writeShort(detectPacket.getJsonSize());//2,json size
            littleEndianDataOutputStream.writeInt(detectPacket.getIp());//4
            littleEndianDataOutputStream.writeShort(detectPacket.getPort());//2
            littleEndianDataOutputStream.writeByte(detectPacket.getTtl());//1
            littleEndianDataOutputStream.writeByte(detectPacket.getConnectType());//1
            int dataLength = 0;
            if (certificateInfo != null) {
                dataLength += certificateInfo.length;
            }
            if (responseData != null) {
                dataLength += responseData.length;
            }
            littleEndianDataOutputStream.writeInt(dataLength);//4,data length

            //消息体（证书 + 2k数据）
            if (certificateInfo != null) {
                littleEndianDataOutputStream.write(certificateInfo);
            }
            littleEndianDataOutputStream.write(responseData);
            littleEndianDataOutputStream.flush();
            littleEndianDataOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
