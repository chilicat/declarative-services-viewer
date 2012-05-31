package net.chilicat.ds.intellij;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.XmlElementVisitor;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import net.chilicat.ds.intellij.model.ReferenceImpl;
import net.chilicat.ds.intellij.model.ServiceComponent;
import net.chilicat.ds.intellij.model.ServiceComponentImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;

/**
 * @author dkuffner
 */
public class ResolveXMLComponents extends AbstractResolveComponents {
    public ResolveXMLComponents(Project project) {
        super(project);
    }

    @Override
    protected List<ServiceComponent> resolveImpl() {
        Project project = getProject();
        ModuleManager moduleManager = ModuleManager.getInstance(project);

        List<ServiceComponent> components = new ArrayList<ServiceComponent>();

        for (Module m : moduleManager.getModules()) {
            ModuleRootManager rootManager = ModuleRootManager.getInstance(m);
            for (VirtualFile file : rootManager.getSourceRoots(false)) {
                VirtualFile manifest = file.findFileByRelativePath("META-INF/MANIFEST.MF");
                if (manifest != null && manifest.exists()) {
                    InputStream in = null;

                    try {
                        in = manifest.getInputStream();
                        Attributes mainAttributes = new Manifest(in).getMainAttributes();
                        String value = mainAttributes.getValue("Service-Component");
                        if (value != null) {
                            for (String v : value.split(",")) {
                                v = v.trim();
                                if (v.length() > 0) {
                                    VirtualFile serviceXML = file.findFileByRelativePath(v);
                                    if (serviceXML == null || !serviceXML.exists()) {
                                        Logger.getLogger(getClass().getName()).warning("Cannot find serviceComponent: " + v + " manifest: " + file.getUrl());
                                        continue;
                                    }

                                    PsiFile psiFile = PsiManager.getInstance(project).findFile(serviceXML);
                                    if (psiFile != null) {
                                        components.addAll(parseXml(project, m, psiFile));
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        Logger.getLogger(getClass().getName()).warning("Cannot read manifest: " + file.getUrl());
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException e) {
                            Logger.getLogger(getClass().getName()).warning("Cannot close manifest: " + file.getUrl());
                        }
                    }
                }

            }
        }
        return components;
    }

    private List<ServiceComponent> parseXml(Project project, Module module, final PsiFile psiFile) {
        Visitor visitor = new Visitor(module, psiFile);
        psiFile.acceptChildren(visitor);
        return visitor.components;

    }

    private static class Visitor extends XmlElementVisitor {
        private final List<ServiceComponent> components = new ArrayList<ServiceComponent>();
        private ServiceComponentImpl comp;
        private final Module module;
        private final PsiFile psiFile;

        public Visitor(Module module, PsiFile psiFile) {
            this.module = module;
            this.psiFile = psiFile;
        }

        @Override
        public void visitXmlDocument(XmlDocument document) {
            XmlTag rootTag = document.getRootTag();
            if (rootTag != null) {
                rootTag.accept(this);
            }
        }

        @Override
        public void visitXmlTag(XmlTag tag) {
            if(tag.getLocalName().equals("components")) {
                tag.acceptChildren(this);
            } else if (tag.getLocalName().equals("component")) {
                comp = new ServiceComponentImpl(module.getName());
                comp.setName(tag.getAttributeValue("name"));
                comp.setFilePath(psiFile.getVirtualFile().getUrl());
                tag.acceptChildren(this);
                components.add(comp);
                comp = null;
            } else if (tag.getLocalName().equals("service")) {
                // <provide interface="com.avid.central.services.authentication.um.AuthenticationService"/>
                tag.acceptChildren(this);
            } else if (tag.getLocalName().equals("provide")) {
                String iface = tag.getAttributeValue("interface");
                comp.addService(iface);
            } else if (tag.getLocalName().equals("reference")) {
                comp.addReference(new ReferenceImpl(tag.getAttributeValue("name"), tag.getAttributeValue("interface")));
            } else if (tag.getLocalName().endsWith("implementation")) {
                comp.setClassName(tag.getAttributeValue("class"));
            }
        }
    }
}
