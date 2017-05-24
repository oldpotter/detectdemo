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

    @Override
    public void run() {
        //不保存空文件
        if(certificateInfo== null || responseData == null){
            return;
        }
        try {
            StringBuffer stringBuffer = new StringBuffer(detectPacket.getFileName()).insert(detectPacket.getFileName().length() - 4, "_NO");
            String newFileName = stringBuffer.toString();
            FileOutputStream fileOutputStream = new FileOutputStream(newFileName, true);
            LittleEndianDataOutputStream littleEndianDataOutputStream = new LittleEndianDataOutputStream(fileOutputStream);

            //消息頭
            littleEndianDataOutputStream.writeInt((int) (System.currentTimeMillis() / 1000));
            littleEndianDataOutputStream.writeInt(detectPacket.getSendCode());
            littleEndianDataOutputStream.writeInt(detectPacket.getReverse());
            littleEndianDataOutputStream.writeInt(detectPacket.getIp());
            littleEndianDataOutputStream.writeShort(detectPacket.getPort());
            littleEndianDataOutputStream.writeByte(detectPacket.getTtl());
            littleEndianDataOutputStream.writeByte(detectPacket.getConnectType());
            int dataLength = 0;
            if (certificateInfo != null) {
                dataLength += certificateInfo.length;
            }
            if (responseData != null) {
                dataLength += responseData.length;
            }
            littleEndianDataOutputStream.writeInt(dataLength);

            //消息体
            littleEndianDataOutputStream.write(certificateInfo);
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
