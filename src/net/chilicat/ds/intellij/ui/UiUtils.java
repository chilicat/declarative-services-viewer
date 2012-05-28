package net.chilicat.ds.intellij.ui;

import net.chilicat.ds.intellij.model.ServiceComponent;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * @author dkuffner
 */
public final class UiUtils {

    private UiUtils() {
        // all methods are static
    }

    @Nullable
    public static DSTreeNode<?> asDSTreeNode(Object obj) {
        if (obj instanceof TreePath) {
            obj = ((TreePath) obj).getLastPathComponent();
        }
        if (obj instanceof DSTreeNode) {
            return (DSTreeNode) obj;
        }
        return null;
    }
}
