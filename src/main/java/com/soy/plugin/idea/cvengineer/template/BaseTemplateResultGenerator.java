package com.soy.plugin.idea.cvengineer.template;

import com.intellij.ide.fileTemplates.impl.UrlUtil;
import com.soy.plugin.idea.cvengineer.intention.DefaultCvIntention;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

/**
 * 基础模板结果生成器
 * @author zhengsy
 * @since 2022-04-20
 */
public abstract class BaseTemplateResultGenerator implements TemplateResultGenerator {

    private static final Configuration configuration;
    private static final StringTemplateLoader stringTemplateLoader;
    static {
        configuration = new Configuration(Configuration.VERSION_2_3_31);
        stringTemplateLoader = new StringTemplateLoader();
        configuration.setTemplateLoader(stringTemplateLoader);
    }

    private static final Logger log = LoggerFactory.getLogger(BaseTemplateResultGenerator.class);

    private final String displayText;
    private final Template template;

    public BaseTemplateResultGenerator(String displayText, String templateName){
        this.displayText = displayText;

        try {
            final String templateString = UrlUtil.loadText(DefaultCvIntention.class.getResource("/template/" + templateName + ".ftl"));
            stringTemplateLoader.putTemplate(templateName, templateString);
            template = configuration.getTemplate(templateName);
        } catch (IOException e) {
            log.error("init template has error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDisplayText() {
        return displayText;
    }

    @Override
    public @Nullable String process() {
        try {
            final StringWriter writer = new StringWriter();
            template.process(this.getDataModel(), writer);
            return writer.toString();
        } catch (TemplateException | IOException e) {
            log.error("process template has error", e);
            return null;
        }
    }

    /**
     * 获取数据模型，用于模板
     * @return 数据模型
     */
    abstract protected Object getDataModel();
}
