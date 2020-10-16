package com.taoyuanx.resources;

import com.alibaba.fastjson.JSON;
import com.deepoove.poi.data.NumbericRenderData;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;
import com.deepoove.poi.data.style.StyleBuilder;
import com.taoyuanx.littlepdf.sign.Itext5PdfSign;
import com.taoyuanx.littlepdf.template.html.FreeMarkerHtmlRender;
import com.taoyuanx.littlepdf.template.html.ThymeleafHtmlRender;
import com.taoyuanx.littlepdf.template.html2pdf.Itext5PdfRenderConfig;
import com.taoyuanx.littlepdf.template.impl.TemplateRender;
import com.taoyuanx.littlepdf.template.word.WordTemplateRender;
import com.taoyuanx.littlepdf.template.word.WordToPdfUtil;
import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    public void templateThymeleafTest() throws IOException {
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

        ThymeleafHtmlRender thymeleafHtmlRender = new ThymeleafHtmlRender(templateEngine);
        thymeleafHtmlRender.setSuffix("html");

        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();

        renderConfig.setFontsDir("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\fonts");

        TemplateRender templateRender = new TemplateRender(renderConfig);
        templateRender.addRender(thymeleafHtmlRender);
        templateRender.render("thymeleaf.html", map, new FileOutputStream("d://11.pdf"));

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
        renderConfig.setFontsDir("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\fonts");
        TemplateRender thymeleafRender = new TemplateRender(renderConfig);
        thymeleafRender.addRender(thymeleafHtmlRender);
        thymeleafRender.render("rent-contract.html", map, new FileOutputStream("d://12.pdf"));

    }

    @Test
    public void wordPdfTest() throws Exception {
        /**
         * word模板测试
         */
        WordTemplateRender wordTemplateRender = new WordTemplateRender("d://temp/");
        wordTemplateRender.setSuffix("docx");
        Itext5PdfRenderConfig renderConfig = new Itext5PdfRenderConfig();
        renderConfig.setFontsDir("G:\\github\\pdf_research\\doc-render\\little-pdf\\src\\main\\resources\\fonts");
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
        map.put("auditOrg", "xxx信息办公室");
        map.put("now_date", "2020年10月12日");
        String wordTemplatePath = "A:\\work\\code\\little-pdf\\little-pdf-springboot-sample\\src\\main\\resources\\pdftemplate\\demo_template.docx";
        thymeleafRender.render(wordTemplatePath, map, new FileOutputStream("d://word.pdf"));
    }
    private Itext5PdfSign itext5PdfSign;
    @Before
    public void before() {

        String signername = "个人出入境管理系统";
        String reason = "出入境管理中心";
        String location = "xxx互联网信息办公室";
        String password = "123456";
        String p12Path =  "xxclient.p12";
        String chapterPath ="xxx_stamp.png";
        String field_name = "xxx_signField";

        Itext5PdfSign.SignConfig signConfig = new Itext5PdfSign.SignConfig();
        signConfig.setSignP12Path(p12Path);
        signConfig.setSignP12Password(password);
        signConfig.setChapterImgPath(chapterPath);
        signConfig.setSignername(signername);
        signConfig.setReason(reason);

        signConfig.setLocation(location);
        signConfig.setSignFiledName(field_name);
        signConfig.setSignKeyWord("信息办公室");
        itext5PdfSign = new Itext5PdfSign(signConfig);


    }

}
