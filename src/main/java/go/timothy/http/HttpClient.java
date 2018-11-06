package go.timothy.http;

import go.timothy.request.Request;
import go.timothy.response.Response;

/**
 * HttpClient
 *
 * @author TimothyZz
 * @date 2018/10/12 15:58
 */
public interface HttpClient {

    /**
     * get请求
     *
     * @param request
     * @return go.timothy.response.Response
     * @author TimothyZz
     * @date 2018/10/12 16:00
     */
    Response get(Request request);

    /**
     * post请求
     *
     * @param request
     * @return go.timothy.response.Response
     * @author TimothyZz
     * @date 2018/10/12 16:01
     */
    Response post(Request request);

    /**
     * 启动客户端
     *
     * @param
     * @return void
     * @author TimothyZz
     * @date 2018/10/13 11:19
     */
    void start();

    /**
     * 关闭客户端
     *
     * @param
     * @return void
     * @author TimothyZz
     * @date 2018/10/13 11:19
     */
    void close();

}
