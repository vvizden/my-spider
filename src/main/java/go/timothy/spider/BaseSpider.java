package go.timothy.spider;

import go.timothy.config.RequestConfig;
import go.timothy.constant.HttpMethodEnum;
import go.timothy.http.Header;
import go.timothy.parser.Parser;
import go.timothy.pipeline.Pipeline;
import go.timothy.request.Request;
import go.timothy.response.Response;
import go.timothy.result.Result;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TimothyZz
 * @program my-spider
 * @description 爬虫基类
 * @date 2018-09-28 17:17
 **/
@Data
@Accessors(chain = true)
public abstract class BaseSpider {
    /**
     * 名称
     */
    protected final String name;
    /**
     * 初始请求
     */
    protected List<Request> requests = new ArrayList<>(8);
    /**
     * 数据加工管道
     */
    protected Pipeline pipeline;

    /**
     * 默认请求头
     */
    protected List<Header> headers;

    /**
     * 默认请求配置
     */
    protected RequestConfig requestConfig;

    /**
     * 配置请求方法
     */
    protected HttpMethodEnum method;

    public BaseSpider(String name) {
        this.name = name;
    }

    /**
     * 设置请求路径
     *
     * @param urls
     * @return go.timothy.spider.BaseSpider
     * @author TimothyZz
     * @date 2018/11/2 16:31
     */
    public BaseSpider addRequests(Parser parser, String... urls) {
        if (urls.length > 0) {
            for (String url : urls) {
                Request request = Request.of(this, url, method, parser);
                request.setHeaders(headers);
                request.setRequestConfig(requestConfig);
                requests.add(request);
            }
        }
        return this;
    }

    public Request makeRequest(String url, Parser<Response, Result> parser) {
        Request request = Request.of(this, url, method, parser);
        request.setHeaders(headers);
        request.setRequestConfig(requestConfig);
        return request;
    }

    public abstract Result parse(Response response);

}
