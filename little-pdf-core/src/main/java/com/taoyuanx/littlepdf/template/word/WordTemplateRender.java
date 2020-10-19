package com.taoyuanx.littlepdf.template.word;

import com.deepoove.poi.XWPFTemplate;
import com.taoyuanx.littlepdf.template.AbstractRender;
import com.taoyuanx.littlepdf.template.IRender;
import com.taoyuanx.littlepdf.utils.FileUtil;
import com.taoyuanx.littlepdf.utils.LittlePdfUtil;
import org.omg.CORBA.OBJ_ADAPTER;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author dushitaoyuan
 * @desc word模板渲染
 * @date 2020/10/15
 */
public class WordTemplateRender extends AbstractRender implements IRender {

    private String wordTempPath;
    private String wordTemplateDir;
    private boolean isClassPath;

    public WordTemplateRender(String wordTemplateDir, String wordTempPath) {
        this.wordTempPath = wordTempPath;
        this.wordTemplateDir = wordTemplateDir;
        isClassPath = wordTemplateDir.startsWith("classpath:");
        if (isClassPath) {
            this.wordTemplateDir = this.wordTemplateDir.replaceFirst("classpath:", "");
        }
    }

    @Override
    public String render(String templatePath, Map<String, Object> renderData) {
        OutputStream out = null;
        InputStream input = null;
        try {
            if (isClassPath) {
                input = WordTemplateRender.class.getClassLoader().getResourceAsStream(wordTemplateDir + templatePath);
            } else {
                File file = new File(templatePath);
                if (!file.exists()) {
                    file = new File(wordTemplateDir, templatePath);
                }
                input = new FileInputStream(file);
            }
            XWPFTemplate template = XWPFTemplate.compile(input).render(renderData);
            File wordRenderFile = new File(wordTempPath, UUID.randomUUID() + ".docx");
            if (!wordRenderFile.getParentFile().exists()) {
                wordRenderFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(wordRenderFile);
            template.write(out);
            out.flush();
            out.close();
            template.close();
            return wordRenderFile.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            FileUtil.close(out);
            FileUtil.close(input);
        }
    }
}
