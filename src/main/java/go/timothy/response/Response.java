package go.timothy.response;

import go.timothy.request.Request;
import lombok.Data;

/**
 * @author TimothyZz
 * @program my-spider
 * @description 响应
 * @date 2018-09-30 10:12
 **/
@Data(staticConstructor = "of")
public class Response {
    /**
     * 请求
     */
    private final Request request;
    /**
     * 响应体
     */
    private final Entity entity;

}
