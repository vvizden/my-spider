package go.timothy.http;

import go.timothy.config.RequestConfig;
import go.timothy.detector.TextDetector;
import go.timothy.request.Request;
import go.timothy.response.Entity;
import go.timothy.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author TimothyZz
 * @description httpClient实例
 * @program my-spider
 * @date 2018-10-19 14:17
 **/
@Slf4j
public class HttpClientImpl implements HttpClient {
    private final CloseableHttpAsyncClient httpClient;

    private HttpClientImpl() {
        this.httpClient = HttpAsyncClients.createDefault();
    }

    private HttpClientImpl(RequestConfig defaultHttpRequestConfig) {
        this.httpClient = HttpAsyncClients.custom()
                .setDefaultRequestConfig(convertRequestConfig(defaultHttpRequestConfig))
                .build();
    }

    public static HttpClientImpl of() {
        return new HttpClientImpl();
    }

    public static HttpClientImpl of(RequestConfig defaultHttpRequestConfig) {
        return new HttpClientImpl(defaultHttpRequestConfig);
    }

    /**
     * get请求
     *
     * @param request
     * @return go.timothy.response.Response
     * @author TimothyZz
     * @date 2018/10/12 16:00
     */
    @Override
    public Response get(Request request) {
        HttpGet httpGet = new HttpGet(request.getUrl());
        List<Header> headers = request.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(e -> {
                if (e != null) {
                    httpGet.setHeader(e.getName(), e.getValue());
                }
            });
        }

        if (request.getRequestConfig() != null) {
            httpGet.setConfig(convertRequestConfig(request.getRequestConfig()));
        }

        Future<HttpResponse> responseFuture = httpClient.execute(httpGet, null);
        HttpResponse httpResponse;
        try {
            httpResponse = responseFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.toString());
            return null;
        }

        return convertResponse(httpResponse, request);
    }

    /**
     * post请求
     *
     * @param request
     * @return go.timothy.response.Response
     * @author TimothyZz
     * @date 2018/10/12 16:01
     */
    @Override
    public Response post(Request request) {
        HttpPost httpPost = new HttpPost(request.getUrl());
        List<Header> headers = request.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(e -> httpPost.setHeader(e.getName(), e.getValue()));
        }

        if (request.getRequestConfig() != null) {
            httpPost.setConfig(convertRequestConfig(request.getRequestConfig()));
        }

        Future<HttpResponse> responseFuture = httpClient.execute(httpPost, null);
        HttpResponse httpResponse;
        try {
            httpResponse = responseFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.toString());
            return null;
        }

        return convertResponse(httpResponse, request);
    }

    /**
     * 启动客户端
     *
     * @return void
     * @author TimothyZz
     * @date 2018/10/13 11:19
     */
    @Override
    public void start() {
        if (httpClient.isRunning()) {
            throw new IllegalStateException("httpClient已启动");
        }
        this.httpClient.start();
        log.info("httpClient已启动");
    }

    /**
     * 关闭客户端
     *
     * @return void
     * @author TimothyZz
     * @date 2018/10/13 11:19
     */
    @Override
    public void close() {
        try {
            this.httpClient.close();
            log.info("httpClient已关闭");
        } catch (IOException e) {
            log.error("httpClient关闭出错", e);
        }
    }

    /**
     * 转化成第三方请求配置
     *
     * @param requestConfig
     * @return org.apache.http.client.config.RequestConfig
     * @author TimothyZz
     * @date 2018/10/23 15:18
     */
    private org.apache.http.client.config.RequestConfig convertRequestConfig(RequestConfig requestConfig) {
        return org.apache.http.client.config.RequestConfig.custom()
                .setConnectTimeout(requestConfig.getConnectTimeout())
                .setSocketTimeout(requestConfig.getSocketTimeout())
                .build();
    }

    /**
     * 将第三方响应转化为系统响应
     *
     * @param httpResponse
     * @param request
     * @return go.timothy.response.Response
     * @author TimothyZz
     * @date 2018/11/2 15:28
     */
    private Response convertResponse(HttpResponse httpResponse, Request request) {
        StatusLine statusLine = httpResponse.getStatusLine();
        if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
            log.info("【{}】响应失败-->响应码：【{}】", request.getUrl(), statusLine.getStatusCode());
            return null;
        }

        HttpEntity entity = httpResponse.getEntity();
        org.apache.http.Header contentType = entity.getContentType();
        ByteArrayInputStream inputStream;
        try (InputStream content = entity.getContent()) {
            inputStream = new ByteArrayInputStream(IOUtils.toByteArray(content));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String charset;
        try {
            charset = contentType.getValue().split(";")[1].trim();
        } catch (RuntimeException e) {
            int length = inputStream.available() < 512 ? inputStream.available() : 512;
            charset = TextDetector.detectCodepage(inputStream, length);
        }

        Header contentTypeHeader = Header.of(contentType.getName(), contentType.getValue());

        return Response.of(request, Entity.of(inputStream, contentTypeHeader, charset));
    }

}
