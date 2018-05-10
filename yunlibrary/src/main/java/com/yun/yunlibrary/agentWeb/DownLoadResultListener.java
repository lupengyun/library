package  com.yun.yunlibrary.agentWeb;

/**
 * Created by cenxiaozhong on 2017/6/21.
 */

public interface DownLoadResultListener {


    void success(String path);

    void error(String path, String resUrl, String cause, Throwable e);

}
