package net.chilicat.ds.intellij;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author dkuffner
 */
public class AnnotationWrapper {
    private final PsiAnnotation annotation;
    private final Map<String, PsiNameValuePair> attributes;

    public AnnotationWrapper(PsiAnnotation annotation) {
        this.annotation = annotation;
        attributes = CommonUtils.toAttributeMap(annotation.getParameterList());
    }

    @Nullable
    public String getString(@NotNull String key, @Nullable String defValue) {
        PsiNameValuePair pair = attributes.get(key);
        if (pair != null) {
            final PsiAnnotationMemberValue value = pair.getValue();
            if (value != null) {
                String text = value.getText();
                return text.substring(1, text.length() - 1);
            }
            return null;
        }
        return defValue;
    }

    @Nullable
    public String getClassAsString(@NotNull String key) {
        PsiNameValuePair psiNameValuePair = attributes.get(key);
        if (psiNameValuePair != null) {
            PsiAnnotationMemberValue value = psiNameValuePair.getValue();
            if (value instanceof PsiClassObjectAccessExpression) {
                PsiType type = ((PsiClassObjectAccessExpression) value).getOperand().getType();
                PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
                // If fqn is not available than the class is not fully resolved.
                if (psiClass != null && psiClass.getQualifiedName() != null) {
                    return psiClass.getQualifiedName();
                }
            }
        }
        return null;
    }

    /**
     * Returns a list of class names.
     *
     * @return the classes.
     * @see #getClasses()
     */
    @NotNull
    public List<String> getClassesAsString() {
        List<PsiClass> classes = getClasses();
        List<String> result = new ArrayList<String>(classes.size());
        for (PsiClass cls : classes) {
            result.add(cls.getQualifiedName());
        }
        return result;
    }

    /**
     * Get classes from the default value which is either "@Annotation(value={VALUE,...})" or "@Annotation({VALUE,...})"
     *
     * @return the classes.
     */
    @NotNull
    public List<PsiClass> getClasses() {
        for (String key : Arrays.asList(null, "value")) {
            PsiNameValuePair pair = attributes.get(key);
            if (pair != null) {
                return CommonUtils.getClasses(pair);
            }
        }
        return Collections.emptyList();
    }

    /**
     * Get classes from the default value which is either "@Annotation(value={VALUE,...})" or "@Annotation({VALUE,...})"
     *
     * @return the classes.
     */
    @NotNull
    public List<PsiAnnotation> getAnnotations() {
        for (String key : Arrays.asList(null, "value")) {
            PsiNameValuePair pair = attributes.get(key);
            if (pair != null) {
                PsiAnnotationMemberValue value = pair.getValue();
                if (value != null) {
                    List<PsiAnnotation> res = new ArrayList<PsiAnnotation>();
                    for (PsiElement child : value.getChildren()) {
                        if (child instanceof PsiAnnotation) {
                            res.add((PsiAnnotation) child);
                        }
                    }
                    return res;
                }
            }
        }
        return Collections.emptyList();
    }
}
