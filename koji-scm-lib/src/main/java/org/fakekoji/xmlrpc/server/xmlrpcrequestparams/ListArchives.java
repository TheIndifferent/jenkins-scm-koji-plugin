package org.fakekoji.xmlrpc.server.xmlrpcrequestparams;

import hudson.plugins.scm.koji.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fakekoji.xmlrpc.server.xmlrpcrequestparams.XmlRpcRequestUtils.starStarLabel;
import static org.fakekoji.xmlrpc.server.xmlrpcrequestparams.XmlRpcRequestUtils.toMap;

public class ListArchives implements XmlRpcRequestParams {

    private final Integer buildId;
    private final List<String> archs;

    public ListArchives(Integer buildId, List<String> archs) {
        this.buildId = buildId;
        this.archs = archs;
    }

    public ListArchives(Object object) {
        final Map<String, Object> map = toMap(object);
        this.buildId = (Integer) map.get(Constants.buildID);
        archs = getArchs(map.get(Constants.arches));
    }

    private List<String> getArchs(Object object) {
        if (object == null) {
            return null;
        }
        final List<String> archList = new ArrayList<>();
        final Object[] archs = (Object[]) object;
        for (final Object arch : archs)
            archList.add((String) arch);
        return archList;
    }

    @Override
    public Object toObject() {
        final Map<String, Object> map = new HashMap<>();
        map.put(Constants.buildID, buildId);
        map.put(starStarLabel, Boolean.TRUE);
        if (archs != null && !archs.isEmpty()) {
            map.put(Constants.arches, archs.toArray());
        }
        return map;
    }

    @Override
    public String getMethodName() {
        return Constants.listArchives;
    }

    public Integer getBuildId() {
        return buildId;
    }

    public List<String> getArchs() {
        return archs;
    }
}
