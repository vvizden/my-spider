package go.timothy.config;

/**
 * 引擎配置
 *
 * @author TimothyZz
 * @date 2018/10/26 11:04
 */
public interface EngineConfig {
    /**
     * 线程池核心线程数
     *
     * @param
     * @return int
     * @author TimothyZz
     * @date 2018/10/26 14:48
     */
    int getCoreThreadSize();

    /**
     * 线程池队列大小
     *
     * @param
     * @return int
     * @author TimothyZz
     * @date 2018/10/26 14:49
     */
    int getQueueSize();
}
