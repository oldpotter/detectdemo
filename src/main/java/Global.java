import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
public class Global {
    public static ExecutorService sendPacketPool = Executors.newFixedThreadPool(Config.detectThreadNum);
    public static ExecutorService writeFilePool = Executors.newSingleThreadExecutor();

}
