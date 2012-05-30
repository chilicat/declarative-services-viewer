package net.chilicat.ds.intellij;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.SearchRequestCollector;
import com.intellij.psi.search.SearchSession;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dkuffner
 */
public class MyProjectComponent implements ProjectComponent {
    private final Project project;
    
    public MyProjectComponent(Project project) {
        this.project = project;
    }

    public void initComponent() {

    }

    public void disposeComponent() {

    }

    @NotNull
    public String getComponentName() {
        return "MyProjectComponent";
    }

    public void projectOpened() {

    }

    public void projectClosed() {
        /*ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(OpenServiceViewerAction.SERVICE_VIEWER_ID);
        if(toolWindow != null) {
            toolWindow.getContentManager().dispose();
        } */
    }
}
