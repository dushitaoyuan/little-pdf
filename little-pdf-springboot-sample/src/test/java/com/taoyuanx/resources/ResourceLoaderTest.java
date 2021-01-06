package com.taoyuanx.resources;

import com.alibaba.fastjson.JSON;
import com.deepoove.poi.data.NumbericRenderData;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;
import com.deepoove.poi.data.style.StyleBuilder;
import com.taoyuanx.littlepdf.template.html.FreeMarkerHtmlRender;
import com.taoyuanx.littlepdf.template.html.ThymeleafHtmlRender;
import com.taoyuanx.littlepdf.template.html2pdf.Itext5PdfRenderConfig;
import com.taoyuanx.littlepdf.template.impl.TemplateRender;
import com.taoyuanx.littlepdf.template.word.WordTemplateRender;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author dushitaoyuan
 * @date 2019/7/210:29
 * @desc: 资源加载测试
 */
public class ResourceLoaderTest {

    private static final String FONT = "fonts/simhei.ttf";


    @Test
    public void templateThymeleafTest() throws IOException {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setOrder(1);
        resolver.setCacheable(true);
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.HTML5);
        resolver.setPrefix("A:\\work\\code\\little-pdf\\little-pdf-springboot-sample\\src\\main\\resources\\pdftemplate\\");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(resolver);
        Map<String, Object> map = new HashMap<>();
        map.put("title", "我是标题");
        map.put("content", "我是内容");
        map.put("list", Arrays.asList("列表1", "列表2", "列表3", "列表4", "列表5", "列表6"));

        ThymeleafHtmlRender thymeleafHtmlRender = new ThymeleafHtmlRender(templateEngine);
        thymeleafHtmlRender.setSuffix("html");

        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();

        renderConfig.setFontsDir("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\fonts");

