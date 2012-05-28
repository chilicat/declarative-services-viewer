package net.chilicat.ds.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import net.chilicat.ds.intellij.model.ServiceComponent;
import net.chilicat.ds.intellij.model.ServiceComponentImpl;
import net.chilicat.ds.intellij.model.ReferenceImpl;

import java.util.ArrayList;
import java.util.List;

import static net.chilicat.ds.intellij.CommonUtils.getModuleForReference;
import static net.chilicat.ds.intellij.FelixScrUtils.*;

/**
 * @author dkuffner
 */
public class ResolveFelixSCRComponents {
    private final Project project;

    public ResolveFelixSCRComponents(Project project) {
        this.project = project;
    }

    public void resolveAsync(final Callback callback) {
        new Thread(new Runnable() {
            public void run() {
                callback.resolved(resolve());
            }
        }).start();
    }

    public List<ServiceComponent> resolve() {
        return ApplicationManager.getApplication().runReadAction(new Computable<List<ServiceComponent>>() {
            public List<ServiceComponent> compute() {
                return resolveImpl();
            }
        });
    }

    private List<ServiceComponent> resolveImpl() {
        final List<ServiceComponent> components = new ArrayList<ServiceComponent>();

        for (final PsiJavaFile file : finalAllComponents(project)) {
            for (final PsiClass cls : file.getClasses()) {

                if (isOuterClass(cls)) {
                    Module module = getModuleForReference(project, cls);

                    if (module != null) {

                        ServiceComponentImpl component = new ServiceComponentImpl(module.getName());
                        component.setClassName(cls.getQualifiedName());

                        VirtualFile virtualFile = file.getVirtualFile();
                        if (virtualFile != null) {
                            component.setFilePath(virtualFile.getUrl());
                        }

                        visitClass(cls, component);
                        visitFields(cls, component);
                        components.add(component);
                    }
                }

            }
        }

        return components;
    }


    private void visitFields(PsiClass cls, ServiceComponentImpl component) {
        for (PsiField field : cls.getAllFields()) {
            visitField(field, component);
        }
    }

    private boolean isOuterClass(PsiClass cls) {
        return cls.getContainingClass() == null;
    }

    private void visitField(PsiField field, ServiceComponentImpl component) {
        PsiModifierList modifierList = field.getModifierList();
        if (modifierList != null) {
            for (PsiAnnotation annotation : modifierList.getAnnotations()) {
                if (isReference(annotation)) {
                    AnnotationWrapper wrapper = new AnnotationWrapper(annotation);
                    String name = wrapper.getString("name", null);
                    String iface = wrapper.getClassAsString("referenceInterface");
                    component.addReference(new ReferenceImpl(name, iface));
                }
            }
        }
    }

    private void visitClass(PsiClass cls, ServiceComponentImpl component) {
        PsiModifierList modifierList = cls.getModifierList();
        if (modifierList != null) {
            for (PsiAnnotation annotation : modifierList.getAnnotations()) {
                if (isComponent(annotation)) {
                    AnnotationWrapper wrapper = new AnnotationWrapper(annotation);
                    component.setName(wrapper.getString("name", null));
                } else if (isService(annotation)) {
                    AnnotationWrapper wrapper = new AnnotationWrapper(annotation);
                    for (String service : wrapper.getClassesAsString()) {
                        component.addService(service);
                    }
                }
            }
        }
    }

    public static interface Callback {
        void resolved(List<ServiceComponent> components);
    }
}
