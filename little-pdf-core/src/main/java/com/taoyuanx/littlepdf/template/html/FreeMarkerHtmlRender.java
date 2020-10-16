package com.taoyuanx.littlepdf.template.html;

import com.taoyuanx.littlepdf.exception.PdfException;
import com.taoyuanx.littlepdf.template.AbstractRender;
import com.taoyuanx.littlepdf.template.IRender;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

/**
 * @author dushitaoyuan
 * @date 2019/9/910:11
 * @desc: freemarker html render
 */
public class FreeMarkerHtmlRender extends AbstractRender implements IRender {
    private Configuration configuration;
    private static final String DEFAULT_SUFFIX = "ftl";

    public FreeMarkerHtmlRender(Configuration configuration) {
        this.suffix = DEFAULT_SUFFIX;
        this.configuration = configuration;
        configuration.setEncoding(Locale.CHINESE, "UTF-8");
    }

    public FreeMarkerHtmlRender(String templateDir) {
        try {
            this.suffix = DEFAULT_SUFFIX;
            configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
            if (templateDir.toLowerCase().startsWith("classpath:")) {
                templateDir = templateDir.toLowerCase().replaceFirst("classpath:", "/");
                configuration.setClassForTemplateLoading(FreeMarkerHtmlRender.class,
                        templateDir);
            } else {
                File file = new File(templateDir);
                if (!file.exists()) {
                    throw new PdfException(templateDir + " ->templateDir 不存在");
                }
                configuration.setTemplateLoader(new FileTemplateLoader(file));
            }
            configuration.setEncoding(Locale.CHINESE, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String render(String templatePath, Map<String, Object> renderData) {
        try {
            Template tp = configuration.getTemplate(templatePath);
            StringWriter stringWriter = new StringWriter();
            tp.process(renderData, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }


}