        TemplateRender templateRender = new TemplateRender(renderConfig);
        templateRender.addRender(thymeleafHtmlRender);
        templateRender.render("demo2.html", map, new FileOutputStream("d://11.pdf"));

    }

    @Test
    public void templateFreemarkerTest() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "我是标题");
        map.put("content", "我是内容");
        map.put("list", Arrays.asList("列表1", "列表2", "列表3", "列表4", "列表5", "列表6"));

        FreeMarkerHtmlRender freeMarkerHtmlRender = new FreeMarkerHtmlRender("classpath:pdftemplate");
        freeMarkerHtmlRender.setSuffix("ftl");
        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();

        renderConfig.setFontsDir("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\fonts");

        TemplateRender templateRender = new TemplateRender(renderConfig);
        templateRender.addRender(freeMarkerHtmlRender);
        templateRender.render("freemarker.ftl", map, new FileOutputStream("d://11.pdf"));

    }

    @Test
    public void templateAllRenderTest() throws IOException {
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
        resolver.setPrefix("A:\\work\\code\\little-pdf\\little-pdf-springboot-sample\\src\\main\\resources\\pdftemplate\\");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(resolver);
        ThymeleafHtmlRender thymeleafHtmlRender = new ThymeleafHtmlRender(templateEngine);
        thymeleafHtmlRender.setSuffix("html");
        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();

        renderConfig.setFontsDir("A:\\work\\code\\little-pdf\\little-pdf-springboot-sample\\src\\main\\resources\\fonts");

        TemplateRender templateRender = new TemplateRender(renderConfig);
        templateRender.addRender(freeMarkerHtmlRender);
        templateRender.addRender(thymeleafHtmlRender);
        templateRender.render("freemarker.ftl", map, new FileOutputStream("d://11.pdf"));

        templateRender.render("thymeleaf.html", map, new FileOutputStream("d://12.pdf"));

    }

    @Test
    public void thymeleafRenderTest() throws IOException {
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
        ThymeleafHtmlRender thymeleafHtmlRender = new ThymeleafHtmlRender(templateEngine);
        thymeleafHtmlRender.setSuffix("html");
        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();
        renderConfig.setFontsDir("A:\\work\\code\\little-pdf\\little-pdf-springboot-sample\\src\\main\\resources\\fonts");
        TemplateRender thymeleafRender = new TemplateRender(renderConfig);
        thymeleafRender.addRender(thymeleafHtmlRender);
        thymeleafRender.render("rent-contract.html", map, new FileOutputStream("d://12.pdf"));

    }

    @Test
    public void wordPdfTest() throws Exception {
        /**
         * word模板测试
         */
        WordTemplateRender wordTemplateRender = new WordTemplateRender("classpath:pdftemplate/", "d://temp/");
        wordTemplateRender.setSuffix("docx");
        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();
        TemplateRender thymeleafRender = new TemplateRender(renderConfig);
        thymeleafRender.addRender(wordTemplateRender);
        Map<String, Object> map = new HashMap<>();
        map.put("reportCode", "xxxx");
        map.put("orgReportCode", "SB20201012-001");
        map.put("submitOrg", "桃源科技有限公司");
        map.put("start_date", "2020年10月12日");
        map.put("end_date", "2020年10月16日");
        NumbericRenderData commonList = new NumbericRenderData(new ArrayList<TextRenderData>() {
            {
                Style style = StyleBuilder.newBuilder().buildFontFamily("仿宋").buildFontSize(16).build();
                add(new TextRenderData("xxxx基本情况为xxxxxxxxxxx111111", style));
                add(new TextRenderData("家里五头牛,10头猪,100只羊,属于大户。", style));
            }
        });
        NumbericRenderData businessList = new NumbericRenderData(new ArrayList<TextRenderData>() {
            {
                Style style = StyleBuilder.newBuilder().buildFontFamily("仿宋").buildFontSize(16).build();
                add(new TextRenderData("xxxx基本情况为xxxxxxxxxxx111111", style));
                add(new TextRenderData("家里五头牛,10头猪,100只羊,属于大户。", style));
            }
        });
        NumbericRenderData orgList = new NumbericRenderData(new ArrayList<TextRenderData>() {
            {
                Style style = StyleBuilder.newBuilder().buildFontFamily("仿宋").buildFontSize(16).build();
                add(new TextRenderData("xxxx基本情况为xxxxxxxxxxx111111", style));
                add(new TextRenderData("家里五头牛,10头猪,100只羊,属于大户。", style));
            }
        });
        NumbericRenderData xxResultList = new NumbericRenderData(new ArrayList<TextRenderData>() {
            {
                Style style = StyleBuilder.newBuilder().buildFontFamily("仿宋").buildFontSize(16).build();
                add(new TextRenderData("xxxx基本情况为xxxxxxxxxxx111111", style));
                add(new TextRenderData("家里五头牛,10头猪,100只羊,属于大户。", style));
            }
        });
        map.put("commonList", commonList);
        map.put("businessList", businessList);
        map.put("orgList", orgList);
        map.put("xxResultList", xxResultList);
        map.put("auditResult", "通过");
        map.put("agent", "桃源");
        map.put("agentPhone", "13717886659");
        map.put("auditOrg", "桃源科技有限公司");
        map.put("now_date", "2020年10月12日");
        String wordTemplatePath = "demo_template.docx";
        thymeleafRender.render(wordTemplatePath, map, new FileOutputStream("d://temp/word.pdf"));

    }

    @Test
    public void htmlPdfTest() throws Exception {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setOrder(1);
        resolver.setCacheable(true);
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("pdftemplate/");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(resolver);
        ThymeleafHtmlRender thymeleafHtmlRender = new ThymeleafHtmlRender(templateEngine);
        thymeleafHtmlRender.setSuffix("html");
        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();
        renderConfig.setHtml2PdfType(Itext5PdfRenderConfig.HTML2PDF_TYPE_JOD_CONVERTER);

        renderConfig.setFontsDir("A:\\work\\code\\little-pdf\\little-pdf-springboot-sample\\src\\main\\resources\\fonts");

        TemplateRender thymeleafRender = new TemplateRender(renderConfig);
        thymeleafRender.addRender(thymeleafHtmlRender);
        /**
         * html模板测试
         */
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("reportCode", "SB20201012-001");
        dataModel.put("companyName", "桃源科技有限公司");
        dataModel.put("submit_date", "2020年10月12日");
        List<String> reasonList = new ArrayList<String>() {
            {
                add("百度百科是百度公司推出的一部内容开放、自由的网络百科全书。其测试版于 2006\n" +
                        "年4 月20 日上线，正式版在 2008 年 4月 21日发布，截至 2020年10月，百度百科已\n" +
                        "经收录了超2100万个词条，参与词条编辑的网友超过717 万人，几乎涵盖了所有已知\n" +
                        "的知识领域");
                add("百度百科是百度公司推出的一部内容开放、自由的网络百科全书。其测试版于 2006\n" +
                        "年4 月20 日上线，正式版在 2008 年 4月 21日发布，截至 2020年10月，百度百科已\n" +
                        "经收录了超2100万个词条，参与词条编辑的网友超过717 万人，几乎涵盖了所有已知\n" +
                        "的知识领域");
                add("百度百科是百度公司推出的一部内容开放、自由的网络百科全书。其测试版于 2006\n" +
                        "年4 月20 日上线，正式版在 2008 年 4月 21日发布，截至 2020年10月，百度百科已\n" +
                        "经收录了超2100万个词条，参与词条编辑的网友超过717 万人，几乎涵盖了所有已知\n" +
                        "的知识领域");
            }
        };

        dataModel.put("reasonList", reasonList);
        dataModel.put("agent", "桃源");
        dataModel.put("agentPhone", "13717886659");
        dataModel.put("auditOrg", "桃源科技有限公司");
        dataModel.put("now_date", "2020年10月12日");
        thymeleafRender.render("html_pdf.html", dataModel, new FileOutputStream("d://temp/word.pdf"));

    }



}
