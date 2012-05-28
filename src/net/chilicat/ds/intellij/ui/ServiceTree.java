package net.chilicat.ds.intellij.ui;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static net.chilicat.ds.intellij.ui.UiUtils.asDSTreeNode;

/**
 * @author dkuffner
 */
class ServiceTree extends JTree {

    public ServiceTree() {
        setRootVisible(false);
        setCellRenderer(new ServiceTreeRenderer());
        addMouseListener(new OpenMouseHandler());
    }

    @Override
    public String getToolTipText(MouseEvent mouseEvent) {
        TreePath path = getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
        if (path != null) {
            DSTreeNode<?> node = UiUtils.asDSTreeNode(path.getLastPathComponent());
            if (node != null) {
                return node.getTooltip();
            }
        }
        return null;
    }

    private class OpenMouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 2 && isLeftMouseButton(mouseEvent)) {
                DSTreeNode<?> comp = asDSTreeNode(getSelectionModel().getLeadSelectionPath());
                if (comp != null) {
                    comp.open();
                }
            }
        }
    }
}
