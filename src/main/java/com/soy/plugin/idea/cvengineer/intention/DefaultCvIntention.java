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
import com.soy.plugin.idea.cvengineer.util.PsiJavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhengsy
 * @since 2022-04-19
 */
public class DefaultCvIntention extends PsiElementBaseIntentionAction implements IntentionAction {

    @Override
    public @IntentionName @NotNull String getText() {
        return "使用 CV 工程师";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        System.out.println("使用 CV 工程师");

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

        System.out.println(data);

        // TODO
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
