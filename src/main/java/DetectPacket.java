/**
 * Created by Administrator on 2017/5/18 0018.
 */
public class DetectPacket {
    private int time;
    private int sendCode;
    private int reverse;
    private int jsonSize;
    private int ip;
    private int port;
    private int ttl;
    private int connectType;
    private int datalength;
    private String fileName;

    public int getJsonSize() {
        return jsonSize;
    }

    public void setJsonSize(int jsonSize) {
        this.jsonSize = jsonSize;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSendCode() {
        return sendCode;
    }

    public void setSendCode(int sendCode) {
        this.sendCode = sendCode;
    }

    public int getReverse() {
        return reverse;
    }

    public void setReverse(int reverse) {
        this.reverse = reverse;
    }

    public int getIp() {
        return ip;
    }

    public String getIpString() {
        return Tools.longToIp(this.ip);
    }

    public void setIp(int ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    public int getDatalength() {
        return datalength;
    }

    public void setDatalength(int datalength) {
        this.datalength = datalength;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "file:" + fileName + ", ip:" + getIpString() + " ,connectType:" + connectType + ",port:" + port + ", reserve:" + reverse + ", time:" + time + ",sendCode:" + sendCode + ",jsonSize:" + jsonSize;
    }

}


