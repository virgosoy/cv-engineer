package com.soy.plugin.idea.cvengineer.util;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
        PsiType fieldType = fieldTypeElement.getType();
//        if(PsiType.VOID.equals(fieldType)){
//            // 字段怎么会是 void 类型。。。不可能的。。
//        }
        if(fieldType instanceof PsiPrimitiveType){
            return Optional.ofNullable(((PsiPrimitiveType) fieldType).getBoxedType(field))
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
}
