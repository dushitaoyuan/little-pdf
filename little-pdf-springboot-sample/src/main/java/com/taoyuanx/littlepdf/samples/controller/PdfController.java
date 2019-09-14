package com.taoyuanx.littlepdf.samples.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taoyuanx.littlepdf.template.html2pdf.LittlePdfTemplateRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;

/**
 * @author dushitaoyuan
 * @date 2019/9/1416:01
 */
@RequestMapping(value = "pdf")
@Controller
public class PdfController {
    @Autowired
    LittlePdfTemplateRender littlePdfTemplateRender;
    @Value("${pdfSaveDir}")
    String pdfSaveDir;
    @RequestMapping(value = "render",method = RequestMethod.POST)
    @ResponseBody
    public  String view(@RequestParam(name = "template") String template,
                      @RequestParam(name = "renderData") String renderData,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonObject = JSON.parseObject(renderData);
        String pdf=UUID.randomUUID().toString().replace("-","")+".pdf";
        File file=new File(pdfSaveDir,pdf);
        file.getParentFile().mkdirs();
        littlePdfTemplateRender.render(template,jsonObject,new FileOutputStream(file));
        return  pdf;
    }


    @RequestMapping(value = "view",method = RequestMethod.GET)
    public  void view(@RequestParam(name = "pdf") String pdf,
                        HttpServletRequest request, HttpServletResponse response) throws IOException {
        File file=new File(pdfSaveDir, pdf);
        response.setContentType(request.getServletContext().getMimeType(file.getName()));
        FileChannel channel = new FileInputStream(file).getChannel();
        ServletOutputStream out = response.getOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(4<<20);
        int len = 0;
        while ((len = channel.read(buffer)) > 0) {
            buffer.flip();
            out.write(buffer.array(), 0, len);
            buffer.clear();
        }
        channel.close();
    }
}
