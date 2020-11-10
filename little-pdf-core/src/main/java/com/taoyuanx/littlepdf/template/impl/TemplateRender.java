package com.taoyuanx.littlepdf.template.impl;

import com.taoyuanx.littlepdf.exception.PdfException;
import com.taoyuanx.littlepdf.template.IRender;
import com.taoyuanx.littlepdf.template.LittlePdfTemplateRender;
import com.taoyuanx.littlepdf.template.html2pdf.Html2PdfUtil;
import com.taoyuanx.littlepdf.template.html2pdf.Itext5PdfRenderConfig;
import com.taoyuanx.littlepdf.template.word.OfficePdfUtil;
import com.taoyuanx.littlepdf.utils.FileUtil;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;

import java.io.FileInputStream;
import java.io.OutputStream;
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
            if (fileSuffix.equals("docx")) {
                OfficePdfUtil.toPdf(new FileInputStream(renderResult), DefaultDocumentFormatRegistry.DOCX, outputStream);
                return;
            } else if (fileSuffix.equals("html")) {
                Html2PdfUtil.html2Pdf(renderResult, outputStream, renderConfig);
                return;

            }
            throw new PdfException(fileSuffix + "不支持");
        } catch (Exception e) {
            if (e instanceof PdfException) {
                throw (PdfException) e;
            }
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
            if (render.accept(fileSuffix)) {
                return render;
            }
        }
        throw new PdfException(fileSuffix + " 不支持");

    }


}
