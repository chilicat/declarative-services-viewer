package net.chilicat.ds.intellij.ui;

import com.intellij.ide.actions.CloseTabToolbarAction;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.actions.CollapseAllAction;
import com.intellij.ui.treeStructure.actions.ExpandAllAction;
import com.intellij.util.PlatformIcons;
import net.chilicat.ds.intellij.OpenServiceViewerAction;
import net.chilicat.ds.intellij.ResolveFelixSCRComponents;
import net.chilicat.ds.intellij.ResolveXMLComponents;
import net.chilicat.ds.intellij.model.Reference;
import net.chilicat.ds.intellij.model.ServiceComponent;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * @author dkuffner
 */
public class ServiceTreePanel extends SimpleToolWindowPanel {

    private ServiceTree mainTree;

    private ServiceTree refAndServicesTree;

    private Project project;
    private Disposable disposer = new MyDisposable();
    private ToolWindow toolWindow;

    private java.util.List<ServiceComponent> components;

    private boolean showModules = true;

    public ServiceTreePanel() {
        super(false, true);

        Splitter content = new Splitter(false, 0.5f);

        content.setFirstComponent(initTree());
        content.setSecondComponent(initReferencesTree());

        setContent(content);

        initToolbar();

        loadReferencesAndServices(null);

    }

