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
        Client.PROTOCOL_TYPE protocol_type = null;
        int sendCode = detectPacket.getSendCode();
        if (detectPacket.getConnectType() == TCP) {
            switch (sendCode) {
                case HTTPS:
                    protocol_type = Client.PROTOCOL_TYPE.HTTPS;
                    break;
                case SMTP:
                    protocol_type = Client.PROTOCOL_TYPE.SMTP;
                    break;
                case LDAP:
                    protocol_type = Client.PROTOCOL_TYPE.LDAP;
                    break;
                case IMAP:
                    protocol_type = Client.PROTOCOL_TYPE.IMAP;
                    break;
                case POP3:
                    protocol_type = Client.PROTOCOL_TYPE.POP3;
                    break;
                default:
                    System.out.println("Unknow send code" + sendCode);
            }
        }

        //连接
        try {
            if (protocol_type != null) {
                Client client = new Client(detectPacket, protocol_type);
                client.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
