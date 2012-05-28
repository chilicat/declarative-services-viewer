package net.chilicat.ds.intellij.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.PlatformIcons;
import net.chilicat.ds.intellij.OpenServiceViewerAction;
import net.chilicat.ds.intellij.ResolveFelixSCRComponents;
import net.chilicat.ds.intellij.model.Reference;
import net.chilicat.ds.intellij.model.ServiceComponent;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * @author dkuffner
 */
public class ServiceTreePanel extends SimpleToolWindowPanel {

    private ServiceTree mainTree;
    private ServiceTree servicesTree;
    private ServiceTree referencesTree;

    private Project project;
    private Disposable disposer = new MyDisposable();
    private ToolWindow toolWindow;

    public ServiceTreePanel() {
        super(false, true);

        Splitter content = new Splitter(false, 0.5f);


        JPanel consumesProvidesPanel = new JPanel(new GridLayout(2, 1));
        consumesProvidesPanel.add(initServicesTree());
        consumesProvidesPanel.add(initReferencesTree());

        content.setFirstComponent(initTree());
        content.setSecondComponent(consumesProvidesPanel);


        setContent(content);

        initToolbar();
    }

    private JScrollPane initTree() {
        mainTree = new ServiceTree();
        mainTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                DSTreeNode<?> node = UiUtils.asDSTreeNode(treeSelectionEvent.getNewLeadSelectionPath());
                if (node instanceof ServiceComponentTreeNode) {
                    ServiceComponentTreeNode scNode = (ServiceComponentTreeNode) node;
                    loadServices(scNode);
                    loadReferences(scNode);
                } else {
                    loadServices(null);
                    loadReferences(null);
                }

            }
        });

        ToolTipManager.sharedInstance().registerComponent(mainTree);
        return ScrollPaneFactory.createScrollPane(mainTree);
    }

    private void loadReferences(@Nullable ServiceComponentTreeNode scNode) {
        if (scNode == null) {
            referencesTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        } else {
            ServiceComponent userObject = scNode.getUserObject();
            DefaultMutableTreeNode referenceRoot = new DefaultMutableTreeNode();
            for (Reference ref : userObject.getReferences()) {
                referenceRoot.add(new ReferenceTreeNode(scNode.getProject(), ref));
            }
            referencesTree.setModel(new DefaultTreeModel(referenceRoot));
        }
    }

    private void loadServices(@Nullable ServiceComponentTreeNode scNode) {
        if (scNode == null) {
            servicesTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        } else {
            ServiceComponent userObject = scNode.getUserObject();
            DefaultMutableTreeNode serviceRoot = new DefaultMutableTreeNode();
            for (String service : userObject.getServices()) {
                SimpleDSTreeNode simpleDSTreeNode = new SimpleDSTreeNode(scNode.getProject(), service);
                serviceRoot.add(simpleDSTreeNode);
            }
            servicesTree.setModel(new DefaultTreeModel(serviceRoot));
        }
    }

    private JScrollPane initServicesTree() {
        servicesTree = new ServiceTree();
        ToolTipManager.sharedInstance().registerComponent(servicesTree);
        loadServices(null);
        return ScrollPaneFactory.createScrollPane(servicesTree);
    }

    private JScrollPane initReferencesTree() {
        referencesTree = new ServiceTree();
        ToolTipManager.sharedInstance().registerComponent(referencesTree);
        loadReferences(null);
        return ScrollPaneFactory.createScrollPane(referencesTree);
    }


    private void initToolbar() {
        JPanel toolBarPanel = new JPanel(new GridLayout());

        DefaultActionGroup leftGroup = new DefaultActionGroup();
        leftGroup.add(new AnAction("Refresh", "Refreshes the content", PlatformIcons.SYNCHRONIZE_ICON) {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                resolveContent(project);
            }
        });
        leftGroup.add(new AnAction("Close", "Close", PlatformIcons.DELETE_ICON) {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                if (project != null && toolWindow != null) {
                    ToolWindowManager.getInstance(project).unregisterToolWindow(OpenServiceViewerAction.SERVICE_VIEWER_ID);
                    //toolWindow.getContentManager().dispose();
                }
            }
        });
        toolBarPanel.add(ActionManager.getInstance().createActionToolbar("DS", leftGroup, false).getComponent());
        setToolbar(toolBarPanel);
    }


    public void resolveContent(@Nullable final Project project) {
        final DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode());

        if (project != null) {
            ResolveFelixSCRComponents resolver = new ResolveFelixSCRComponents(project);
            resolver.resolveAsync(new ResolveFelixSCRComponents.Callback() {
                public void resolved(java.util.List<ServiceComponent> components) {

                    final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
                    for (ServiceComponent comp : components) {
                        root.add(new ServiceComponentTreeNode(project, comp));
                    }

                    invokeLater(new Runnable() {
                        public void run() {
                            model.setRoot(root);
                        }
                    });

                }
            });
        }

        mainTree.setModel(model);
        this.project = project;
    }


    public JTree getTreeComponent() {
        return mainTree;
    }

    public Disposable getDisposer() {
        return disposer;
    }

    public void setToolWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    private class MyDisposable implements Disposable {
        public void dispose() {
            ToolTipManager.sharedInstance().unregisterComponent(mainTree);
            ToolTipManager.sharedInstance().unregisterComponent(servicesTree);
            ToolTipManager.sharedInstance().unregisterComponent(referencesTree);
        }
    }
}