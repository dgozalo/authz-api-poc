package org.redhat.authz.opa.bundles;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RegisterForReflection
public class ExpandedResource {

    private String resource;

    private Map<String, List<String>> relations;

    private Map<String, List<List<String>>> computed;

    public Map<String, List<List<String>>> getComputed() {
        return computed;
    }

    public void setComputed(Map<String, List<List<String>>> computed) {
        this.computed = computed;
    }

    public ExpandedResource() {
        relations = new MultivaluedHashMap<>();
    }

    public Map<String, List<String>> getRelations() {
        return relations;
    }

    public void setRelations(Map<String, List<String>> relations) {
        this.relations = relations;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpandedResource that = (ExpandedResource) o;
        return Objects.equals(resource, that.resource) && Objects.equals(relations, that.relations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, relations);
    }

    @Override
    public String toString() {
        return "MergedTuples{" +
                "resource='" + resource + '\'' +
                ", relations=" + relations +
                '}';
    }
}
