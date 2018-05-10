package  com.yun.yunlibrary.agentWeb;

/**
 * Created by cenxiaozhong on 2017/8/17.
 */

public interface PermissionInterceptor {

    boolean intercept(String url, String[] permissions, String action);

}
