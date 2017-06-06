import java.net.Socket;

/**
 * Created by Administrator on 2017/6/5 0005.
 */
public class Client extends BaseClient {
    public Client(DetectPacket detectPacket, PROTOCOL_TYPE protocol_type) {
        super(detectPacket, protocol_type);
    }

    @Override
    public void connect() throws Exception {
        Socket socket = new Socket(detectPacket.getIpString(), detectPacket.getPort());
        byte[] request_data = new byte[1024];
        switch (this.protocol_type) {
            case HTTP:
                request_data = HTTPdata;
                break;
        }
        detectPacket.setJsonSize(0);
        sendRequestBytes(socket.getOutputStream(), request_data);
        Global.writeFilePool.submit(new ScratchWriteTask(readResponse(socket.getInputStream()), detectPacket));
    }

}
