package go.timothy;

import go.timothy.config.EngineConfigImpl;
import go.timothy.constant.HttpMethodEnum;
import go.timothy.constant.UserAgentConst;
import go.timothy.engine.Engine;
import go.timothy.http.Header;
import go.timothy.http.HttpClientImpl;
import go.timothy.parser.Parser;
import go.timothy.pipeline.Pipeline;
import go.timothy.request.Request;
import go.timothy.response.Response;
import go.timothy.result.Result;
import go.timothy.spider.BaseSpider;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author TimothyZz
 * @description 程序入口
 * @program my-spider
 * @date 2018-11-02 16:05
 **/
@Slf4j
public class Entry {
    public static void main(String[] args) {

        Parser tuParser = (Parser<Response, Result>) response -> {
            Elements elements = response.getEntity().css("#picture > p > img");
            List<String> src = elements.stream().map(element -> element.attr("src")).collect(Collectors.toList());
            return new Result(src, response.getRequest());

        };

        BaseSpider spider = new BaseSpider("妹子图") {
            @Override
            public Result parse(Response response) {
                Result result = new Result<>();
                Elements elements = response.getEntity().css("#maincontent > div.inWrap > ul > li:nth-child(1) > div > div > a");
                log.info("elements size: {}", elements.size());

                List<String> href = elements.stream()
                        .map(element -> element.attr("href"))
                        .collect(Collectors.toList());
                href.forEach(e -> {
                    result.addRequest(this.makeRequest(e, tuParser));
                });
                //result.addRequests(href1);

                // 获取下一页 URL
                Optional<Element> nextEl = response.getEntity().css("#wp_page_numbers > ul > li > a").stream().filter(element -> "下一页".equals(element.text())).findFirst();
                if (nextEl.isPresent()) {
                    String nextPageUrl = "http://www.meizitu.com/a/" + nextEl.get().attr("href");
                    Request nextReq = this.makeRequest(nextPageUrl, this::parse);
                    result.addRequest(nextReq);
                }
                return result;
            }
        };
        ArrayList<Header> headers = new ArrayList<>();
        headers.add(Header.of("User-Agent", UserAgentConst.CHROME_WIN10));
        headers.add(Header.of("Content-Type", "text/html;utf-8"));
        spider.setHeaders(headers);

        spider.setMethod(HttpMethodEnum.GET);
        spider.setRequestConfig(new go.timothy.config.RequestConfig(20_000, 20_000));
        spider.addRequests(tuParser, "http://www.meizitu.com/a/sexy.html",
                //"http://www.meizitu.com/a/fuli.html",
                "http://www.meizitu.com/a/legs.html");

        HttpClientImpl httpClient = new HttpClientImpl();
        httpClient.start();
        Pipeline pipeline = (Pipeline<List<String>, String>) (urls, request) -> {
            urls.forEach(imgUrl -> {
                log.info("开始下载: {}", imgUrl);

                Response response = httpClient.get(spider.makeRequest(imgUrl, tuParser));
                try {
                    Files.copy(response.getEntity().getInputStream(), Paths.get("C:\\Users\\mbwl\\Desktop\\meizi\\" + System.currentTimeMillis() + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            log.info("[{}] 图片下载 OJ8K.", request.getUrl());
            return request.getUrl();
        };

        spider.setPipeline(pipeline);

        Engine engine = new Engine(new EngineConfigImpl());
        engine.registerSpider(spider);
        engine.start();
    }
}
