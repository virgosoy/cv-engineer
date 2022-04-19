package com.soy.plugin.idea.cvengineer.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author zhengsy
 * @since 2022-04-19
 */
public class DefaultCvIntention implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "使用 CV 工程师";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "CV Engineer";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        System.out.println("使用 CV 工程师");
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
