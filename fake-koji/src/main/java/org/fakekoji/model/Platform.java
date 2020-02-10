package org.fakekoji.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Platform implements  Comparable<Platform> {

    private final String id;
    private final String os;
    private final String version;
    private final String architecture;
    private final String kojiArch;
    private final List<Provider> providers;
    private final String vmName;
    private final List<String> tags;
    private final List<OToolVariable> variables;

    public Platform() {
        id = null;
        vmName = null;
        os = null;
        version = null;
        architecture = null;
        kojiArch = null;
        providers = null;
        tags = null;
        variables = null;
    }

    public Platform(
            String id,
            String os,
            String version,
            String architecture,
            String kojiArch,
            List<Provider> providers,
            String vmName,
            List<String> tags,
            List<OToolVariable> variables
    ) {
        this.id = id;
        this.os = os;
        this.version = version;
        this.architecture = architecture;
        this.kojiArch = kojiArch;
        this.providers = providers;
        this.vmName = vmName;
        this.tags = tags;
        this.variables = variables;
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

    public Optional<String> getKojiArch() {
        return Optional.ofNullable(kojiArch);
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

    public List<OToolVariable> getVariables() {
        return variables;
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
                Objects.equals(tags, platform.tags) &&
                Objects.equals(variables, platform.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(os, version, architecture, vmName, tags, variables);
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
                ", variables=" + variables +
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

    public static Platform create(Platform platform) {
        return new Platform(
                platform.os + platform.version + '.' + platform.getArchitecture(),
                platform.os,
                platform.version,
                platform.architecture,
                platform.kojiArch,
                platform.providers,
                platform.vmName,
                platform.tags,
                platform.variables
        );
    }
}

