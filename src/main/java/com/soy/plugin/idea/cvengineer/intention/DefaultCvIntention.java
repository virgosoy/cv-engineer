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
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.TextTransferable;
import com.soy.plugin.idea.cvengineer.template.BaseTemplateResultGenerator;
import com.soy.plugin.idea.cvengineer.template.BaseTsTemplateResultGenerator;
import com.soy.plugin.idea.cvengineer.template.TemplateResultGenerator;
import com.soy.plugin.idea.cvengineer.util.PsiJavaUtils;
import org.jetbrains.annotations.Contract;
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
                ((List<Map<String, String>>) data.get("fields")).forEach(m -> m.put("tsType",
                        Optional.ofNullable(m.get("type"))
                                .map(this::convertType)
                                .orElse(null)));
                return data;
            }
        };

        final BaseTemplateResultGenerator allFieldName = new BaseTemplateResultGenerator("所有字段名称", "allFieldName") {
            @Override
            protected Object getDataModel() {
                return data;
            }
        };

        final BaseTemplateResultGenerator tsApi = new BaseTsTemplateResultGenerator("前端api", "tsApi") {

            /**
             * spring requestMapping 的注解类型列表
             */
            final List<String> mappingMap = List.of(
                    "org.springframework.web.bind.annotation.RequestMapping",
                    "org.springframework.web.bind.annotation.GetMapping",
                    "org.springframework.web.bind.annotation.PostMapping",
                    "org.springframework.web.bind.annotation.PutMapping",
                    "org.springframework.web.bind.annotation.DeleteMapping",
                    "org.springframework.web.bind.annotation.PatchMapping"
            );

            /**
             * 处理路径变量，将 {} 变成 ${}
             * @param path 路径
             * @return 处理后的路径
             */
            @Contract("null -> null; !null -> !null")
            private String resolvePathVariable(@Nullable String path){
                if(path == null) {
                    return null;
                }
                return path.replace("{", "${");
            }

            @Override
            protected Object getDataModel() {
                Map<String, Object> data = new HashMap<>();

                final PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);

                if(method!=null){

                    // 类上的 requestMapping 的值
                    final String classRequestMappingValue = Optional.ofNullable(method.getContainingClass())
                            .flatMap(v -> Arrays.stream(v.getAnnotations())
                                    .filter(annotation -> mappingMap.contains(annotation.getQualifiedName()))
                                    .findFirst()
                            )
                            .map(annotation -> PsiJavaUtils.getFirstValueInAnnotation(annotation, "value"))
                            .orElse(null);
                    data.put("class", Collections.singletonMap("requestMappingValue", this.resolvePathVariable(classRequestMappingValue)));

                    Map<String, Object> methodData = new HashMap<>();
                    // 方法名
                    methodData.put("name", method.getName());

                    // 方法参数
                    final PsiParameterList parameterList = method.getParameterList();
                    final PsiParameter[] parameters = parameterList.getParameters();
                    final List<Map<String, Object>> parametersData = Arrays.stream(parameters).map(parameter -> {
                        Map<String, Object> parameterData = new HashMap<>(2);
                        parameterData.put("name", parameter.getName());
                        parameterData.put("type", Optional.ofNullable(parameter.getTypeElement())
                                .map(v -> PsiJavaUtils.getTypeQualifiedName(v))
                                .map(v -> this.convertType(v))
                                .orElse(null));
                        return parameterData;
                    }).collect(Collectors.toList());
                    methodData.put("parameters", parametersData);

                    // 方法返回类型
                    String returnTypeName = null;
                    final PsiTypeElement returnTypeElement = method.getReturnTypeElement();
                    if(returnTypeElement != null){
                        final PsiType returnType = returnTypeElement.getType();
                        if(returnType instanceof PsiClassReferenceType){
                            final PsiClassReferenceType type = (PsiClassReferenceType) returnType;
                            if("Response".equals(type.getClassName())) {
                                final PsiType[] returnTypeParameters = type.getParameters();
                                if(returnTypeParameters.length == 1){
                                    returnTypeName = PsiJavaUtils.getTypeQualifiedName(returnTypeParameters[0]);
                                }
                            }
                        }
                        returnTypeName = Optional.ofNullable(returnTypeName).orElseGet(() -> PsiJavaUtils.getTypeQualifiedName(returnTypeElement));
                    }
                    methodData.put("returnType", Optional.ofNullable(returnTypeName).map(v -> this.convertType(v)).orElse(null));

                    // 方法上的 requestMapping 的值
                    final String methodRequestMappingValue = Arrays.stream(method.getAnnotations())
                            .filter(annotation -> mappingMap.contains(annotation.getQualifiedName()))
                            .findFirst()
                            .map(annotation -> PsiJavaUtils.getFirstValueInAnnotation(annotation, "value"))
                            .orElse(null);
                    methodData.put("requestMappingValue", this.resolvePathVariable(methodRequestMappingValue));

                    data.put("method", methodData);
                }
                System.out.println(data);
                return data;
            }
        };

        final TemplateResultGenerator[] templateResultGenerators = {tsType, allFieldName, tsApi};

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
