package net.chilicat.ds.intellij.ui;

import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * @author dkuffner
 */
public class SimpleDSTreeNode extends DSTreeNode<String> {

    public SimpleDSTreeNode(Project project, String value) {
        super(project, value);
    }

    @Override
    public String getDisplayValue() {
        return getUserObject();
    }

    @Override
    public String getClassName() {
        return getUserObject();
    }
}
