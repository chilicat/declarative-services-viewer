package net.chilicat.ds.intellij.ui;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author dkuffner
 */
public abstract class DSTreeNode<T> extends DefaultMutableTreeNode {

    private final Project project;

    protected DSTreeNode(Project project, T o) {
        super(o);
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public abstract String getDisplayValue();

    public String getTooltip() {
        return null;
    }

    public Icon getIcon() {
        return null;
    }

    public abstract String getClassName();

    public void open() {
        String className = getClassName();
        final JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass implementationClass = psiFacade.findClass(className, GlobalSearchScope.projectScope(project));

        if (implementationClass != null) {
            PsiFile containingFile = implementationClass.getContainingFile();
            VirtualFile file = containingFile.getVirtualFile();
            if (file != null) {
                FileEditorManager.getInstance(project).openFile(file, true, true);
            }
        }
    }

    @Override
    public T getUserObject() {
        return (T) super.getUserObject();
    }
}
