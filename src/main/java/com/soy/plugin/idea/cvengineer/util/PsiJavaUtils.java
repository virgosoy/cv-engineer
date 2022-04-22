package com.soy.plugin.idea.cvengineer.util;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Psi Java Util
 * @author zhengsy
 * @since 2022-04-19
 */
public final class PsiJavaUtils {

    private PsiJavaUtils(){
        throw new UnsupportedOperationException();
    }

    /**
     * 根据字段获取其类型的完全限定名
     * @param field 字段
     * @return 完全限定名字符串，如果找不到则返回 null
     */
    @Nullable
    public static String getTypeQualifiedNameByField(@NotNull PsiField field){
        PsiTypeElement fieldTypeElement = field.getTypeElement();
        if(fieldTypeElement == null){
            return null;
        }
        return getTypeQualifiedName(fieldTypeElement);
    }

    /**
     * 获取类型的完全限定名，如果是原始数据类型会被装箱
     * @param typeElement 类型元素
     * @return 完全限定名
     */
    @Nullable
    public static String getTypeQualifiedName(@NotNull PsiTypeElement typeElement){
        PsiType fieldType = typeElement.getType();
        if(fieldType instanceof PsiPrimitiveType){
            return Optional.ofNullable(((PsiPrimitiveType) fieldType).getBoxedType(typeElement))
                    .map(PsiClassType::resolve)
                    .map(PsiClass::getQualifiedName)
                    .orElse(null);
        }else if(fieldType instanceof PsiClassReferenceType){
            return Optional.ofNullable(((PsiClassReferenceType) fieldType).resolve())
                    .map(PsiClass::getQualifiedName)
                    .orElse(null);
        }
        return null;
    }

    /**
     * 获取类型的完全限定名，如果是原始数据类型会被装箱
     * @param psiType 类型
     * @return 完全限定名
     */
    @Nullable
    public static String getTypeQualifiedName(@NotNull PsiType psiType){
        if(psiType instanceof PsiPrimitiveType){
            // 这里和其他方法不太一样
            return ((PsiPrimitiveType) psiType).getBoxedTypeName();
        }else if(psiType instanceof PsiClassReferenceType){
            return Optional.ofNullable(((PsiClassReferenceType) psiType).resolve())
                    .map(PsiClass::getQualifiedName)
                    .orElse(null);
        }
        return null;
    }

    /**
     * 获取注解属性值的第一个值（如果是数组则取第一个）
     * // TODO: 目前仅支持第一个字面量表达式，有待改进
     * @param annotationAttributeValue
     * @return
     */
    @Nullable
    public static String getFirstValueInAnnotationAttributeValue(PsiAnnotationMemberValue annotationAttributeValue){
        if( annotationAttributeValue instanceof PsiArrayInitializerMemberValue ){
            // 如果是一个数组
            return Arrays.stream(annotationAttributeValue.getChildren())
                    .filter(c -> c instanceof PsiLiteralExpression)
                    .map(c -> (PsiLiteralExpression)c)
                    .findFirst()
                    .map(PsiLiteralValue::getValue)
                    .map(Object::toString)
                    .orElse(null);
        }else if(annotationAttributeValue instanceof PsiLiteralExpression){
            // 如果是一个字面量表达式
            return Optional.ofNullable(((PsiLiteralExpression) annotationAttributeValue).getValue())
                    .map(Object::toString)
                    .orElse(null);
        }
        return null;
    }

    /**
     * 根据注解和其元素名获取第一个元素值（如果是数组则取第一个）
     * @param annotation 注解
     * @param attributeName 元素名
     * @return 第一个元素值
     */
    public static String getFirstValueInAnnotation(PsiAnnotation annotation, String attributeName){
        final PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue(attributeName);
        return getFirstValueInAnnotationAttributeValue(attributeValue);
    }
}
