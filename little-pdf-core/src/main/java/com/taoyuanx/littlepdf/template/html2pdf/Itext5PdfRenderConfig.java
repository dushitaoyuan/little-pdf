package com.taoyuanx.littlepdf.template.html2pdf;

import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.net.FileRetrieve;
import com.itextpdf.tool.xml.net.FileRetrieveImpl;
import com.itextpdf.tool.xml.net.ReadingProcessor;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.pipeline.html.ImageProvider;
import com.taoyuanx.littlepdf.utils.LittlePdfUtil;
import com.taoyuanx.littlepdf.utils.TemplateLoaderUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author dushitaoyuan
 * @date 2019/9/922:49
 * @desc: pdf渲染配置
 */
@Data
public class Itext5PdfRenderConfig {
    /**
     * fontsDir 字体所在目录,必须是文件系统路径
     * defaultFont 默认字体
     * resourcesDir 资源路径(css,img)
     * charset 字符编码
     */
    private String fontsDir;
    private String defaultFont;
    private String resourcesDir;
    private String charset="UTF-8";

    private static final Logger LOG = LoggerFactory.getLogger(Itext5PdfRenderConfig.class);

    /* public HtmlPipelineContext build() {

     }*/
    public  XMLWorkerFontProvider getFontProvider(){
        XMLWorkerFontProvider xmlWorkerFontProvider = new XMLWorkerFontProvider() {
            @Override
            public Font getFont(String fontname, String encoding,
                                float size, final int style) {
                if (fontname == null) {
                    fontname = defaultFont;
                }
                return super.getFont(fontname, encoding, size, style);
            }
        };
        xmlWorkerFontProvider.registerDirectory(fontsDir);
        return xmlWorkerFontProvider;
    }
    public  ImageProvider getImageProvider(){
        ImageProvider imageProvider = new AbstractImageProvider() {
            @Override
            public String getImageRootPath() {
                return resourcesDir;
            }

            @Override
            public Image retrieve(String src) {
                if (StringUtils.isEmpty(src)) {
                    return null;
                }
                try {
                    InputStream load = TemplateLoaderUtil.load(resourcesDir, src);
                    byte[] bytes = LittlePdfUtil.streamToBytes(load);
                    Image image = Image.getInstance(bytes);
                    if (image != null) {
                        store(src, image);
                        return image;
                    }
                    return super.retrieve(src);
                } catch (Exception e) {
                    LOG.error("retrieve image  failed, src:{}", src);
                    return null;
                }
            }
        };
        return  imageProvider;
    }
    public  FileRetrieve getFileRetrieve(){
        FileRetrieve cssFileRetrieve = null;

        FileRetrieveImpl fileRetrieve = new FileRetrieveImpl() {
            @Override
            public void processFromHref(String href, ReadingProcessor processor) throws IOException {
                try {
                    InputStream cssInputStream = TemplateLoaderUtil.load(resourcesDir, href);
                    InputStreamReader reader = new InputStreamReader(cssInputStream, charset);
                    int i = -1;
                    while ((i = reader.read()) != -1) {
                        processor.process(i);
                    }
                    cssInputStream.close();
                } catch (Throwable e) {
                    LOG.error("retrieve csss  failed, href:{}", href);
                }
            }
        };
        if (null != resourcesDir) {
            fileRetrieve.addRootDir(new File(resourcesDir));
        }
        cssFileRetrieve = fileRetrieve;
        return  cssFileRetrieve;
    }
}
