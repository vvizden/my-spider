package go.timothy.response;

import go.timothy.http.Header;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

    private Entity(InputStream inputStream, Header contentType, String charset) {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.charset = charset;
    }

    private Entity(Header contentType, String charset, String document) {
        this.contentType = contentType;
        this.charset = charset;
        this.document = document;
    }

    public static Entity of(InputStream inputStream, Header contentType, String charset) {
        return new Entity(inputStream, contentType, charset);
    }

    public static Entity of(Header contentType, String charset, String document) {
        return new Entity(contentType, charset, document);
    }

    @Override
    public String toString() {
        if (document != null) {
            return document;
        }
        try (BufferedReader bfr = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            StringBuilder sb = bfr.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
            this.document = sb.toString();
            return this.document;
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

    /**
     * 保存为文件
     *
     * @param uri
     * @return void
     * @author TimothyZz
     * @date 2018/11/7 10:45
     */
    public void saveFile(String uri) {
        try {
            int available = this.inputStream.available();
            if (available > 0) {
                Path path = Paths.get(uri);
                Path parent = path.getParent();
                if (Files.notExists(parent)) {
                    Files.createDirectories(parent);
                }
                if (Files.notExists(path)) {
                    Files.createFile(path);
                }
                Files.copy(this.inputStream, Paths.get(uri), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(this.inputStream);
        }
    }
}
