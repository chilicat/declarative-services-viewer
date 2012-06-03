package net.chilicat.ds.intellij.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
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

    public final static Icon JAVA_ICON = IconLoader.getIcon("/fileTypes/java.png");
    public final static Icon XML_ICON = IconLoader.getIcon("/fileTypes/xml.png");

    protected DSTreeNode(@NotNull Project project, @NotNull T o) {
        super(o);
        this.project = project;
    }

    public void sort(@NotNull Comparator comparator) {
        if (children != null) {
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

    public void openClass() {
        UiUtils.openClass(getProject(), getClassName());
    }

    @Override
    public T getUserObject() {
        //noinspection unchecked
        return (T) super.getUserObject();
    }
}
