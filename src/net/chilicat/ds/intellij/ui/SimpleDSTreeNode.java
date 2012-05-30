package net.chilicat.ds.intellij.ui;

import com.intellij.openapi.project.Project;
import com.intellij.util.PlatformIcons;

import javax.swing.*;

/**
 * @author dkuffner
 */
public class SimpleDSTreeNode extends DSTreeNode<String> {

    private Icon icon = PlatformIcons.CLASS_ICON;
    private boolean classProvider;

    public SimpleDSTreeNode(Project project, String value) {
        super(project, value);
    }

    public void setClassProvider(boolean classProvider) {
        this.classProvider = classProvider;
    }

    public boolean isClassProvider() {
        return classProvider;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    public String getDisplayValue() {
        return getUserObject();
    }

    @Override
    public String getClassName() {
        if(!isClassProvider()) {
            return null;
        }
        return getUserObject();
    }

    @Override
    public Icon getIcon() {
        return icon;
    }
}
