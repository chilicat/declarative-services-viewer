package net.chilicat.ds.intellij.ui;

import com.intellij.openapi.project.Project;
import net.chilicat.ds.intellij.model.Reference;

import javax.swing.*;

/**
 * @author dkuffner
 */
public class ReferenceTreeNode extends DSTreeNode<Reference> {

    private String displayName;

    public ReferenceTreeNode(Project project, Reference o) {
        super(project, o);
    }

    @Override
    public String getTooltip() {
        return getClassName();
    }

    @Override
    public Icon getIcon() {
        return JAVA_ICON;
    }

    @Override
    public String getDisplayValue() {
        if (displayName == null) {
            Reference c = getUserObject();
            String className = c.getInterface();

            String cls = className == null ? "" : className;
            int idx = cls.lastIndexOf(".");
            if (idx > -1) {
                cls = cls.substring(idx + 1, cls.length());
            }
            String name = c.getName();
            if (name == null) {
                displayName = cls;
            } else {
                displayName = name; //+ " (" + cls + ")";
            }
        }
        return displayName;
    }

    @Override
    public String getClassName() {
        return getUserObject().getInterface();
    }
}
