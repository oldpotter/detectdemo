import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/6/5 0005.
 */
public class SSLClient extends BaseClient {
    public SSLClient(DetectPacket detectPacket, PROTOCOL_TYPE protocol_type) {
        super(detectPacket, protocol_type);
    }

    @Override
    public void connect() throws Exception{
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


    /**
     * 获取SSLSocket
     * @return
     * @throws Exception
     */
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
}
