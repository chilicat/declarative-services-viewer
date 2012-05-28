package net.chilicat.ds.intellij;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
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





//        SearchSession session = new SearchSession();
//        SearchRequestCollector collector = new SearchRequestCollector(session);
//
//        PsiSearchHelper.SERVICE.getInstance(project).processRequests(collector, new Processor<PsiReference>() {
//            public boolean process(PsiReference psiReference) {
//                System.out.println(psiReference.toString());
//                return false;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });
//
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
