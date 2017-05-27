import javax.annotation.processing.SupportedSourceVersion;

/**
 * 发包类
 */
class ScratchTask implements Runnable {

    private static final int TCP = 0x06;
    private static final int TCP_HTTP = 0x020050;
    private static final int HTTPS = 0x0201BB;

    private DetectPacket detectPacket = null;

    public ScratchTask(DetectPacket detectPacket) {
        this.detectPacket = detectPacket;
    }

    @Override
    public void run() {
        Client client = null;
        if (detectPacket.getConnectType() == TCP) {
            if (detectPacket.getSendCode() == HTTPS) {
                client = new SSLHTTPClient(detectPacket);
            } else {
                System.out.println("Others");
            }
        } else {
            System.out.println("UDP");
        }
        try {
            if (client != null) {
                client.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
