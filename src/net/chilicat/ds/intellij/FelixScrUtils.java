package net.chilicat.ds.intellij;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author dkuffner
 */
public final class FelixScrUtils {
    public final static String COMPONENT_CLASS = "org.apache.felix.scr.annotations.Component";
    public final static String REFERENCE_CLASS = "org.apache.felix.scr.annotations.Reference";
    public final static String SERVICE_CLASS = "org.apache.felix.scr.annotations.Service";
    public final static String REFERENCES_CLASS = "org.apache.felix.scr.annotations.References";

    private FelixScrUtils() {
        // all methods static.
    }

    /**
     * Returns the PsiClass component class for the given module.
     *
     * @param module the module
     * @return the psi class or null.
     */
    @Nullable
    public static PsiClass getComponentClass(@NotNull Module module) {
        return JavaPsiFacade.getInstance(module.getProject()).findClass(COMPONENT_CLASS, module.getModuleWithLibrariesScope());
    }

    /**
     * Returns the PsiClass reference class for the given module.
     *
     * @param module the module
     * @return the psi class or null.
     */
    @Nullable
    public static PsiClass getReferenceClass(@NotNull Module module) {
        return JavaPsiFacade.getInstance(module.getProject()).findClass(REFERENCE_CLASS, module.getModuleWithLibrariesScope());
    }

    /**
     * Is given annotation a reference annotation.
     *
     * @param annotation the annotation to check
     * @return true in case it is a reference.
     */
    public static boolean isReference(@NotNull PsiAnnotation annotation) {
        return isAnnotation(annotation, REFERENCE_CLASS);
    }

    /**
     * Is given annotation a reference annotation.
     *
     * @param annotation the annotation to check
     * @return true in case it is a reference.
     */
    public static boolean isService(@NotNull PsiAnnotation annotation) {
        return isAnnotation(annotation, SERVICE_CLASS);
    }

    /**
     * Is given annotation a references annotation.
     *
     * @param annotation the annotation to check
     * @return true in case it is a references annotation.
     */
    public static boolean isReferences(@NotNull PsiAnnotation annotation) {
        return isAnnotation(annotation, REFERENCES_CLASS);
    }

    /**
     * Is given annotation a component annotation.
     *
     * @param annotation the annotation to check
     * @return true in case it is a component.
     */
    public static boolean isComponent(@NotNull PsiAnnotation annotation) {
        return isAnnotation(annotation, COMPONENT_CLASS);
    }

    private static boolean isAnnotation(@NotNull PsiAnnotation annotation, String cls) {
        final String qfn = annotation.getQualifiedName();
        return Comparing.strEqual(qfn, cls);
    }

    @NotNull
    public static Collection<PsiJavaFile> finalAllComponents(@NotNull Project project) {
        final Map<String, PsiJavaFile> files = new HashMap<String, PsiJavaFile>();

        for (PsiReference ref : findAllComponentReferences(project)) {
            if (ref.getElement().getParent() instanceof PsiAnnotation) {
                PsiJavaFile file = CommonUtils.getPsiJavaFile(ref);

                if (file != null) {
                    VirtualFile virtualFile = file.getVirtualFile();
                    if (virtualFile != null) {
                        files.put(virtualFile.getUrl(), file);
                    }
                }
            }
        }
        return files.values();
    }

    @NotNull
    public static Collection<PsiReference> findAllComponentReferences(@NotNull Project project) {
        final List<PsiReference> references = new ArrayList<PsiReference>();
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            references.addAll(findAllComponentReferences(module));
        }
        return references;
    }

    private static Collection<PsiReference> findAllComponentReferences(Module module) {
        PsiClass componentClass = FelixScrUtils.getComponentClass(module);
        if (componentClass != null) {
            Query<PsiReference> result = ReferencesSearch.search(componentClass, module.getModuleScope(false));
            return result.findAll();
        }
        return Collections.emptyList();
    }


}
