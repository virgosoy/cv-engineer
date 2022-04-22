package com.soy.plugin.idea.cvengineer.template;

import org.apache.groovy.util.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * 基础 TS 模板结果生成器
 * @author zhengsy
 */
public abstract class BaseTsTemplateResultGenerator extends BaseTemplateResultGenerator {
    public BaseTsTemplateResultGenerator(String displayText, String templateName) {
        super(displayText, templateName);
    }

    final Map<String, String> typeMap = Maps.of(
            "java.lang.Byte", "boolean",
            "java.lang.Short", "number",
            "java.lang.Integer", "number",
            "java.lang.Long", "string",
            "java.lang.Float", "string",
            "java.lang.Double", "string",
            "java.lang.Boolean", "boolean",
            "java.lang.Char", "string",
            "java.lang.String", "string",
            "java.time.LocalDate", "string",
            "java.time.LocalDateTime", "string",
            "java.math.BigDecimal", "string"
    );

    protected String convertType(@NotNull String javaType){
        return Optional.ofNullable(typeMap.get(javaType))
                .orElseGet(() -> javaType.substring(javaType.lastIndexOf(".") + 1));
    }

}
