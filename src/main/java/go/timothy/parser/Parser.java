package go.timothy.parser;

/**
 * 数据解析器
 *
 * @author TimothyZz
 * @date 2018/9/30 10:05
 */
@FunctionalInterface
public interface Parser<T, R> {
    /**
     * 解析数据
     *
     * @param t
     * @return R
     * @author TimothyZz
     * @date 2018/9/30 10:06
     */
    R parse(T t);
}
