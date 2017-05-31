import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.annotation.processing.SupportedSourceVersion;

/**
 * 发包类
 */
class ScratchTask implements Runnable {

    private static final int TCP = 0x06;
    private static final int UDP = 0x11;
    private static final int HTTPS = 0x0201BB;
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
        Client client = null;
        int connectType = detectPacket.getConnectType();
        int sendCode = detectPacket.getSendCode();
        switch (sendCode) {
            case HTTPS:
                System.out.println("HTTPS" + connectType);
                break;
            case SMTP:
                System.out.println("SMTP" + connectType);
                break;
            case LDAP:
                System.out.println("LDAP" + connectType);
                break;
            case IMAP:
                System.out.println("IMAP" + connectType);
                break;
            case POP3:
                System.out.println("POP3" + connectType);
                break;
            default:
                System.out.println("Unknow send code" + sendCode);
        }

//        //连接
//        try {
//            if (client != null) {
//                client.connect();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
