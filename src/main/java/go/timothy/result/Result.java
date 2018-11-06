package go.timothy.result;

import go.timothy.request.Request;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TimothyZz
 * @program my-spider
 * @description 解析结果
 * @date 2018-09-30 09:33
 **/
@Data
@NoArgsConstructor
public class Result<T> {
    /**
     * 解析出来后进行下一步请求
     */
    private final List<Request> requests = new ArrayList<>(16);
    /**
     * 解析出来的资源
     */
    private T item;
    /**
     * 上一次请求
     */
    private Request request;

    public Result(T item, Request request) {
        this.item = item;
        this.request = request;
    }

    public Result addRequest(Request request) {
        this.requests.add(request);
        return this;
    }

    public Result addRequests(List<Request> requests) {
        this.requests.addAll(requests);
        return this;
    }

}
