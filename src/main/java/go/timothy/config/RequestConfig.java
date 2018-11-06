package go.timothy.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author TimothyZz
 * @description 请求配置
 * @program my-spider
 * @date 2018-10-19 15:09
 **/
@Data
@AllArgsConstructor
public class RequestConfig {
    /**
     * 连接建立超时
     */
    private int connectTimeout;
    /**
     * 数据包间隔超时
     */
    private int socketTimeout;
}
