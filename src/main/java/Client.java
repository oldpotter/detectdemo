import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public class Client {

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
    byte[] POP3data = {0x43, 0x41, 0x50, 0x41, 0x0d, (byte) 0x0a};
    byte[] IMAPdata = {0x41, 0x30, 0x30, 0x31, 0x20, 0x43, 0x41, 0x50, 0x41, 0x42, 0x49, 0x4c, 0x49, 0x54, 0x59, 0x0d, 0x0a};
    byte[] LDAPdata = {0x30, 0x0c, 0x02, 0x01, 0x01, 0x60, 0x07, 0x02, 0x01, 0x02, 0x04, 0x00, (byte) 0x80, 0x00};

    public DetectPacket detectPacket;

    /**
     * 证书信息
     */
    private byte[] bytesCertificates = new byte[10000];

    private PROTOCOL_TYPE protocol_type;

    public Client(DetectPacket detectPacket, PROTOCOL_TYPE protocol_type) {
        this.detectPacket = detectPacket;
        this.protocol_type = protocol_type;
    }

    public void connect() throws Exception {
        SSLSocket sslSocket = getSSLSocket();
        X509Certificate[] certificates = (X509Certificate[]) sslSocket.getSession().getPeerCertificates();
        bytesCertificates = parssCertifications(certificates);
        detectPacket.setJsonSize(bytesCertificates.length);//set json size
        byte[] request_data = new byte[1024];
        switch (this.protocol_type) {
            case HTTPS:
                request_data = HTTPdata;
                break;
            case IMAP:
                request_data = IMAPdata;
                break;
            case LDAP:
                request_data = LDAPdata;
                break;
            case POP3:
                request_data = POP3data;
                break;
            case SMTP:
                request_data = SMTPdata;
                break;
        }
        sendRequestBytes(sslSocket.getOutputStream(), request_data);//send request data
        //write task
        Global.writeFilePool.submit(new ScratchWriteTask(readResponse(sslSocket.getInputStream()), detectPacket, bytesCertificates));
    }

    public SSLSocket getSSLSocket() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        // Install the all-trusting trust manager
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        return (SSLSocket) sslContext.getSocketFactory().createSocket(detectPacket.getIpString(), detectPacket.getPort());
    }


    /**
     * 解析证书
     *
     * @param certificates
     * @return
     * @throws Exception
     */
    public byte[] parssCertifications(X509Certificate[] certificates) throws Exception {
        HashMap<String, Object> hash = new HashMap<String, Object>();
        for (int i = 0; i < certificates.length; i++) {
            X509Certificate c = certificates[i];
            if (i == 0) {
                String strProtocol = null;
                switch (this.protocol_type) {
                    case HTTPS:
                        strProtocol = "HTTPS";
                        break;
                    case IMAP:
                        strProtocol = "IMAP";
                        break;
                    case LDAP:
                        strProtocol = "LDAP";
                        break;
                    case POP3:
                        strProtocol = "POP3";
                        break;
                    case SMTP:
                        strProtocol = "SMTP";
                        break;
                }
                hash.put("protocol", strProtocol);
                hash.put("devicename", "");
            }

            hash.put("version", c.getVersion());
            hash.put("serial number", c.getSerialNumber().toString());
            hash.put("signature algorithm", c.getSigAlgName());
            hash.put("issuer", c.getIssuerDN());
            hash.put("validity", c.getNotBefore() + "," + c.getNotAfter());
            hash.put("public key algorithm", c.getPublicKey());
            hash.put("algorithm id", c.getSigAlgOID());
            hash.put("subject", c.getSubjectDN());
        }

        return hash.toString().getBytes();
    }

    private void sendRequestBytes(OutputStream out, byte[] bytes) throws Exception {
        out.write(bytes);
        out.flush();
    }

    private byte[] readResponse(InputStream in) throws IOException {
        return InputStreamTOByte(in);
    }

    /**
     * 将InputStream转换成byte数组
     *
     * @param in InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] InputStreamTOByte(InputStream in) throws IOException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;
        while ((count = in.read(data, 0, 1024)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return outStream.toByteArray();
    }
}
