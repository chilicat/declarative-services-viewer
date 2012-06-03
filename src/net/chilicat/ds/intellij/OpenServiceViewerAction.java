package net.chilicat.ds.intellij;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManagerAdapter;
import com.intellij.ui.content.ContentManagerEvent;
import net.chilicat.ds.intellij.ui.ServiceTreePanel;

/**
 * @author dkuffner
 */
public class OpenServiceViewerAction extends AnAction {

    public final static String SERVICE_VIEWER_ID = "Declarative Services";

    public void actionPerformed(AnActionEvent e) {

        final Project project = PlatformDataKeys.PROJECT.getData(e.getDataContext());

        ToolWindowManager windowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = windowManager.getToolWindow(SERVICE_VIEWER_ID);

        ServiceTreePanel tree;
        if (toolWindow == null) {
            tree = new ServiceTreePanel();

            toolWindow = windowManager.registerToolWindow(SERVICE_VIEWER_ID, true, ToolWindowAnchor.BOTTOM);

            Content content = toolWindow.getContentManager().getFactory().createContent(tree, "Components", false);
            content.setShouldDisposeContent(true);
            content.setToolwindowTitle("Components");
            content.setCloseable(true);
            content.setDisplayName("Components");
            content.setPreferredFocusableComponent(tree.getTreeComponent());
            content.setDisposer(tree.getDisposer());
            toolWindow.getContentManager().addContent(content);
            toolWindow.setTitle("Services");
            toolWindow.setToHideOnEmptyContent(true);
            toolWindow.getContentManager().addContentManagerListener(new ContentManagerAdapter() {
                @Override
                public void contentRemoved(ContentManagerEvent event) {
                    ToolWindowManager.getInstance(project).unregisterToolWindow(OpenServiceViewerAction.SERVICE_VIEWER_ID);
                }
            });
            tree.setToolWindow(toolWindow);
        } else {
            tree = (ServiceTreePanel) toolWindow.getContentManager().getContent(0).getComponent();
        }

        final ServiceTreePanel finalTree = tree;
        toolWindow.activate(new Runnable() {
            public void run() {
                finalTree.resolveContent(project);
            }
        });

    }

}
