package hudson.plugins.scm.koji;

import hudson.model.Run;
import hudson.model.User;
import hudson.plugins.scm.koji.model.Build;
import hudson.plugins.scm.koji.model.RPM;
import hudson.scm.ChangeLogSet;
import hudson.scm.RepositoryBrowser;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KojiChangeLogSet extends ChangeLogSet<ChangeLogSet.Entry> {

    private final Build build;
    private final List<Entry> entries;

    public KojiChangeLogSet(Build build, Run<?, ?> run, RepositoryBrowser<?> browser) {
        super(run, browser);
        this.build = build;

        if (build != null) {
            List<Entry> list = Arrays.asList(
                    new KojiChangeEntry("Build Name", Collections.singletonList(new Hyperlink(build.getName()))),
                    new KojiChangeEntry("Build Version", Collections.singletonList(new Hyperlink(build.getVersion()))),
                    new KojiChangeEntry("Build Release", Collections.singletonList(new Hyperlink(build.getRelease()))),
                    new KojiChangeEntry("Build NVR", Collections.singletonList(new Hyperlink(build.getNvr()))),
                    new KojiChangeEntry("Build Tags", Collections.singletonList(new Hyperlink(String.join(", ", build.getTags())))),
                    new KojiChangeEntry("Build RPMs/Tarballs", getListFromRPMs(build.getRpms())),
                    new KojiChangeEntry("Build Sources", getListFromUrl(build.getSrcUrl())),
                    new KojiChangeEntry("Actions", getActions())
            );
            entries = Collections.unmodifiableList(list);
        } else {
            entries = Collections.emptyList();
        }
    }

    @Override
    public boolean isEmptySet() {
        return false;
    }

    @Override
    public Iterator<Entry> iterator() {
        return entries.iterator();
    }

    public static class KojiChangeEntry extends ChangeLogSet.Entry {

        private final String field;
        private final List<Hyperlink> hyperlinks;

        public KojiChangeEntry(String field, List<Hyperlink> hyperlinks) {
            this.field = field;
            this.hyperlinks = hyperlinks;
        }

        public String getField() {
            return field;
        }

        public List<Hyperlink> getHyperlinks() {
            return hyperlinks;
        }

        @Override
        public String getMsg() {
            return field + ": " + hyperlinks.toString();
        }

        @Override
        public User getAuthor() {
            return User.current();
        }

        @Override
        public Collection<String> getAffectedPaths() {
            return Collections.emptyList();
        }

    }

    private String nvr1() {
        return build.getNvr();
    }

    private String nvr2() {
        return build.getName() + "-" + build.getVersion() + "-" + build.getRelease();
    }

    private List<Hyperlink> getActions() {
        List<Hyperlink> r = new ArrayList<>();
        r.add(new Hyperlink("                           "));
        r.add(new Hyperlink("Most of htem missbehaves for builds, pulls and some others. Limit usage to Tests."));
        r.add(checkoutNvr(nvr1()));
        if (haveDualNvr()) {
            r.add(checkoutNvr(nvr2()));
        }
        r.add(new Hyperlink("re/run", down("kojiScmRe?type=run&id=" + getRun().getId()), "copy " + getRun().getId() + "/changelog.xml as build.xml and Build Now"));
        r.add(testNvr(nvr1()));
        if (haveDualNvr()) {
            r.add(testNvr(nvr2()));
        }
        r.add(new Hyperlink("re/checkout", down("kojiScmRe?type=checkout"), "remove build.xml and Build Now. If all builds are in processed.txt, will evolve to fail"));
        return r;
    }

    private String down(String s) {
        return "../../../../../../"+s+"&job="+ getRun().getFullDisplayName().split("\\s+")[0];
    }

    private boolean haveDualNvr() {
        return !nvr1().equals(nvr2());
    }

    private Hyperlink testNvr(String nvr) {
        return new Hyperlink("re/test?nvr=", down("kojiScmRe?type=test&nvr=" + nvr), "remove " + nvr + " from processed.txt Build Now");
    }

    private Hyperlink checkoutNvr(String nvr) {
        return new Hyperlink("re/checkout?nvr=", down("kojiScmRe?type=checkout&nvr=" + nvr), "remove build.xml, remove " + nvr + " from processed.txt Build Now");
    }

    private static List<Hyperlink> getListFromRPMs(List<RPM> rpms) {
        Predicate<RPM> predicate;
        if (atLeastOneRPMHasUrl(rpms)) {
            predicate = (rpm) -> rpm.getUrl() != null;
        } else {
            predicate = (rpm) -> true;
        }
        return rpms.stream()
                .filter(predicate)
                .map(rpm -> new Hyperlink(rpm.toString(), rpm.getUrl(), rpm.getHashSum()))
                   .collect(Collectors.toList());
    }

    private static List<Hyperlink> getListFromUrl(URL url) {
        List<Hyperlink> hyperlinks = new ArrayList<>();
        if (url != null) {
            if (RPM.Suffix.INSTANCE.endsWithSuffix(url.toString())) {
                hyperlinks.add(new Hyperlink(new File(url.getPath()).getName(), url.toString()));
            } else {
                hyperlinks.add(new Hyperlink("Source file not found. You can search for it here.", url.toString()));
            }
        }
        return hyperlinks;
    }

    private static boolean atLeastOneRPMHasUrl(List<RPM> rpms) {
        for (RPM rpm : rpms) {
            if (rpm.getUrl() != null) {
                return true;
            }
        }
        return false;
    }

    public static class Hyperlink {

        private final String displayedString;
        private final String url;
        private final String hashSum;

        Hyperlink(String displayedString) {
            this.displayedString = displayedString;
            this.url = null;
            this.hashSum = null;
        }

        Hyperlink(String displayedString, String url) {
            this.displayedString = displayedString;
            this.url = url;
            this.hashSum = null;
        }

        Hyperlink(String displayedString, String url, String hashSum) {
            this.displayedString = displayedString;
            this.url = url;
            this.hashSum = hashSum;
        }

        public String getDisplayedString() {
            return displayedString;
        }

        public String getUrl() {
            return url;
        }

        public boolean isContainingUrl() {
            return url != null;
        }

        public boolean isContainingHashSum() {
            return hashSum != null;
        }

        public String getHashSum() {
            return hashSum;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Hyperlink{");
            sb.append("displayedString='").append(displayedString).append('\'');
            sb.append(", url='").append(url).append('\'');
            sb.append(", hashSum='").append(hashSum).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
