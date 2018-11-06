package go.timothy.storager;

import go.timothy.request.Request;
import go.timothy.result.Result;

/**
 * 存储器
 *
 * @author TimothyZz
 * @date 2018/10/26 15:39
 */
public interface Storager {

    /**
     * 存请求
     *
     * @param request
     * @return void
     * @author TimothyZz
     * @date 2018/10/12 17:22
     */
    void putRequest(Request request);

    /**
     * 存解析结果
     *
     * @param result
     * @return void
     * @author TimothyZz
     * @date 2018/10/12 17:23
     */
    void putResult(Result result);

    /**
     * 是否存在请求
     *
     * @param
     * @return boolean
     * @author TimothyZz
     * @date 2018/10/12 16:35
     */
    boolean hasRequest();

    /**
     * 是否存在解析结果
     *
     * @param
     * @return boolean
     * @author TimothyZz
     * @date 2018/10/12 16:36
     */
    boolean hasResult();

    /**
     * 下一个请求
     *
     * @param
     * @return go.timothy.request.Request
     * @author TimothyZz
     * @date 2018/10/12 16:49
     */
    Request nextRequest();

    /**
     * 下一个解析结果
     *
     * @param
     * @return go.timothy.response.Response
     * @author TimothyZz
     * @date 2018/10/12 16:52
     */
    Result nextResult();

    /**
     * 清空存储器
     *
     * @param
     * @return void
     * @author TimothyZz
     * @date 2018/10/26 17:14
     */
    void clear();
}
