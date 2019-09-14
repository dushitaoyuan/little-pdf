package com.taoyuanx.littlepdf.template.html2pdf.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.taoyuanx.littlepdf.exception.PdfException;
import com.taoyuanx.littlepdf.template.html.IHtmlRender;
import com.taoyuanx.littlepdf.template.html2pdf.Itext5PdfRenderConfig;
import com.taoyuanx.littlepdf.template.html2pdf.LittlePdfTemplateRender;
import com.taoyuanx.littlepdf.utils.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author dushitaoyuan
 * @date 2019/7/210:15
 * @desc: Thymeleaf 模板渲染
 */
public class ThymeleafRender implements LittlePdfTemplateRender {

    private List<IHtmlRender> renders = new ArrayList<>();

    private Itext5PdfRenderConfig renderConfig;

    public ThymeleafRender(Itext5PdfRenderConfig renderConfig) {
        this.renderConfig = renderConfig;
    }

    public void addRender(IHtmlRender iHtmlRender) {
        renders.add(iHtmlRender);
    }

    public void addRender(int order, IHtmlRender iHtmlRender) {
        renders.add(order, iHtmlRender);
    }

    @Override
    public void render(String template, Map<String, Object> renderData, OutputStream outputStream) {
        try {
            IHtmlRender iHtmlRender = choseHtmlRender(template);
            String html = iHtmlRender.render(template, renderData);
            doGenerate(html, outputStream);
        } catch (Exception e) {
            throw new PdfException("渲染异常", e);
        }
    }

    private IHtmlRender choseHtmlRender(String template) {
        if (renders == null || renders.isEmpty()) {
            throw new PdfException("IHtmlRender 未指定");
        }
        String fileSuffix = FileUtil.getFileSuffix(template).toLowerCase();
        Iterator<IHtmlRender> iterator = renders.iterator();
        IHtmlRender render = null;
        while (iterator.hasNext()) {
            render = iterator.next();
            if (render.accpect(fileSuffix)) {
                return render;
            }
        }
        throw new PdfException(fileSuffix + " 不支持");

    }


    private void doGenerate(String html, OutputStream out) throws Exception {
        Charset charset = renderConfig.getCharset() == null ? Charset.defaultCharset() : Charset.forName(renderConfig.getCharset());
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();
        // 字体处理
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(new CssAppliersImpl(renderConfig.getFontProvider()));
        // img图片加载
        htmlContext.setImageProvider(renderConfig.getImageProvider());
        htmlContext.setAcceptUnknown(true).autoBookmark(true).setTagFactory(Tags.getHtmlTagProcessorFactory());
        // css加载
        CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);
        cssResolver.setFileRetrieve(renderConfig.getFileRetrieve());

        HtmlPipeline htmlPipeline = new HtmlPipeline(htmlContext, new PdfWriterPipeline(document, writer));
        Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, htmlPipeline);
        XMLWorker worker = new XMLWorker(pipeline, true);
        XMLParser parser = new XMLParser(true, worker, charset);
        parser.parse(new ByteArrayInputStream(html.getBytes()), charset);
        document.close();
    }
}
