package com.soy.plugin.idea.cvengineer.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.TextTransferable;
import com.soy.plugin.idea.cvengineer.template.BaseTemplateResultGenerator;
import com.soy.plugin.idea.cvengineer.template.BaseTsTemplateResultGenerator;
import com.soy.plugin.idea.cvengineer.template.TemplateResultGenerator;
import com.soy.plugin.idea.cvengineer.util.PsiJavaUtils;
import org.apache.groovy.util.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CV 工程师 的 意图
 * @author zhengsy
 * @since 2022-04-19
 */
public class DefaultCvIntention extends PsiElementBaseIntentionAction implements IntentionAction {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCvIntention.class);

    @Override
    public @IntentionName @NotNull String getText() {
        return "使用 CV 工程师";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        logger.debug("使用 CV 工程师");

        final PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class, false);
        if(clazz == null){
            return;
        }
        String className = clazz.getName();

        PsiField[] fields = clazz.getAllFields();
        List<Map<String, String>> fieldList = Arrays.stream(fields).map(f -> {
            Map<String, String> result = new HashMap<>(2);
            result.put("name", f.getName());
            result.put("type", PsiJavaUtils.getTypeQualifiedNameByField(f));
            return result;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>(2);
        data.put("class", className);
        data.put("fields", fieldList);

        final BaseTemplateResultGenerator tsType = new BaseTsTemplateResultGenerator("TS声明", "tsType") {

            @Override
            protected Object getDataModel() {
                ((List<Map<String, String>>) data.get("fields")).forEach(m -> m.put("tsType", convertType(m.get("type"))));
                return data;
            }
        };

        final BaseTemplateResultGenerator allFieldName = new BaseTemplateResultGenerator("所有字段名称", "allFieldName") {
            @Override
            protected Object getDataModel() {
                return data;
            }
        };

        final TemplateResultGenerator[] templateResultGenerators = {tsType, allFieldName};

        ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(
                new BaseListPopupStep<TemplateResultGenerator>("您想 CV 什么？", templateResultGenerators) {
                    @Override
                    public @NotNull String getTextFor(TemplateResultGenerator value) {
                        return value.getDisplayText();
                    }

                    @Override
                    public @Nullable PopupStep<?> onChosen(TemplateResultGenerator selectedValue, boolean finalChoice) {
                        return this.doFinalStep(() -> {
                            final String result = selectedValue.process();
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new TextTransferable(result), null);
                            logger.debug("复制内容 {} 到剪贴板成功", result);
                        });
                    }
                }
        );
        listPopup.showInBestPositionFor(editor);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return true;
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "CV Engineer";
    }
}
