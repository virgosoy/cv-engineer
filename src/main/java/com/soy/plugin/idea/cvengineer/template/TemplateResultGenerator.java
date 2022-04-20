package com.soy.plugin.idea.cvengineer.template;

import org.jetbrains.annotations.Nullable;

/**
 * 模板结果生成器
 * @author zhengsy
 * @since 2022-04-20
 */
public interface TemplateResultGenerator {

    /**
     * 获取显示的名称
     * @return
     */
    String getDisplayText();

    /**
     * 处理模板生成结果
     * @return 结果
     */
    @Nullable
    String process();
}
