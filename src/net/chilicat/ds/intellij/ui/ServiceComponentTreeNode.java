package net.chilicat.ds.intellij.ui;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.PlatformIcons;
import net.chilicat.ds.intellij.model.ServiceComponent;

import javax.swing.*;

/**
 * @author dkuffner
 */
public class ServiceComponentTreeNode extends DSTreeNode<ServiceComponent> {

    private String displayName = null;

    public ServiceComponentTreeNode(Project project, ServiceComponent component) {
        super(project, component);
    }

    @Override
    public String getDisplayValue() {
        if (displayName == null) {
            ServiceComponent c = getUserObject();
            String className = c.getClassName();

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
        return getUserObject().getClassName();
    }

    @Override
    public String getTooltip() {
        return getUserObject().getClassName();
    }

    @Override
    public Icon getIcon() {
        return PlatformIcons.CLASS_ICON;
    }


}
