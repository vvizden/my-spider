package go.timothy.engine;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author TimothyZz
 * @description 自定义线程工厂
 * @program my-spider
 * @date 2018-10-26 14:58
 **/
public class NamedThreadFactory implements ThreadFactory {
    /**
     * 工厂名称
     */
    private final String name;
    /**
     * 自增器
     */
    private final LongAdder longAdder = new LongAdder();

    public NamedThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        longAdder.increment();
        return new Thread(r, name + "@Thread-" + longAdder);
    }
}
