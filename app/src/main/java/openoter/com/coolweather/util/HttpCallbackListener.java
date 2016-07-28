package openoter.com.coolweather.util;

/**
 * 回调服务返回的结果
 */
public interface HttpCallbackListener {

    /**
     * 完成
     * @param response
     */
    void onFinish(String response);

    /**
     * 出错
     * @param e
     */
    void onError(Exception e);
}
