package go.timothy.response;

import go.timothy.http.Header;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author TimothyZz
 * @description 响应体
 * @program my-spider
 * @date 2018-10-23 17:02
 **/
@Data
public class Entity {
    /**
     * 输入流
     */
    private InputStream inputStream;
    /**
     * 响应类型
     */
    private final Header contentType;
    /**
     * 文本编码
     */
    private final String charset;
    /**
     * html
     */
    private String document;

    public Entity(InputStream inputStream, Header contentType, String charset) {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.charset = charset;
    }

    public Entity(Header contentType, String charset, String document) {
        this.contentType = contentType;
        this.charset = charset;
        this.document = document;
    }

    @Override
    public String toString() {
        if (StringUtils.isNotBlank(document)) {
            return document;
        }
        try (BufferedReader bfr = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            StringBuilder sb = bfr.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * css选择器
     *
     * @param css
     * @return org.jsoup.select.Elements
     * @author TimothyZz
     * @date 2018/11/2 15:14
     */
    public Elements css(String css) {
        return Jsoup.parse(this.toString()).select(css);
    }

}
