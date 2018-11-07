package go.timothy.engine;

import go.timothy.config.EngineConfig;
import go.timothy.config.EngineConfigImpl;
import go.timothy.http.HttpClient;
import go.timothy.http.HttpClientImpl;
import go.timothy.pipeline.Pipeline;
import go.timothy.request.Request;
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
    private final List<BaseSpider> baseSpiders = new ArrayList<>(8);
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

    private Engine(EngineConfig config, ThreadPoolExecutor executor, Storager storager, HttpClient httpClient) {
        init(config, executor, storager, httpClient);
    }

    public static Engine of() {
        return new Engine(null, null, null, null);
    }

    public static Engine of(EngineConfig config) {
        return new Engine(config, null, null, null);
    }

    public static Engine of(EngineConfig config, ThreadPoolExecutor executor, Storager storager, HttpClient httpClient) {
        return new Engine(config, executor, storager, httpClient);
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
        log.info("准备组件中");
        readyModules();
        log.info("组件准备完成");

        // 2.循环所有的爬虫，把所有的请求装载进存储器
        log.info("爬虫准备中");
        loadAllRequests();

        // 3.定义一个执行任务去装载请求任务
        executor.execute(() -> {
            while (isRunning) {
                if (!this.storager.hasRequest()) {
                    ThreadUtil.sleepMillisecond(200);
                    continue;
                }

                Request request = this.storager.nextRequest();
                executor.execute(ProductionTask.of(request, this.httpClient, this.storager));

                if (request.getRequestConfig() != null) {
                    ThreadUtil.sleepMillisecond(request.getRequestConfig().getRequestIntervalMillisecond());
                }
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
                    requests.forEach(request -> {
                        if (request != null) {
                            this.storager.putRequest(request);
                        }
                    });
                }
                if (result.getTargetSource() != null) {
                    Pipeline pipeline = result.getPipeline();
                    if (pipeline != null) {
                        pipeline.process(result.getTargetSource(), result.getPreRequest());
                    }
                }
            });
        }
    }

    /**
     * 停止引擎
     *
     * @param
     * @return void
     * @author TimothyZz
     * @date 2018/11/6 16:46
     */
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
                List<Request> requests = e.getRequests();
                if (requests != null && !requests.isEmpty()) {
                    requests.forEach(i -> {
                        if (i != null) {
                            this.storager.putRequest(i);
                        }
                    });
                    log.info("爬虫【{}】启动", e.getName());
                }
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
            this.httpClient = HttpClientImpl.of();
        } else {
            this.httpClient = httpClient;
        }

    }
}
