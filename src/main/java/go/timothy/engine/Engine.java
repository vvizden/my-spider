package go.timothy.engine;

import go.timothy.config.EngineConfig;
import go.timothy.config.EngineConfigImpl;
import go.timothy.config.RequestConfig;
import go.timothy.constant.HttpMethodEnum;
import go.timothy.http.HttpClient;
import go.timothy.http.HttpClientImpl;
import go.timothy.parser.Parser;
import go.timothy.pipeline.Pipeline;
import go.timothy.request.Request;
import go.timothy.response.Response;
import go.timothy.result.Result;
import go.timothy.spider.BaseSpider;
import go.timothy.storager.Storager;
import go.timothy.storager.StoragerImpl;
import go.timothy.util.ThreadUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author TimothyZz
 * @program my-spider
 * @description 引擎
 * @date 2018-09-28 17:16
 **/
@Slf4j
public class Engine {
    /**
     * 引擎状态
     */
    private boolean isRunning = false;

    /**
     * 所有爬虫
     */
    private final List<BaseSpider> baseSpiders = new ArrayList<>();
    /**
     * 引擎配置
     */
    private EngineConfig config;
    /**
     * 任务执行器
     */
    private ThreadPoolExecutor executor;
    /**
     * 存储器
     */
    private Storager storager;
    /**
     * http客户端
     */
    private HttpClient httpClient;

    public Engine(EngineConfig config) {
        init(config, null, null, null);
    }

    public Engine(EngineConfig config, ThreadPoolExecutor executor, Storager storager, HttpClient httpClient) {
        init(config, executor, storager, httpClient);
    }

    /**
     * 注册爬虫
     *
     * @param baseSpider
     * @return go.timothy.engine.Engine
     * @author TimothyZz
     * @date 2018/9/28 17:57
     */
    public Engine registerSpider(@NonNull BaseSpider baseSpider) {
        this.baseSpiders.add(baseSpider);
        log.info("爬虫【{}】开始待命", baseSpider.getName());
        return this;
    }

    /**
     * 启动引擎
     *
     * @param
     * @return go.timothy.engine.Engine
     * @author TimothyZz
     * @date 2018/9/29 14:35
     */
    public void start() {
        if (isRunning) {
            throw new IllegalStateException("引擎已启动");
        }

        this.isRunning = true;
        log.info("引擎启动");

        // 1.准备各组件
        log.info("准备组件中。。。");
        readyModules();
        log.info("组件准备完成。。。");

        // 2.循环所有的爬虫，把所有的请求装载进存储器
        log.info("爬虫准备中。。。");
        loadAllRequests();

        // 3.定义一个执行任务去装载请求任务
        executor.execute(() -> {
            while (isRunning) {
                if (!this.storager.hasRequest()) {
                    ThreadUtil.sleepMillisecond(200);
                    continue;
                }

                Request request = this.storager.nextRequest();
                executor.execute(() -> {
                    BaseSpider spider = request.getSpider();
                    log.info("开始请求【{}】【{}】", spider.getName(), request.getUrl());
                    HttpMethodEnum method = request.getMethod();
                    Response response = null;
                    if (HttpMethodEnum.GET.equals(method)) {
                        response = this.httpClient.get(request);
                    } else if (HttpMethodEnum.POST.equals(method)) {
                        response = this.httpClient.post(request);
                    } else {
                        log.info("【{}】暂不支持此【{}】请求方式", request.getUrl(), method);
                    }
                    if (response != null) {
                        log.info("【{}】请求结束", request.getUrl());
                        Parser<Response, Result> parser = request.getParser();
                        Result result = parser.parse(response);
                        this.storager.putResult(result);
                    }
                });
            }
        });

        // 4.处理解析结果
        while (isRunning) {
            if (!this.storager.hasResult()) {
                ThreadUtil.sleepMillisecond(200);
                continue;
            }

            Result result = this.storager.nextResult();
            executor.execute(() -> {
                List<Request> requests = result.getRequests();
                if (requests != null && !requests.isEmpty()) {
                    requests.forEach(this.storager::putRequest);
                }
                if (result.getItem() != null) {
                    Pipeline pipeline = result.getRequest().getSpider().getPipeline();
                    pipeline.process(result.getItem(), result.getRequest());
                }
            });
        }
    }

    public void stop() {
        this.isRunning = false;

        this.executor.shutdown();
        this.httpClient.close();
        this.storager.clear();
        log.info("引擎停止");
    }

    private void readyModules() {
        httpClient.start();
    }

    private void loadAllRequests() {
        if (this.baseSpiders.size() > 0) {
            this.baseSpiders.forEach(e -> {
                log.info("爬虫【{}】启动", e.getName());
                e.getRequests().forEach(i -> storager.putRequest(i));
            });
        }
    }

    private void init(EngineConfig config, ThreadPoolExecutor executor, Storager storager, HttpClient httpClient) {

        if (Objects.isNull(config)) {
            this.config = new EngineConfigImpl();
        } else {
            this.config = config;
        }

        if (Objects.isNull(executor)) {
            int coreThreadSize = this.config.getCoreThreadSize();
            int queueSize = this.config.getQueueSize();
            this.executor = new ThreadPoolExecutor(coreThreadSize, coreThreadSize, 0, TimeUnit.MILLISECONDS
                    , queueSize == 0
                    ? new SynchronousQueue<>()
                    : queueSize < 0
                    ? new LinkedBlockingQueue<>()
                    : new LinkedBlockingQueue<>(queueSize));
        } else {
            this.executor = executor;
        }

        if (Objects.isNull(storager)) {
            this.storager = new StoragerImpl();
        } else {
            this.storager = storager;
        }

        if (Objects.isNull(httpClient)) {
            this.httpClient = new HttpClientImpl(new RequestConfig(5000, 5000));
        } else {
            this.httpClient = httpClient;
        }

    }
}
