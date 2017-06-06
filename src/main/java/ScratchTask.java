import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.annotation.processing.SupportedSourceVersion;

/**
 * 发包类
 */
class ScratchTask implements Runnable {

    private static final int TCP = 0x06;
    private static final int UDP = 0x11;
    private static final int HTTPS = 0x0201BB;
    private static final int HTTP = 0x20050;
    private static final int SMTP = 0x0201D1;
    private static final int LDAP = 0x02027C;
    private static final int IMAP = 0x0203E1;
    private static final int POP3 = 0x0203E3;

    private DetectPacket detectPacket = null;

    public ScratchTask(DetectPacket detectPacket) {
        this.detectPacket = detectPacket;
    }

    @Override
    public void run() {
        BaseClient.PROTOCOL_TYPE protocol_type = null;
        int sendCode = detectPacket.getSendCode();
        if (detectPacket.getConnectType() == TCP) {
            switch (sendCode) {
                case HTTP:
                    protocol_type = BaseClient.PROTOCOL_TYPE.HTTP;
                    break;
                case HTTPS:
                    protocol_type = BaseClient.PROTOCOL_TYPE.HTTPS;
                    break;
                case SMTP:
                    protocol_type = BaseClient.PROTOCOL_TYPE.SMTP;
                    break;
                case LDAP:
                    protocol_type = BaseClient.PROTOCOL_TYPE.LDAP;
                    break;
                case IMAP:
                    protocol_type = BaseClient.PROTOCOL_TYPE.IMAP;
                    break;
                case POP3:
                    protocol_type = BaseClient.PROTOCOL_TYPE.POP3;
                    break;
                default:
                    System.out.println("Unknow send code" + sendCode);
            }
        }

        //连接
        // TODO: 2017/6/5 0005 完成这里 
        try {
            BaseClient client;
            if (protocol_type == BaseClient.PROTOCOL_TYPE.HTTP) {
                client = new Client(detectPacket, protocol_type);
            } else {
                client = new SSLClient(detectPacket, protocol_type);
            }
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
