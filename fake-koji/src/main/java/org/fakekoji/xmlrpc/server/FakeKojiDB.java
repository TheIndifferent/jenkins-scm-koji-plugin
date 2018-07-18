/*
 * The MIT License
 *
 * Copyright 2015 user.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fakekoji.xmlrpc.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import hudson.plugins.scm.koji.model.Build;
import hudson.plugins.scm.koji.model.RPM;
import org.fakekoji.http.AccessibleSettings;
import org.fakekoji.xmlrpc.server.core.FakeBuild;
import org.fakekoji.xmlrpc.server.utils.DirFilter;

/**
 * Hart beat of fake koji. This class works over directory, with similar
 * structure as koji have, and is trying to deduct informations just on content
 * and names. On those deductions it offers its content and even tags.
 */
public class FakeKojiDB {

    private final String[] projects;
    private final List<FakeBuild> builds;
    private final AccessibleSettings settings;

    FakeKojiDB(AccessibleSettings settings) {
        ServerLogger.log(new Date().toString() + " (re)initizing fake koji DB");
        this.settings = settings;
        File[] projectDirs = settings.getDbFileRoot().listFiles(new DirFilter());
        projects = new String[projectDirs.length];
        builds = new ArrayList<>();
        //read all projects
        for (int i = 0; i < projectDirs.length; i++) {
            File projectDir = projectDirs[i];
            projects[i] = projectDir.getName();
            //and all builds in those project
            File[] versions = projectDir.listFiles(new DirFilter());
            for (File version : versions) {
                File[] releases = version.listFiles(new DirFilter());
                for (File release : releases) {
                    FakeBuild b = new FakeBuild(projectDir.getName(), version.getName(), release.getName(), release, settings.getProjectMapping());
                    builds.add(b);
                }
            }

        }

    }

    Integer getPkgId(String requestedProject) {
        StringBuilder triedProjects = new StringBuilder();
        for (String project : projects) {
            triedProjects.append(" ").append(project);
            if (project.equals(requestedProject)) {
                //is there better str->int function?
                //indeed, the file. But number of projects is small.
                return project.hashCode();
            }
        }
        ServerLogger.log("Unknown project " + requestedProject + ". Tried: " + triedProjects + ".");
        return null;
    }

    List<Build> getProjectBuilds(Integer projectId) {
        List<Build> projectBuilds = new ArrayList<>();
        for (FakeBuild build : builds) {
            if (build.getProjectID() == projectId) {
                if (new IsFailedBuild(build.getDir()).reCheck().getLastResult()) {
                    ServerLogger.log("Removing build " + build.toString() + " from result. Contains FAILED records");
                    continue;
                }
                projectBuilds.add(build.toBuild());
            }
        }
        return projectBuilds;
    }

    FakeBuild getBuildById(Integer buildId) {
        for (FakeBuild build : builds) {
            if (build.getBuildID() == buildId) {
                return build;
            }
        }
        return null;
    }

    /**
     * This method is trying to deduct tags from content of build. Purpose is,
     * that if the build is for some os only, it should be tagged accodringly.
     * On contrary, if it is static, then it should pass anywhere
     *
     * @param buildId Integer
     * @return set of strings
     */

    public Set<String> getTags(Integer buildId) {
        for (FakeBuild build : builds) {
            if (build.getBuildID() == buildId) {
                return build.getTags();
            }
        }
        return Collections.emptySet();
    }
/*
    void checkAll() {
        for (String project : projects) {
            check(project);
        }
    }

    void check(String string) {
        Integer id = getPkgId(string);
        if (id == null) {
            return;
        }
        ServerLogger.log(string + " id=" + id);
        Object[] chBuilds = getProjectBuildsByProjectIdAsMaps(id);
        ServerLogger.log(string + " builds#=" + chBuilds.length);
        int bn = 0;
        for (Object chBuild : chBuilds) {
            bn++;
            ServerLogger.log("####### " + bn + " #######");
            Map m = (Map) chBuild;
            Set keys = m.keySet();
            for (Object key : keys) {
                Object value = m.get(key);
                ServerLogger.log("  " + key + ": " + value);
                if (key.equals(Constants.build_id)) {
                    ServerLogger.log("    tags:");
                    Object[] tags = getTags((Integer) value);
                    for (Object tag : tags) {
                        ServerLogger.log("      " + tag);
                    }
                    ServerLogger.log("Artifacts for given build");
                    FakeBuild bld = getBuildById((Integer) value);
                    bld.printExpectedArchesForThisBuild();
                    List<String> arches = bld.getArches();
                    ServerLogger.log("  archs: " + arches.size());
                    for (String arch : arches) {
                        ServerLogger.log("  logs: " + arch);
                        //list logs
                        List<File> logs = bld.getLogs(arch);
                        logs.stream().forEach((log) -> {
                            ServerLogger.log(log.toString());
                        });
                        //list others
                        ServerLogger.log("  all: " + arch);
                        List<File> files = bld.getNonLogs(arch);
                        files.stream().forEach((f) -> {
                            ServerLogger.log(f.toString());
                        });
                    }
                }
                if (key.equals(Constants.rpms)) {
                    ServerLogger.log("  mapped: ");
                    Object[] rpms = (Object[]) value;
                    for (Object rpm : rpms) {
                        Map mrpms = (Map) rpm;
                        Set rks = mrpms.keySet();
                        rks.stream().forEach((rk) -> {
                            ServerLogger.log("    " + rk + ": " + mrpms.get(rk));
                        });

                    }
                }

            }
        }
        ServerLogger.log("Artifacts for given project " + string + " " + id);
        List<FakeBuild> blds = getProjectBuildsByProjectId(id);
        for (FakeBuild bld : blds) {
            List<String> arches = bld.getArches();
            for (String arch : arches) {
                ServerLogger.log("  arch: " + arch);
                //list logs
                List<File> logs = bld.getLogs(arch);
                logs.stream().forEach((log) -> {
                    ServerLogger.log(log.toString());
                });
                //list others
                List<File> files = bld.getNonLogs(arch);
                files.stream().forEach((f) -> {
                    ServerLogger.log(f.toString());
                });
            }
        }
    }
*/

    List<RPM> getRpms(Integer buildId, List<String> archs) {
        FakeBuild build = getBuildById(buildId);
        if (build == null) {
            return Collections.emptyList();
        }
        return build.getRpms(archs);
    }


    // all files other than rpms(tarxz, msi, ...) should be contained here
    List<String> getArchives(Object get, Object get0) {
        return Collections.emptyList();
    }
}
