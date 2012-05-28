package net.chilicat.ds.intellij.ui;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * @author dkuffner
 */
class ServiceTreeRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree jTree, Object o, boolean b, boolean b1, boolean b2, int i, boolean b3) {

        DSTreeNode<?> c = UiUtils.asDSTreeNode(o);
        if (c != null) {
            o = c.getDisplayValue();
        }

        Component comp = super.getTreeCellRendererComponent(jTree, o, b, b1, b2, i, b3);

        if (c != null) {
            setIcon(c.getIcon());
        }

        return comp;
    }
}
