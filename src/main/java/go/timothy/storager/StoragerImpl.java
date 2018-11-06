package go.timothy.storager;

import go.timothy.request.Request;
import go.timothy.result.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author TimothyZz
 * @description 存储器
 * @program my-spider
 * @date 2018-10-19 14:06
 **/
@Slf4j
public class StoragerImpl implements Storager {
    /**
     * 待处理请求
     */
    private final BlockingQueue<Request> requests = new LinkedBlockingQueue<>();
    /**
     * 待处理结果
     */
    private final BlockingQueue<Result> results = new LinkedBlockingQueue<>();

    /**
     * 是否存在请求
     *
     * @param
     * @return boolean
     * @author TimothyZz
     * @date 2018/10/12 16:35
     */
    @Override
    public boolean hasRequest() {
        return requests.size() > 0;
    }

    /**
     * 是否存在
     *
     * @param
     * @return boolean
     * @author TimothyZz
     * @date 2018/10/12 16:36
     */
    @Override
    public boolean hasResult() {
        return results.size() > 0;
    }

    /**
     * 下一个请求
     *
     * @param
     * @return go.timothy.request.Request
     * @author TimothyZz
     * @date 2018/10/12 16:49
     */
    @Override
    public Request nextRequest() {
        while (true) {
            try {
                return requests.take();
            } catch (InterruptedException e) {
                log.error("从请求存储容器取值时出错", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 下一个解析结果
     *
     * @return go.timothy.response.Response
     * @author TimothyZz
     * @date 2018/10/12 16:52
     */
    @Override
    public Result nextResult() {
        while (true) {
            try {
                return results.take();
            } catch (InterruptedException e) {
                log.error("从解析结果存储容器取值时出错", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 清空存储器
     *
     * @return void
     * @author TimothyZz
     * @date 2018/10/26 17:14
     */
    @Override
    public void clear() {
        this.requests.clear();
        this.results.clear();
    }

    /**
     * 存请求
     *
     * @param request
     * @return void
     * @author TimothyZz
     * @date 2018/10/12 17:22
     */
    @Override
    public void putRequest(Request request) {
        try {
            requests.put(request);
        } catch (InterruptedException e) {
            log.error("向请求存储容器存值时出错", e);
        }
    }

    /**
     * 存解析结果
     *
     * @param result
     * @return void
     * @author TimothyZz
     * @date 2018/10/12 17:23
     */
    @Override
    public void putResult(Result result) {
        try {
            results.put(result);
        } catch (InterruptedException e) {
            log.error("向解析结果存储容器存值时出错", e);
        }
    }
}
