package net.chilicat.ds.intellij.ui;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author dkuffner
 */
public abstract class DSTreeNode<T> extends DefaultMutableTreeNode {

    private final Project project;

    protected DSTreeNode(@NotNull Project project, @NotNull T o) {
        super(o);
        this.project = project;
    }

    public void sort(@NotNull Comparator comparator) {
        if(children != null) {
            //noinspection unchecked
            Collections.sort(children, comparator);
        }
    }


    @NotNull
    public Project getProject() {
        return project;
    }

    @Nullable
    public abstract String getDisplayValue();

    @Nullable
    public String getTooltip() {
        return null;
    }

    @Nullable
    public Icon getIcon() {
        return null;
    }

    @Nullable
    public abstract String getClassName();

    public void open() {
        String className = getClassName();
        if(className != null) {
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
    }

    @Override
    public T getUserObject() {
        //noinspection unchecked
        return (T) super.getUserObject();
    }
}
