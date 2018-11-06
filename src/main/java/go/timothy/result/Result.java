package go.timothy.result;

import go.timothy.request.Request;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TimothyZz
 * @program my-spider
 * @description 解析结果
 * @date 2018-09-30 09:33
 **/
@Data(staticConstructor = "of")
public class Result<T> {
    /**
     * 上一次请求
     */
    private final Request preRequest;
    /**
     * 解析出来后进行下一步请求
     */
    private final List<Request> requests = new ArrayList<>(8);
    /**
     * 解析出来的资源
     */
    private T targetSource;

    /**
     * 添加请求
     *
     * @param request
     * @return go.timothy.result.Result
     * @author TimothyZz
     * @date 2018/11/6 17:14
     */
    public Result addRequest(Request request) {
        this.requests.add(request);
        return this;
    }

    /**
     * 批添加请求
     *
     * @param requests
     * @return go.timothy.result.Result
     * @author TimothyZz
     * @date 2018/11/6 17:14
     */
    public Result addRequests(List<Request> requests) {
        this.requests.addAll(requests);
        return this;
    }

}
