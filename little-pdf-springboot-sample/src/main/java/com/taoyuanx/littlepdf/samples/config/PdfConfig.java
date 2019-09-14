package com.taoyuanx.littlepdf.samples.config;

import com.taoyuanx.littlepdf.template.html.impl.TemplateHtmlRender;
import com.taoyuanx.littlepdf.template.html2pdf.Itext5PdfRenderConfig;
import com.taoyuanx.littlepdf.template.html2pdf.LittlePdfTemplateRender;
import com.taoyuanx.littlepdf.template.html2pdf.impl.ThymeleafRender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    public Itext5PdfRenderConfig  littlePdfConfig(){
        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();
        return renderConfig;
    }
    @Bean
    @ConditionalOnBean(value = {Itext5PdfRenderConfig.class})
    public LittlePdfTemplateRender  littlePdfTemplateRender(){
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setOrder(1);
        resolver.setCacheable(true);
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.HTML5);
        resolver.setPrefix("pdftemplate/");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(resolver);
        TemplateHtmlRender templateHtmlRender = new TemplateHtmlRender(templateEngine);
        templateHtmlRender.setSuffix("html");
        Itext5PdfRenderConfig renderConfig = littlePdfConfig();
        ThymeleafRender thymeleafRender = new ThymeleafRender(renderConfig);
        thymeleafRender.addRender(templateHtmlRender);
        return  thymeleafRender;
    }
}
