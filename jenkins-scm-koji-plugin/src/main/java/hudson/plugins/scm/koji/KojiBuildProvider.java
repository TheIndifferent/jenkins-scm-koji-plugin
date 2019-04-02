package hudson.plugins.scm.koji;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import org.fakekoji.xmlrpc.server.JavaServerConstants;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class KojiBuildProvider implements Describable<KojiBuildProvider>, Serializable {

    private final String topUrl;
    private final String downloadUrl;

    @DataBoundConstructor
    public KojiBuildProvider(String topUrl, String downloadUrl) {
        this.topUrl = topUrl;
        this.downloadUrl = downloadUrl;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Descriptor<KojiBuildProvider> getDescriptor() {
        return Jenkins.getActiveInstance().getDescriptorOrDie(getClass());
    }

    public String getTopUrl() {
        return replaceXPort(topUrl);
    }

    public String getDownloadUrl() {
        return replaceDPORT(downloadUrl);
    }

    private static String replaceDPORT(String url) {
        return url.replace(
                ":" + JavaServerConstants.dPortAxiom,
                ":" + JavaServerConstants.DFAULT_DWNLD_PORT
        );
    }

    private static String replaceXPort(String url) {
        return url.replace(
                ":" + JavaServerConstants.xPortAxiom,
                ":" + JavaServerConstants.DFAULT_RP2C_PORT
        );
    }

    @Extension
    public static final class KojiBuildProviderDescriptor extends Descriptor<KojiBuildProvider> {

        private static final String SUPPORTED_PROTOCOL = "HTTP";
        private static final String URL_INVALID = "The URL is invalid";
        private static final String PROTOCOL_NOT_SUPPORTED = "Only " + SUPPORTED_PROTOCOL + " protocol is supported";

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Koji build provider";
        }

        FormValidation doCheckTopUrl(@QueryParameter String value) {
            try {
                new URL(replaceXPort(value));
            } catch (MalformedURLException e) {
                return FormValidation.error(URL_INVALID);
            }
            return FormValidation.ok();
        }

        FormValidation doCheckDownloadUrl(@QueryParameter String value) {
            try {
                final URL url = new URL(replaceDPORT(value));
                if (!url.getProtocol().equals(SUPPORTED_PROTOCOL.toLowerCase())) {
                    return FormValidation.error(PROTOCOL_NOT_SUPPORTED);
                }
            } catch (MalformedURLException e) {
                return FormValidation.error(URL_INVALID);
            }
            return FormValidation.ok();
        }
    }
}
