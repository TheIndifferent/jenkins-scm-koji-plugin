package org.fakekoji.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class Platform implements  Comparable<Platform> {

    private final String id;
    private final String os;
    private final String version;
    private final String architecture;
    private final List<Provider> providers;
    private final String vmName;
    private final List<String> tags;

    public Platform() {
        id = null;
        vmName = null;
        os = null;
        version = null;
        architecture = null;
        providers = null;
        tags = null;
    }

    public Platform(
            String os,
            String version,
            String architecture,
            List<Provider> providers,
            String vmName,
            List<String> tags
    ) {
        this.os = os;
        this.version = version;
        this.architecture = architecture;
        this.providers = providers;
        this.vmName = vmName;
        this.tags = tags;
        id = os + version + '.' + architecture;
    }

    public Platform(
            String id,
            String os,
            String version,
            String architecture,
            List<Provider> providers,
            String vmName,
            List<String> tags
    ) {
        this.id = id;
        this.os = os;
        this.version = version;
        this.architecture = architecture;
        this.providers = providers;
        this.vmName = vmName;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public String getOs() {
        return os;
    }

    public String getVersion() {
        return version;
    }

    public String getArchitecture() {
        return architecture;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public String getVmName() {
        return vmName;
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Platform)) return false;
        Platform platform = (Platform) o;
        return Objects.equals(os, platform.os) &&
                Objects.equals(version, platform.version) &&
                Objects.equals(architecture, platform.architecture) &&
                Objects.equals(providers, platform.providers) &&
                Objects.equals(vmName, platform.vmName) &&
                Objects.equals(tags, platform.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(os, version, architecture, vmName, tags);
    }

    @Override
    public String toString() {
        return "Platform{" +
                "os='" + os + '\'' +
                ", version='" + version + '\'' +
                ", architecture='" + architecture + '\'' +
                ", providers='" + providers + '\'' +
                ", vmName='" + vmName + '\'' +
                ", tags=" + tags +
                '}';
    }

    public String toOsVar() {
        return getOs() + '.' + getVersion();
    }

    @Override
    public int compareTo(@NotNull Platform platform) {
        return this.getId().compareTo(platform.getId());
    }

    public static class Provider {
        private final String id;
        private final List<String> hwNodes;
        private final List<String> vmNodes;

        public Provider() {
            id = null;
            hwNodes = null;
            vmNodes = null;
        }

        public Provider(
                String id,
                List<String> hwNodes,
                List<String> vmNodes
        ) {
            this.id = id;
            this.hwNodes = hwNodes;
            this.vmNodes = vmNodes;
        }

        public String getId() {
            return id;
        }

        public List<String> getHwNodes() {
            return hwNodes;
        }

        public List<String> getVmNodes() {
            return vmNodes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Provider)) return false;
            Provider provider = (Provider) o;
            return Objects.equals(id, provider.id) &&
                    Objects.equals(hwNodes, provider.hwNodes) &&
                    Objects.equals(vmNodes, provider.vmNodes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, hwNodes, vmNodes);
        }

        @Override
        public String toString() {
            return "Provider{" +
                    "id='" + id + '\'' +
                    ", hwNodes=" + hwNodes +
                    ", vmNodes=" + vmNodes +
                    '}';
        }
    }
}

