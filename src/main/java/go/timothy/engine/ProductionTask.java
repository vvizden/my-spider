package go.timothy.engine;

import go.timothy.constant.HttpMethodEnum;
import go.timothy.http.HttpClient;
import go.timothy.parser.Parser;
import go.timothy.request.Request;
import go.timothy.response.Response;
import go.timothy.result.Result;
import go.timothy.spider.BaseSpider;
import go.timothy.storager.Storager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author TimothyZz
 * @description 生产任务
 * @program my-spider
 * @date 2018-11-06 15:08
 **/
@Slf4j
@RequiredArgsConstructor(staticName = "of")
public class ProductionTask implements Runnable {
    private final Request request;
    private final HttpClient httpClient;
    private final Storager storager;

    @Override
    public void run() {
        BaseSpider spider = request.getSpider();
        log.info("开始请求【{}】【{}】", spider.getName(), request.getUrl());

        Response response = null;

        HttpMethodEnum method = request.getMethod();
        if (HttpMethodEnum.GET.equals(method)) {
            response = this.httpClient.get(request);
        } else if (HttpMethodEnum.POST.equals(method)) {
            response = this.httpClient.post(request);
        } else {
            log.info("【{}】暂不支持此【{}】请求方式", request.getUrl(), method);
        }

        log.info("【{}】请求结束", request.getUrl());
        if (response != null) {
            Parser<Response, Result> parser = request.getParser();
            Result result = parser.parse(response);
            if (result != null) {
                this.storager.putResult(result);
            }
        }
    }
}
