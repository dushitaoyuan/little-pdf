package com.taoyuanx.littlepdf.samples.config;

import com.taoyuanx.littlepdf.template.html.ThymeleafHtmlRender;
import com.taoyuanx.littlepdf.template.html2pdf.Itext5PdfRenderConfig;
import com.taoyuanx.littlepdf.template.LittlePdfTemplateRender;
import com.taoyuanx.littlepdf.template.impl.TemplateRender;
import com.taoyuanx.littlepdf.template.word.WordTemplateRender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * @author dushitaoyuan
 * @date 2019/9/1413:55
 * @desc: pdf配置
 */
@Configuration

public class PdfConfig {
    @ConfigurationProperties(prefix = "little.pdf")
    @Bean
    public Itext5PdfRenderConfig littlePdfConfig() {
        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();
        return renderConfig;
    }

    @Bean
    @ConditionalOnBean(value = {Itext5PdfRenderConfig.class})
    public LittlePdfTemplateRender littlePdfTemplateRender() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setOrder(1);
        resolver.setCacheable(true);
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.HTML5);
        resolver.setPrefix("pdftemplate/");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(resolver);
        WordTemplateRender wordTemplateRender = new WordTemplateRender("d://temp/");
        wordTemplateRender.setSuffix("docx");
        ThymeleafHtmlRender thymeleafHtmlRender = new ThymeleafHtmlRender(templateEngine);
        thymeleafHtmlRender.setSuffix("html");
        Itext5PdfRenderConfig renderConfig = littlePdfConfig();
        TemplateRender thymeleafRender = new TemplateRender(renderConfig);
        thymeleafRender.addRender(thymeleafHtmlRender);
        thymeleafRender.addRender(wordTemplateRender);
        return thymeleafRender;
    }
}
