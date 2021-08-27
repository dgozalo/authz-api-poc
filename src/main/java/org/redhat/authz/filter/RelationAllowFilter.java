package org.redhat.authz.filter;

import io.quarkus.security.identity.SecurityIdentity;
import org.redhat.authz.opa.OPAClient;
import org.redhat.authz.opa.Result;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Optional;

@WithRelation()
@Provider
public class RelationAllowFilter implements ContainerRequestFilter {

    @Context
    ResourceInfo info;

    @Inject
    OPAClient client;

    @Inject
    SecurityIdentity identity;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        if (identity.isAnonymous()) {
            containerRequestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
            return;
        }
        WithRelation an = info.getResourceMethod().getAnnotation(WithRelation.class);
        String relation = an.value();
        String clusterId = containerRequestContext.getUriInfo().getPathParameters().getFirst(an.resourceFromPathParam());
        String lookup = Optional.ofNullable(containerRequestContext.getUriInfo().getQueryParameters(true).getFirst("lookup")).orElse("external");
        Result r = this.client.checkRelationWithReason(relation,
                String.format("users:%1s", containerRequestContext.getSecurityContext().getUserPrincipal().getName()),
                "kafka_clusters", clusterId, lookup);
        if (!r.isAllow()) {
            containerRequestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }
    }
}
