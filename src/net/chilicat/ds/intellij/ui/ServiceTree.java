package net.chilicat.ds.intellij.ui;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.*;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static net.chilicat.ds.intellij.ui.UiUtils.asDSTreeNode;

/**
 * @author dkuffner
 */
class ServiceTree extends JTree {

    public ServiceTree() {
        setRootVisible(false);
        setCellRenderer(new ServiceTreeRenderer());

        OpenHandler openHandler = new OpenHandler();

        addMouseListener(openHandler);
        registerKeyboardAction(openHandler, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), WHEN_FOCUSED);
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

    private class OpenHandler extends MouseAdapter implements ActionListener {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 2 && isLeftMouseButton(mouseEvent)) {
                openSelection();
            }
        }

        public void actionPerformed(ActionEvent actionEvent) {
            openSelection();
        }
    }

    private void openSelection() {
        DSTreeNode<?> comp = asDSTreeNode(getSelectionModel().getLeadSelectionPath());
        if (comp != null) {
            comp.openClass();
        }
    }
}
