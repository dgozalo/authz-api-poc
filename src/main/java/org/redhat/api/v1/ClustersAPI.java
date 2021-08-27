package org.redhat.api.v1;

import io.quarkus.security.Authenticated;
import org.redhat.authz.filter.WithRelation;
import org.redhat.authz.filter.MatchTags;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1/kafka/clusters")
@Authenticated
public class ClustersAPI {

    @GET
    @Path("{clusterId}")
    @WithRelation(value = "connect_to_cluster", resourceFromPathParam = "clusterId")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCluster(@PathParam("clusterId") String v) {
        return "Cluster: " + v;
    }

    @GET
    @Path("/withTag/{clusterId}")
    @MatchTags(value = "clusterId")
    @Produces(MediaType.TEXT_PLAIN)
    public String getClusterWithTag(@PathParam("clusterId") String v) {
        return "Cluster: " + v;
    }
}