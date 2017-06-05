import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2017/6/5 0005.
 */
public class BaseClient {
    public enum PROTOCOL_TYPE {
        HTTPS,
        SMTP,
        LDAP,
        IMAP,
        POP3,
        HTTP,
    }

    byte[] HTTPdata = {0x47, 0x45, 0x54, 0x20, 0x2f, 0x20, 0x48, 0x54, 0x54, 0x50, 0x2f, 0x31, 0x2e, 0x30, 0x0d, 0x0a, 0x0d, 0x0a}; //GET / HTTP/1.0
    byte[] SMTPdata = {0x45, 0x48, 0x4c, 0x4f, 0x0d, 0x0a};
    byte[] POP3data = {0x43, 0x41, 0x50, 0x41, 0x0d, 0x0a};
    byte[] IMAPdata = {0x41, 0x30, 0x30, 0x31, 0x20, 0x43, 0x41, 0x50, 0x41, 0x42, 0x49, 0x4c, 0x49, 0x54, 0x59, 0x0d, 0x0a};
    byte[] LDAPdata = {0x30, 0x0c, 0x02, 0x01, 0x01, 0x60, 0x07, 0x02, 0x01, 0x02, 0x04, 0x00, (byte) 0x80, 0x00};


    public DetectPacket detectPacket;

    public PROTOCOL_TYPE protocol_type;

    public byte[] bytesCertificates;

    public BaseClient(DetectPacket detectPacket, PROTOCOL_TYPE protocol_type) {
        this.detectPacket = detectPacket;
        this.protocol_type = protocol_type;
    }

    /**
     * 连接
     * @throws Exception
     */
    public void connect() throws Exception {

    }

    /**
     * 发送请求数据
     * @param out
     * @param bytes
     * @throws Exception
     */
    public void sendRequestBytes(OutputStream out, byte[] bytes) throws Exception {
        out.write(bytes);
        out.flush();
    }

    /**
     * 读取返回数据
     * @param in
     * @return
     * @throws IOException
     */
    public byte[] readResponse(InputStream in) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;
        while ((count = in.read(data, 0, 1024)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return outStream.toByteArray();
    }
}
