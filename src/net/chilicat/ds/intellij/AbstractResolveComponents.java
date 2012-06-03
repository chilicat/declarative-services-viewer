package net.chilicat.ds.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import net.chilicat.ds.intellij.model.ServiceComponent;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dkuffner
 */
public abstract class AbstractResolveComponents {

    private final Project project;

    public Project getProject() {
        return project;
    }

    public AbstractResolveComponents(Project project) {
        this.project = project;
    }

    public void resolveAsync(final ResolveCallback callback) {
        new Thread(new Runnable() {
            public void run() {
                callback.resolved(resolve());
            }
        }).start();
    }

    public List<ServiceComponent> resolve() {
        try {
            return ApplicationManager.getApplication().runReadAction(new Computable<List<ServiceComponent>>() {
                public List<ServiceComponent> compute() {
                    return resolveImpl();
                }
            });
        } catch (RuntimeException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", ex);
            return Collections.emptyList();
        }
    }

    protected abstract List<ServiceComponent> resolveImpl();

    public static interface ResolveCallback {
        void resolved(List<ServiceComponent> components);
    }
}
