import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public class Client {

    private DetectPacket detectPacket;

    public Client(DetectPacket detectPacket) {
        this.detectPacket = detectPacket;
    }

    public Client() {

    }

    public void connect() throws Exception {
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


}
