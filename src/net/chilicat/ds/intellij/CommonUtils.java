package net.chilicat.ds.intellij;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author dkuffner
 */
public final class CommonUtils {
    private CommonUtils() {
        // all methods are static
    }

    /**
     * Returns the file for the given reference.
     *
     * @param element the reference.
     * @return a file or null if reference not physical.
     */
    @Nullable
    public static VirtualFile getVirtualFile(@NotNull PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        if (containingFile != null) {
            return containingFile.getVirtualFile();
        }
        return null;
    }

    /**
     * Returns the file for the given reference.
     *
     * @param reference the reference.
     * @return a file or null if reference not physical.
     */
    @Nullable
    public static PsiJavaFile getPsiJavaFile(@NotNull PsiReference reference) {
        final PsiElement element = reference.getElement();
        PsiFile containingFile = element.getContainingFile();
        if (containingFile instanceof PsiJavaFile) {
            return (PsiJavaFile) containingFile;
        }
        return null;
    }


    /**
     * Returns the module for the given psi reference.
     *
     * @param project   the project.
     * @param reference the reference.
     * @return the module or null if reference is not physical.
     */
    @Nullable
    public static Module getModuleForReference(@NotNull Project project, @NotNull PsiElement reference) {

        VirtualFile virtualFile = getVirtualFile(reference);
        if (virtualFile != null) {
            final ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
            final ProjectFileIndex fileIndex = rootManager.getFileIndex();
            return fileIndex.getModuleForFile(virtualFile);
        }
        return null;
    }


    /**
     * Annotation parameter list to map.
     *
     * @param list the list.
     * @return the map.
     */
    @NotNull
    public static Map<String, PsiNameValuePair> toAttributeMap(@NotNull PsiAnnotationParameterList list) {
        final Map<String, PsiNameValuePair> map = new HashMap<String, PsiNameValuePair>();
        for (PsiNameValuePair p : list.getAttributes()) {
            map.put(p.getName(), p);
        }
        return map;
    }

    /**
     * A String values in a annotation contains the quotes. This method return the value without quotes.
     *
     * @param p the pair.
     * @return the value.
     */
    @Nullable
    public static String getStringValue(@NotNull PsiNameValuePair p) {
        final PsiAnnotationMemberValue value = p.getValue();
        if (value != null) {
            String text = value.getText();
            return text.substring(1, text.length() - 1);
        }
        return null;
    }

    @Nullable
    public static String getStringValue(@NotNull Map<String, PsiNameValuePair> map, @NotNull String key, @Nullable String defValue) {
        PsiNameValuePair psiNameValuePair = map.get(key);
        if (psiNameValuePair != null) {
            String strValue = getStringValue(psiNameValuePair);
            return strValue != null ? strValue : defValue;
        }
        return defValue;
    }

    /**
     * Resolves a list of classes form the annotation. The class list must be set either on "value" or null.
     *
     * @param map the map.
     * @return a list of classes.
     */
    @NotNull
    public static List<PsiClass> getClassesFromAnnotation(@NotNull Map<String, PsiNameValuePair> map) {
        for (String key : Arrays.asList(null, "value")) {
            PsiNameValuePair pair = map.get(key);
            if (pair != null) {
                return getClasses(pair);
            }
        }
        return Collections.emptyList();
    }

    /**
     * Resolves all classes stored in the pair.
     * Returns only classes which can be resolved
     *
     * @param pair the pair.
     * @return a list.
     */
    @NotNull
    public static List<PsiClass> getClasses(@NotNull PsiNameValuePair pair) {
        List<PsiClass> classes = new ArrayList<PsiClass>();
        PsiAnnotationMemberValue value = pair.getValue();
        if (value != null) {
            if (value instanceof PsiClassObjectAccessExpression) {
                resolveClassAndAddToList(classes, (PsiClassObjectAccessExpression) value);
            } else {
                PsiElement[] children = value.getChildren();
                for (PsiElement child : children) {
                    if (child instanceof PsiClassObjectAccessExpression) {
                        PsiClassObjectAccessExpression exp = (PsiClassObjectAccessExpression) child;
                        resolveClassAndAddToList(classes, exp);
                    }
                }

            }
        }
        return classes;
    }


    private static void resolveClassAndAddToList(@NotNull List<PsiClass> classes, @NotNull PsiClassObjectAccessExpression value) {
        PsiType type = value.getOperand().getType();
        PsiClass psiClass = PsiTypesUtil.getPsiClass(type);

        // If fqn is not available than the class is not fully resolved.
        if (psiClass != null && psiClass.getQualifiedName() != null) {
            classes.add(psiClass);
        }
    }

}
