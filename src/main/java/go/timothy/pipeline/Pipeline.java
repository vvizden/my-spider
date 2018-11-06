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
     * @param request
     * @return R
     * @author TimothyZz
     * @date 2018/11/6 17:58
     */
    R process(T t, Request request);
    
}
