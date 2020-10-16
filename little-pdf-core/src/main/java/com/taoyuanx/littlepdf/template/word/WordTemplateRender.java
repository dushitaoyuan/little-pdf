package com.taoyuanx.littlepdf.template.word;

import com.deepoove.poi.XWPFTemplate;
import com.taoyuanx.littlepdf.template.AbstractRender;
import com.taoyuanx.littlepdf.template.IRender;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.UUID;

/**
 * @author dushitaoyuan
 * @desc word模板渲染
 * @date 2020/10/15
 */
public class WordTemplateRender extends AbstractRender implements IRender {

    private String wordTempPath;

    public WordTemplateRender(String wordTempPath) {
        this.wordTempPath = wordTempPath;
    }

    @Override
    public String render(String templatePath, Map<String, Object> renderData) {
        try {
            XWPFTemplate template = XWPFTemplate.compile(templatePath).render(renderData);
            File file = new File(wordTempPath, UUID.randomUUID() + ".docx");
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(file);
            template.write(out);
            out.flush();
            out.close();
            template.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
