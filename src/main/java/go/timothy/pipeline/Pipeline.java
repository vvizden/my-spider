package go.timothy.pipeline;

import go.timothy.request.Request;

/**
 * 数据加工管道
 *
 * @author TimothyZz
 * @date 2018/9/30 10:03
 */
@FunctionalInterface
public interface Pipeline<T, R> {
    /**
     * 加工数据
     *
     * @param t
     * @return R
     * @author TimothyZz
     * @date 2018/9/30 10:04
     */
    R process(T t, Request request);
}
