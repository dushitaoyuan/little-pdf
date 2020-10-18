package com.taoyuanx.littlepdf.template.impl;

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
import com.taoyuanx.littlepdf.template.IRender;
import com.taoyuanx.littlepdf.template.LittlePdfTemplateRender;
import com.taoyuanx.littlepdf.template.html2pdf.Html2PdfUtil;
import com.taoyuanx.littlepdf.template.html2pdf.Itext5PdfRenderConfig;
import com.taoyuanx.littlepdf.template.word.WordToPdfUtil;
import com.taoyuanx.littlepdf.utils.FileUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author dushitaoyuan
 * @date 2019/7/210:15
 */
public class TemplateRender implements LittlePdfTemplateRender {

    private List<IRender> renders = new ArrayList<>();

    private Itext5PdfRenderConfig renderConfig;

    public TemplateRender(Itext5PdfRenderConfig renderConfig) {
        this.renderConfig = renderConfig;
    }

    public void addRender(IRender render) {
        renders.add(render);
    }

    public void addRender(int order, IRender render) {
        renders.add(order, render);
    }

    @Override
    public void render(String template, Map<String, Object> renderData, OutputStream outputStream) {
        String tempFileDelete = null;
        try {
            String fileSuffix = FileUtil.getFileSuffix(template).toLowerCase();
            IRender render = choseRender(fileSuffix);
            String renderResult = render.render(template, renderData);
            tempFileDelete = renderResult;
            if (fileSuffix.equalsIgnoreCase("docx")) {
                WordToPdfUtil.word2Pdf( new FileInputStream(renderResult), outputStream);
            } else {
                Html2PdfUtil.html2Pdf(renderResult, outputStream, renderConfig);
            }
        } catch (Exception e) {
            throw new PdfException("渲染异常", e);
        } finally {
           FileUtil.deleteQuietly(tempFileDelete);
        }
    }

    private IRender choseRender(String fileSuffix) {
        if (renders == null || renders.isEmpty()) {
            throw new PdfException("render 未指定");
        }

        Iterator<IRender> iterator = renders.iterator();
        IRender render = null;
        while (iterator.hasNext()) {
            render = iterator.next();
            if (render.accpect(fileSuffix)) {
                return render;
            }
        }
        throw new PdfException(fileSuffix + " 不支持");

    }


}
