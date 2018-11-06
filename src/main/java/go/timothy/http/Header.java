package go.timothy.http;

import lombok.Data;

/**
 * @author TimothyZz
 * @description 请求/响应头
 * @program my-spider
 * @date 2018-10-19 16:07
 **/
@Data(staticConstructor = "of")
public class Header {
    /**
     * 名称
     */
    private final String name;
    /**
     * 值
     */
    private final String value;
}
