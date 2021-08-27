package org.redhat.authz.opa.bundles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.stream.Collectors;

@RegisterForReflection
public class OPAData {

    @SerializedName("namespaces")
    private final Map<String, Map<String, Map<String, List<String>>>> relations;

    public OPAData(String namespace, List<ExpandedResource> expandedResources) {
        this.relations = new HashMap<>();
        transformRelations(namespace, expandedResources);
    }

    public Map<String, Map<String, Map<String, List<String>>>> getRelations() {
        return relations;
    }

    private void transformRelations(String namespace, List<ExpandedResource> expandedResources) {
        this.relations.put(namespace, expandedResources.stream().collect(Collectors.toMap(ExpandedResource::getResource, ExpandedResource::getRelations)));
    }


}
