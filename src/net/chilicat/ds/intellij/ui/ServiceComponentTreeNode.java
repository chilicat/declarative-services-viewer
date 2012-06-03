package net.chilicat.ds.intellij.ui;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import net.chilicat.ds.intellij.model.ServiceComponent;

import javax.swing.*;

/**
 * @author dkuffner
 */
public class ServiceComponentTreeNode extends DSTreeNode<ServiceComponent> {

    private String displayName = null;
    private Icon icon = JAVA_ICON;

    public ServiceComponentTreeNode(Project project, ServiceComponent component) {
        super(project, component);
        if (component.getFilePath().endsWith(".xml")) {
            icon = XML_ICON;
        } else {
            icon = JAVA_ICON;
        }
    }

    @Override
    public void openClass() {
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(getUserObject().getFilePath());
        if (file != null) {
            FileEditorManager.getInstance(getProject()).openFile(file, true, true);
        } else {
            super.openClass();
        }
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
        return icon;
    }


}
