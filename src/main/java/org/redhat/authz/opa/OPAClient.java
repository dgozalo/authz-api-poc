package org.redhat.authz.opa;

import com.bisnode.opa.client.OpaClient;
import com.bisnode.opa.client.query.OpaQueryApi;
import com.bisnode.opa.client.query.QueryForDocumentRequest;
import io.quarkus.arc.Unremovable;
import org.eclipse.microprofile.config.ConfigProvider;
import org.redhat.authz.opa.bundles.BundlesLoader;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Unremovable
public class OPAClient {

    OpaQueryApi client;

    @PostConstruct
    void init() {
        String opaUrl = ConfigProvider.getConfig().getValue("opa.url", String.class);
        System.out.println(opaUrl);
        client = OpaClient.builder()
                .opaConfiguration("http://"+opaUrl)
                .build();
        new BundlesLoader();
    }

    public Result checkRelationWithReason(String relation, String subject, String namespace, String object, String lookup) {
        return queryRelation(relation, subject, namespace, object, lookup);
    }

    private Result queryRelation(String relation, String subject, String namespace, String object, String lookup) {
        QueryForDocumentRequest queryForDocumentRequest = new QueryForDocumentRequest(new Input(
                subject, relation, namespace, object), String.format("ciam/authz/%s", lookup));
        return client.queryForDocument(queryForDocumentRequest, Result.class);
    }

}
