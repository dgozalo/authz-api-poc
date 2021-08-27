package org.redhat.authz.opa.bundles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.microprofile.config.ConfigProvider;
import sh.ory.keto.ApiClient;
import sh.ory.keto.ApiException;
import sh.ory.keto.api.ReadApi;
import sh.ory.keto.model.ExpandTree;
import sh.ory.keto.model.GetRelationTuplesResponse;
import sh.ory.keto.model.InternalRelationTuple;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MultivaluedHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class BundlesLoader {

    private final ReadApi ketoReadApi;

    private final Gson gson;

    public BundlesLoader() {
        String ketoUrl = ConfigProvider.getConfig().getValue("keto.url", String.class);
        System.out.println(ketoUrl);
        ApiClient ketoClient = new ApiClient();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        ketoClient.setBasePath(String.format("http://%s", ketoUrl));
        ketoReadApi = new ReadApi(ketoClient);
    }

    public String getExpandedRelationData() {
        try {
            List<ExpandedResource> expandedUserSets = getExpandedUserSets();
            OPAData opaData = new OPAData("kafka_clusters", expandedUserSets);
            return gson.toJson(opaData);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static ExpandedResource fromInternalRelationToMergedTuple(InternalRelationTuple internalRelationTuple) {
        ExpandedResource tuples = new ExpandedResource();
        Map<String, List<String>> rels = new MultivaluedHashMap<>();
        String subject = internalRelationTuple.getSubject();
        String relation = internalRelationTuple.getRelation();
        rels.put(relation, Collections.singletonList(subject));
        tuples.setResource(internalRelationTuple.getObject());
        tuples.setRelations(rels);
        return tuples;
    }

    private static ExpandedResource mergeRelations(List<ExpandedResource> mergedTuples) {
        return mergedTuples.stream()
                .reduce((expandedResource1, expandedResource2) -> {
                    expandedResource1.getRelations().putAll(expandedResource2.getRelations());
                    return expandedResource1;
                }).get();
    }

    private List<ExpandedResource> getExpandedUserSets() throws ApiException {
        GetRelationTuplesResponse rep = ketoReadApi.getRelationTuples("kafka_clusters", "", "", "", "", 10000L);
        List<ExpandedResource> m = Objects.requireNonNull(rep.getRelationTuples())
                .stream()
                .map(BundlesLoader::fromInternalRelationToMergedTuple)
                .collect(Collectors.groupingBy(ExpandedResource::getResource)).values()
                .stream()
                .map(BundlesLoader::mergeRelations).collect(Collectors.toList());

        List<ExpandedResource> k = m.stream().map(this::replaceAllMap).collect(Collectors.toList());
        System.out.println("Replaced representations");
        k.forEach(System.out::println);
        return k;
    }

    private Set<String> traverseTreeChildren(List<ExpandTree> children) {
        if (children == null || children.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> leaves = new HashSet<>();
        for (ExpandTree tree : children) {
            if (tree.getType() == ExpandTree.TypeEnum.LEAF) {
                if (tree.getSubject().contains("#")) {
                    System.out.println("Expanding tuple: " + tree.getSubject());
                    leaves.addAll(expandRelation(parseRelationTuple(tree.getSubject())));
                } else {
                    leaves.add(tree.getSubject());
                }
            } else {
                leaves.addAll(traverseTreeChildren(tree.getChildren()));
            }
        }
        return leaves;
    }

    private Set<String> expandRelation(InternalRelationTuple internalRelationTuple) {
        Set<String> subjects = new HashSet<>();
        try {
            System.out.println(internalRelationTuple);
            ExpandTree tree = ketoReadApi.getExpand(internalRelationTuple.getNamespace(), internalRelationTuple.getObject(), internalRelationTuple.getRelation(), 100L);
            if (tree == null) {
                System.out.println("Response is null");
                return Collections.emptySet();
            }
            if (tree.getType().equals(ExpandTree.TypeEnum.LEAF)) {
                subjects.add(tree.getSubject());
            } else {
                subjects.addAll(traverseTreeChildren(tree.getChildren()));
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    private InternalRelationTuple parseRelationTuple(String relationTuple) {
        String[] parts = relationTuple.split(":");
        String namespace = parts[0];
        parts = parts[1].split("#");
        String object = parts[0];
        String relation = parts[1];
        InternalRelationTuple internalRelationTuple = new InternalRelationTuple();
        internalRelationTuple.setNamespace(namespace);
        internalRelationTuple.setObject(object);
        internalRelationTuple.setRelation(relation);
        return internalRelationTuple;
    }

    private ExpandedResource replaceAllMap(ExpandedResource expandedResource) {
        expandedResource.getRelations().replaceAll(this::expandRelationUsersets);
        return expandedResource;
    }

    private List<String> expandRelationUsersets(String s, List<String> vals) {
        return vals.stream().map(s1 -> {
            Set<String> leaves = new HashSet<>();
            if (s1.contains("#")) {
                System.out.println("=====================");
                System.out.println("------- " + s1 + " -------");
                InternalRelationTuple tu = parseRelationTuple(s1);
                leaves.addAll(expandRelation(tu));
            } else {
                leaves.add(s1);
            }
            System.out.println("=====================");
            leaves.forEach(System.out::println);
            return new ArrayList<>(leaves);
        }).flatMap(List::stream).collect(Collectors.toList());
    }
}
