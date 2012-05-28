package net.chilicat.ds.intellij.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author dkuffner
 */
public class ServiceComponentImpl implements ServiceComponent {
    private String name, className, moduleName, filePath;
    private final Set<String> services = new HashSet<String>();
    private final List<Reference> references = new ArrayList<Reference>();

    public ServiceComponentImpl(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public Set<String> getServices() {
        return services;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public void addReference(ReferenceImpl reference) {
        references.add(reference);
    }

    public void addService(String qualifiedName) {
        services.add(qualifiedName);
    }


    @Override
    public String toString() {
        return "ComponentImpl{\n" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ",\n services=" + services +
                ",\n references=" + references +
                ",\n filePath='" + filePath + '\'' +
                "\n}";
    }
}
