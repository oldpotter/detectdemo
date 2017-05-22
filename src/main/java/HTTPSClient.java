import com.google.common.io.LittleEndianDataOutputStream;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

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
            HttpsURLConnection connection = (HttpsURLConnection) destinationURL.openConnection();
            connection.connect();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            byte[] responseData = new byte[bufferedInputStream.available()];
            X509Certificate cert = (X509Certificate) (connection.getServerCertificates())[0];
            //写操作
            Global.writeFilePool.submit(new ScratchWriteTask(responseData, detectPacket, parse(cert)));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

    }

    public static byte[] parse(X509Certificate c) {
        HashMap<String, Object> hash = new HashMap<String, Object>();
        hash.put("protocol", "SSL");
        hash.put("devicename", "");
        hash.put("version", c.getVersion());
        hash.put("serial number", c.getSerialNumber().toString());
        hash.put("signature algorithm", c.getSigAlgName());
        hash.put("issuer", c.getIssuerDN());
        hash.put("validity", c.getNotBefore() + "," +c.getNotAfter());
        hash.put("public key algorithm", c.getPublicKey());
        hash.put("algorithm id", c.getSigAlgOID());
        hash.put("subject", c.getSubjectDN());
        return hash.toString().getBytes();
    }
}
