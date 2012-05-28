package net.chilicat.ds.intellij.model;

import com.intellij.openapi.module.Module;

import java.util.List;
import java.util.Set;

/**
 * @author dkuffner
 */
public interface ServiceComponent {
    public String getFilePath();
    public String getModuleName();
    public String getName();
    public String getClassName();
    public Set<String> getServices();
    public List<Reference> getReferences();
}
