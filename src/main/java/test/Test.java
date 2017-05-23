package test;

/**
 * Created by Administrator on 2017/5/23 0023.
 */
public class Test {
    public static void main(String[] args){
        String string = "abcd.dat";
        StringBuffer stringBuffer = new StringBuffer(string);
        stringBuffer.insert(string.length() - 4,"_NO");
        System.out.println("new string:" + stringBuffer.toString());
    }
}
