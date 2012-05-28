package net.chilicat.ds.intellij.model;

/**
 * @author dkuffner
 */
public class ReferenceImpl implements Reference {
    private final String name, iface;

    public ReferenceImpl(String name, String iface) {
        this.name = name;
        this.iface = iface;
    }

    public String getName() {
        return name;
    }

    public String getInterface() {
        return iface;
    }

    @Override
    public String toString() {
        return "ReferenceImpl{" +
                "name='" + name + '\'' +
                ", interface='" + iface + '\'' +
                '}';
    }
}
