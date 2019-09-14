package com.taoyuanx.resources;

import com.alibaba.fastjson.JSON;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.taoyuanx.littlepdf.template.html.IHtmlRender;
import com.taoyuanx.littlepdf.template.html.impl.FreeMarkerHtmlRender;
import com.taoyuanx.littlepdf.template.html.impl.TemplateHtmlRender;
import com.taoyuanx.littlepdf.template.html2pdf.Itext5PdfRenderConfig;
import com.taoyuanx.littlepdf.template.html2pdf.impl.ThymeleafRender;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dushitaoyuan
 * @date 2019/7/210:29
 * @desc: 资源加载测试
 */
public class ResourceLoaderTest {

    private static final String FONT = "fonts/simhei.ttf";


    @Test
    public void templateThymeleaf() throws IOException {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setOrder(1);
        resolver.setCacheable(true);
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.HTML5);
        resolver.setPrefix("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\pdftemplate\\");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(resolver);
        Map<String, Object> map = new HashMap<>();
        map.put("title", "我是标题");
        map.put("content", "我是内容");
        map.put("list", Arrays.asList("列表1", "列表2", "列表3", "列表4", "列表5", "列表6"));

        TemplateHtmlRender templateHtmlRender = new TemplateHtmlRender(templateEngine);
        templateHtmlRender.setSuffix("html");

        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();

        renderConfig.setFontsDir("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\fonts");

        ThymeleafRender thymeleafRender = new ThymeleafRender(renderConfig);
        thymeleafRender.addRender((IHtmlRender)templateHtmlRender);
        thymeleafRender.render("thymeleaf.html", map, new FileOutputStream("d://11.pdf"));

    }

    @Test
    public void templateFreemarker() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "我是标题");
        map.put("content", "我是内容");
        map.put("list", Arrays.asList("列表1", "列表2", "列表3", "列表4", "列表5", "列表6"));

        FreeMarkerHtmlRender  freeMarkerHtmlRender = new FreeMarkerHtmlRender("classpath:pdftemplate");
        freeMarkerHtmlRender.setSuffix("ftl");
        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();

        renderConfig.setFontsDir("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\fonts");

        ThymeleafRender thymeleafRender = new ThymeleafRender(renderConfig);
        thymeleafRender.addRender((IHtmlRender)freeMarkerHtmlRender);
        thymeleafRender.render("freemarker.ftl", map, new FileOutputStream("d://11.pdf"));

    }

    @Test
    public void templateAllRender() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "我是标题");
        map.put("content", "我是内容");
        map.put("list", Arrays.asList("列表1", "列表2", "列表3", "列表4", "列表5", "列表6"));
        FreeMarkerHtmlRender freeMarkerHtmlRender = new FreeMarkerHtmlRender("classpath:pdftemplate");
        freeMarkerHtmlRender.setSuffix("ftl");

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setOrder(1);
        resolver.setCacheable(true);
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.HTML5);
        resolver.setPrefix("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\pdftemplate\\");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(resolver);
        TemplateHtmlRender templateHtmlRender = new TemplateHtmlRender(templateEngine);
        templateHtmlRender.setSuffix("html");
        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();

        renderConfig.setFontsDir("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\fonts");

        ThymeleafRender thymeleafRender = new ThymeleafRender(renderConfig);
        thymeleafRender.addRender(freeMarkerHtmlRender);
        thymeleafRender.addRender(templateHtmlRender);
        thymeleafRender.render("freemarker.ftl", map, new FileOutputStream("d://11.pdf"));

        thymeleafRender.render("thymeleaf.html", map, new FileOutputStream("d://12.pdf"));

    }

    @Test
    public void templateRender() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("contractNumber", "合同编号0001");
        map.put("personFirst", "都市桃源");
        map.put("personFirstPhone", "17819999481");
        map.put("personLegal", "法人001");
        map.put("personFirstAddress", "北京昌平");
        map.put("personFirstTenantry", "承租方001");
        map.put("personFirstTenantryPhone", "1111111111");
        map.put("personFirstTenantryLegal", "承租方负责人");
        map.put("personFirstTenantryAddress", "北京昌平001");
        map.put("houseAddress", "北京昌平002");
        map.put("houseNumber", "xx公寓001");
        map.put("monthCount", "12");
        map.put("yearFrom", "2019");
        map.put("monthFrom", "1");
        map.put("dayFrom", "25");

        map.put("yearTo", "2022");
        map.put("monthTo", "1");
        map.put("dayTo", "25");
        System.out.println(JSON.toJSONString(map));

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
        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();
        renderConfig.setFontsDir("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\fonts");
        ThymeleafRender thymeleafRender = new ThymeleafRender(renderConfig);
        thymeleafRender.addRender(templateHtmlRender);
        thymeleafRender.render("rent-contract.html", map, new FileOutputStream("d://12.pdf"));

    }


}
