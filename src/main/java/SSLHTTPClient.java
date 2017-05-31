import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static java.lang.System.out;

/**
 * Created by Administrator on 2017/5/24 0024.
 */
public class SSLHTTPClient extends Client {


    public SSLHTTPClient(DetectPacket detectPacket) {
        super(detectPacket);
    }

    @Override
    public void connect() throws Exception {
        SSLSocket sslSocket = getSSLSocket();
        X509Certificate[] certificates = (X509Certificate[]) sslSocket.getSession().getPeerCertificates();
        //解析证书，如果有重复就保存version等字段
        byte[] bytes_certificates = new byte[10000];
        for (int i = 0; i < certificates.length; i++) {
            byte[] bytes;
            if (i == 0) {
                bytes = parseCertification(certificates[i]);
                System.arraycopy(bytes, 0, bytes_certificates, 0, bytes.length);
            } else {
                bytes = parsePartCertification(certificates[i]);
                System.arraycopy(bytes, 0, bytes_certificates, parseCertification(certificates[i - 1]).length, bytes.length);
            }
            System.out.println("共有" + certificates.length + "个证书:" + new String(bytes_certificates));
        }
    }

    public byte[] parseCertification(X509Certificate c) throws Exception {
        HashMap<String, Object> hash = new HashMap<String, Object>();
        hash.put("protocol", "HTTPS");
        hash.put("devicename", "");
        hash.put("version", c.getVersion());
        hash.put("serial number", c.getSerialNumber().toString());
        hash.put("signature algorithm", c.getSigAlgName());
        hash.put("issuer", c.getIssuerDN());
        hash.put("validity", c.getNotBefore() + "," + c.getNotAfter());
        hash.put("public key algorithm", c.getPublicKey());
        hash.put("algorithm id", c.getSigAlgOID());
        hash.put("subject", c.getSubjectDN());
        return hash.toString().getBytes();
    }

    public byte[] parsePartCertification(X509Certificate c) throws Exception {
        HashMap<String, Object> hash = new HashMap<String, Object>();
        hash.put("version", c.getVersion());
        hash.put("serial number", c.getSerialNumber().toString());
        hash.put("signature algorithm", c.getSigAlgName());
        hash.put("issuer", c.getIssuerDN());
        hash.put("validity", c.getNotBefore() + "," + c.getNotAfter());
        hash.put("public key algorithm", c.getPublicKey());
        hash.put("algorithm id", c.getSigAlgOID());
        hash.put("subject", c.getSubjectDN());
        return hash.toString().getBytes();
    }

}
