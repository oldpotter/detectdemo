import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public class HTTPSClient extends Client {
    private DetectPacket detectPacket;

    public HTTPSClient(DetectPacket detectPacket) {
        this.detectPacket = detectPacket;
    }

    @Override
    public void connect() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            String ipAddress = "https://" + detectPacket.getIpString();
            URL destinationURL = new URL(ipAddress);
            HttpsURLConnection conn = (HttpsURLConnection) destinationURL.openConnection();
            conn.connect();

            Certificate[] certs = conn.getServerCertificates();
            byte[] head = detectPacket.getBytes();
            detectPacket.setTime((int) System.currentTimeMillis());
            detectPacket.setDatalength(certs.length == 0 ? 0 : certs[0].getEncoded().length);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }

    }
}