    private JScrollPane initTree() {
        mainTree = new ServiceTree();
        mainTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                DSTreeNode<?> node = UiUtils.asDSTreeNode(treeSelectionEvent.getNewLeadSelectionPath());
                if (node instanceof ServiceComponentTreeNode) {
                    ServiceComponentTreeNode scNode = (ServiceComponentTreeNode) node;
                    loadReferencesAndServices(scNode);

                } else {
                    loadReferencesAndServices(null);
                }

            }
        });

        ToolTipManager.sharedInstance().registerComponent(mainTree);
        return ScrollPaneFactory.createScrollPane(mainTree);
    }

    private void loadReferencesAndServices(@Nullable ServiceComponentTreeNode scNode) {
        if (scNode == null) {
            refAndServicesTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        } else {
            ServiceComponent userObject = scNode.getUserObject();

            SimpleDSTreeNode referenceRoot = new SimpleDSTreeNode(scNode.getProject(), "References");
            referenceRoot.setIcon(IconLoader.getIcon("/vcs/arrow_left.png"));
            for (Reference ref : userObject.getReferences()) {
                referenceRoot.add(new ReferenceTreeNode(scNode.getProject(), ref));
            }
            referenceRoot.sort(new DisplayNameSorter());

            SimpleDSTreeNode serviceRoot = new SimpleDSTreeNode(scNode.getProject(), "Services");
            serviceRoot.setIcon(IconLoader.getIcon("/vcs/arrow_right.png"));
            for (String service : userObject.getServices()) {
                SimpleDSTreeNode simpleDSTreeNode = new SimpleDSTreeNode(scNode.getProject(), service);
                serviceRoot.add(simpleDSTreeNode);
            }
            serviceRoot.sort(new DisplayNameSorter());


            SimpleDSTreeNode usedByRoot = new SimpleDSTreeNode(scNode.getProject(), "Used By");
            usedByRoot.setIcon(IconLoader.getIcon("/actions/previousOccurence.png"));
            for (ServiceComponent comp : resolveUsedBy(userObject)) {
                usedByRoot.add(new ServiceComponentTreeNode(project, comp));
            }
            usedByRoot.sort(new DisplayNameSorter());

            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            if (serviceRoot.getChildCount() > 0) {
                root.add(serviceRoot);
            }

            if (referenceRoot.getChildCount() > 0) {
                root.add(referenceRoot);
            }

            if (usedByRoot.getChildCount() > 0) {
                root.add(usedByRoot);
            }

            if (root.getChildCount() == 0) {
                root.add(new DefaultMutableTreeNode("No services or references provided"));
            }

            refAndServicesTree.setModel(new DefaultTreeModel(root));

            for (int i = 0; i < refAndServicesTree.getRowCount(); i++) {
                refAndServicesTree.expandRow(i);
            }
        }
    }

    private Set<ServiceComponent> resolveUsedBy(ServiceComponent userObject) {
        final Set<ServiceComponent> result = new HashSet<ServiceComponent>();
        for (String className : userObject.getServices()) {
            for (ServiceComponent other : components) {
                for (Reference ref : other.getReferences()) {
                    if (className.equals(ref.getInterface())) {
                        result.add(other);
                    }
                }
            }
        }

        return result;
    }

    private JScrollPane initReferencesTree() {
        refAndServicesTree = new ServiceTree();
        ToolTipManager.sharedInstance().registerComponent(refAndServicesTree);

        return ScrollPaneFactory.createScrollPane(refAndServicesTree);
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

        leftGroup.add(new ExpandAllAction(mainTree));
        leftGroup.add(new CollapseAllAction(mainTree));

        leftGroup.add(new ToggleAction("Show Modules", "Show Modules", IconLoader.getIcon("/objectBrowser/showModules.png")) {
            @Override
            public boolean isSelected(AnActionEvent anActionEvent) {
                return showModules;
            }

            @Override
            public void setSelected(AnActionEvent anActionEvent, boolean b) {
                if (showModules != b) {
                    showModules = b;
                    resolveContent(project);
                }
            }
        });

        leftGroup.add(new CloseTabToolbarAction() {
            public void actionPerformed(AnActionEvent e) {
                if (project != null && toolWindow != null) {
                    ToolWindowManager.getInstance(project).unregisterToolWindow(OpenServiceViewerAction.SERVICE_VIEWER_ID);

                }
            }
        });

        toolBarPanel.add(ActionManager.getInstance().createActionToolbar("DS", leftGroup, false).getComponent());
        setToolbar(toolBarPanel);
    }


    public void resolveContent(@Nullable final Project project) {
        DefaultMutableTreeNode loadingRoot = new DefaultMutableTreeNode();
        loadingRoot.add(new DefaultMutableTreeNode("Loading..."));
        final DefaultTreeModel model = new DefaultTreeModel(loadingRoot);

        mainTree.setModel(model);
        this.project = project;

        if (project != null) {
            ResolveFelixSCRComponents resolver = new ResolveFelixSCRComponents(project);
            ResolveXMLComponents resolveXMLComponents = new ResolveXMLComponents(project);

            MyResolveCallback callback = new MyResolveCallback(project, model);
            resolver.resolveAsync(callback);
            resolveXMLComponents.resolveAsync(callback);
        }
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

    private static class DisplayNameSorter implements Comparator<DSTreeNode> {
        public int compare(DSTreeNode o, DSTreeNode o1) {
            String name1 = o.getDisplayValue();
            String name2 = o1.getDisplayValue();
            name1 = name1 == null ? "" : name1;
            name2 = name2 == null ? "" : name2;
            return name1.compareTo(name2);
        }
    }

    private class MyDisposable implements Disposable {
        public void dispose() {
            ToolTipManager.sharedInstance().unregisterComponent(mainTree);
            ToolTipManager.sharedInstance().unregisterComponent(refAndServicesTree);
        }
    }

    private class MyResolveCallback implements ResolveFelixSCRComponents.ResolveCallback {
        private final Project project;
        private final DefaultTreeModel model;

        private List<ServiceComponent> comps = Collections.synchronizedList(new ArrayList<ServiceComponent>());

        public MyResolveCallback(Project project, DefaultTreeModel model) {
            this.project = project;
            this.model = model;
        }

        AtomicInteger count = new AtomicInteger(0);

        public void resolved(final List<ServiceComponent> components) {
            int count = this.count.incrementAndGet();
            comps.addAll(components);

            if (count == 2) {
                final DSTreeNode root = new SimpleDSTreeNode(project, "ROOT");

                if (!showModules) {
                    for (ServiceComponent comp : comps) {
                        root.add(new ServiceComponentTreeNode(project, comp));
                    }
                } else {
                    Map<String, SimpleDSTreeNode> modules = new HashMap<String, SimpleDSTreeNode>();
                    Icon moduleIcon = IconLoader.getIcon("/objectBrowser/showModules.png");
                    for (ServiceComponent comp : comps) {
                        SimpleDSTreeNode r = modules.get(comp.getModuleName());
                        if (r == null) {
                            r = new SimpleDSTreeNode(project, comp.getModuleName());
                            r.setClassProvider(false);
                            r.setIcon(moduleIcon);
                            modules.put(comp.getModuleName(), r);
                        }
                        r.add(new ServiceComponentTreeNode(project, comp));
                    }

                    for (SimpleDSTreeNode r : modules.values()) {
                        r.sort(new DisplayNameSorter());
                        root.add(r);
                    }
                }

                root.sort(new DisplayNameSorter());

                invokeLater(new Runnable() {
                    public void run() {
                        ServiceTreePanel.this.components = new ArrayList<ServiceComponent>(comps);
                        model.setRoot(root);

                        for (int i = 0; i < mainTree.getRowCount(); i++) {
                            mainTree.expandRow(i);
                        }
                    }
                });
            }
        }
    }
}