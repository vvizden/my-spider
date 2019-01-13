package meizitu;

import go.timothy.config.RequestConfig;
import go.timothy.constant.UserAgentConst;
import go.timothy.engine.Engine;
import go.timothy.http.HttpClientImpl;
import go.timothy.parser.Parser;
import go.timothy.pipeline.Pipeline;
import go.timothy.request.Request;
import go.timothy.response.Entity;
import go.timothy.response.Response;
import go.timothy.result.Result;
import go.timothy.spider.BaseSpider;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author TimothyZz
 * @description 妹子图爬虫
 * @program my-spider
 * @date 2018-11-07 09:59
 **/
@Slf4j
public class MzituSpider {
    private static HttpClientImpl httpClient = HttpClientImpl.of();
    private static BaseSpider meizituSpider = BaseSpider.of("妹子图");
    private final static String DIR = "C:\\Users\\mbwl\\Desktop\\meizitu";

    static {
        RequestConfig requestConfig = RequestConfig.builder()
                .requestIntervalMillisecond(1_000)
                .connectTimeout(5_000)
                .socketTimeout(5_000)
                .build();
        meizituSpider.setRequestConfig(requestConfig);

        Pipeline<List<String>, String> pipeline = (t, preRequest) -> {
            if (t != null && !t.isEmpty()) {
                t.forEach(e -> {
                    URL url = null;
                    try {
                        url = new URL(e);
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }
                    String path;
                    if (url != null) {
                        path = url.getPath();
                    } else {
                        path = "\\" + System.currentTimeMillis() + ".jpg";
                    }
                    Request request = Request.of(e);
                    request.addHeader("User-Agent", UserAgentConst.CHROME_WIN10)
                            .addHeader("Referer", preRequest.getUrl());
                    log.info("开始下载：【{}】", e);
                    Response response = httpClient.get(request);
                    Entity entity = response.getEntity();
                    String targetUri = DIR + path;
                    entity.saveFile(targetUri);
                    log.info("图片【{}】下载完毕，本机位置：【{}】", e, targetUri);
                });
            }
            return null;
        };

        meizituSpider.setPipeline(pipeline);

        Parser<Response, Result<List<String>>> contentParser = (response) -> {
            if (response != null) {
                Request request = response.getRequest();
                Entity entity = response.getEntity();
                BaseSpider spider = request.getSpider();
                Parser<Response, Result> parser = request.getParser();
                Optional<Element> first = entity.css("body > div.main div.main-image > p > a")
                        .stream().findFirst();
                if (first.isPresent()) {
                    Element element = first.get();
                    List<String> urls = element.select("img").stream()
                            .map(e -> e.attr("src"))
                            .collect(Collectors.toList());

                    String nextPage = element.attr("href");
                    Request r = spider.makeRequest(nextPage, parser);
                    r.addHeader("Referer", request.getUrl());

                    Result<List<String>> result = Result.of(request);
                    result.addRequest(r);
                    result.setTargetSource(urls);
                    result.setPipeline(spider.getPipeline());
                    return result;
                }
            }
            return null;
        };

        Parser<Response, Result<List<String>>> mainParser = (response) -> {
            if (response != null) {
                Request request = response.getRequest();
                BaseSpider meizituSpider = request.getSpider();
                Parser<Response, Result> parser = request.getParser();
                Entity entity = response.getEntity();
                Elements elements = entity.css("ul#pins > li > a");
                List<Request> requests = elements.stream()
                        .map(e -> e.attr("href"))
                        .map(url -> {
                            Request r = meizituSpider.makeRequest(url, contentParser);
                            return r.addHeader("Referer", request.getUrl());
                        }).collect(Collectors.toList());

                Elements nextPageElement = response.getEntity().css("body > div.main div.postlist div.nav-links > a.next");
                Optional<Element> first = nextPageElement.stream().findFirst();
                if (first.isPresent()) {
                    Element element = first.get();
                    String href = element.attr("href");
                    String nextPage = "https://www.mzitu.com" + href;
                    Request r = meizituSpider.makeRequest(nextPage, parser);
                    request.addHeader("Referer", request.getUrl());
                    requests.add(r);
                }

                Result<List<String>> result = Result.of(request);
                return result.addRequests(requests);
            }
            return null;
        };

        meizituSpider.setMainParser(mainParser);

        meizituSpider.addUrls("https://www.mzitu.com/");

    }

    public static void main(String[] args) {
        httpClient.start();
        Engine engine = Engine.of();
        engine.registerSpider(meizituSpider).start();
    }

}
