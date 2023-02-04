package io.github.msyysoft.java.utiltools;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class FreemarkerUtil {

    /**
     * 处理生成模版信息...
     *
     * @param templatePathName 模版路径名称
     * @param model            数据模型
     * @param outTemplate      输出的模型路径文件...
     * @return
     */
    public static boolean process(String templatePathName, Map<String, Object> model, File outTemplate) throws Exception {
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File(FreemarkerUtil.class.getResource("/").getPath()));
        //cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setDefaultEncoding("utf-8");
        Template template = cfg.getTemplate(templatePathName);
        template.setEncoding("utf-8");
        template.process(model, new FileWriter(outTemplate));
        return true;
    }
}
