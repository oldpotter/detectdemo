/**
 * 发包类
 */
class ScratchTask implements Runnable {

    private static final int TCP = 0x06;
    private static final int TCP_HTTP = 0x020050;

    private DetectPacket detectPacket = null;

    public ScratchTask(DetectPacket detectPacket) {
        this.detectPacket = detectPacket;
    }

    @Override
    public void run() {
        Client client = null;
        if (detectPacket.getConnectType() == TCP) {
            client = new HTTPSClient(detectPacket);
        }
        client.connect();
    }
}
