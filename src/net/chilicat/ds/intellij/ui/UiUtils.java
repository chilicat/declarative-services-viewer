package net.chilicat.ds.intellij.ui;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;

/**
 * @author dkuffner
 */
public final class UiUtils {

    private UiUtils() {
        // all methods are static
    }

    @Nullable
    public static DSTreeNode<?> asDSTreeNode(Object obj) {
        if (obj instanceof TreePath) {
            obj = ((TreePath) obj).getLastPathComponent();
        }
        if (obj instanceof DSTreeNode) {
            return (DSTreeNode) obj;
        }
        return null;
    }

    public static boolean openClass(Project project, String className) {
        if (className != null) {
            final JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
            final PsiClass implementationClass = psiFacade.findClass(className, GlobalSearchScope.projectScope(project));

            if (implementationClass != null) {
                PsiFile containingFile = implementationClass.getContainingFile();
                VirtualFile file = containingFile.getVirtualFile();
                if (file != null) {
                    FileEditorManager.getInstance(project).openFile(file, true, true);
                    return true;
                }
            }
        }
        return false;
    }
}
