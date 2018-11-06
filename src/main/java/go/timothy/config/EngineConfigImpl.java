package go.timothy.config;

/**
 * @author TimothyZz
 * @description
 * @program my-spider
 * @date 2018-10-26 14:43
 **/
public class EngineConfigImpl implements EngineConfig {

    /**
     * 线程池核心线程数
     *
     * @return int
     * @author TimothyZz
     * @date 2018/10/26 14:48
     */
    @Override
    public int getCoreThreadSize() {
        return Runtime.getRuntime().availableProcessors() * 2;
    }

    /**
     * 线程池队列大小
     *
     * @return int
     * @author TimothyZz
     * @date 2018/10/26 14:49
     */
    @Override
    public int getQueueSize() {
        return -1;
    }

}
