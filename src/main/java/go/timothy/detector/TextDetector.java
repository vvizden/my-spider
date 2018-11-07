package go.timothy.detector;

import info.monitorenter.cpdetector.io.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author TimothyZz
 * @description 文本编码检测器
 * @program my-spider
 * @date 2018-11-02 14:43
 **/
public class TextDetector {
    private final static CodepageDetectorProxy cdp;

    static {
        cdp = CodepageDetectorProxy.getInstance();
        cdp.add(new ParsingDetector(false));
        cdp.add(UnicodeDetector.getInstance());
        cdp.add(new ByteOrderMarkDetector());
        // 依赖jar包 ：antlr.jar & chardet.jar
        cdp.add(JChardetFacade.getInstance());
        cdp.add(ASCIIDetector.getInstance());
    }

    /**
     * 检测文本编码
     *
     * @param in
     * @param length
     * @return java.lang.String
     * @author TimothyZz
     * @date 2018/11/2 14:34
     */
    public static String detectCodepage(ByteArrayInputStream in, int length) {
        try {
            Charset charset = cdp.detectCodepage(in, length);
            return charset.name();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
