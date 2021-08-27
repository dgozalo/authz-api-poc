package org.redhat.api.v1;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.redhat.authz.opa.bundles.BundlesLoader;

import java.io.IOException;
import java.io.InputStream;


@Path("/v1/bundles")
@ApplicationScoped
public class BundlesAPI {

    private BundlesLoader bundlesLoader;

    @GET
    @Produces("application/gzip")
    public Response serveOpaBundle(@HeaderParam("Accept-Encoding") String acceptEncoding) {
        StreamingOutput output = strOut -> {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            try (GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(strOut);
                 TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut);
                 InputStream externalPolicyIS = classloader.getResourceAsStream("/rego/keto_external_policy.rego");
                 InputStream internalPolicyIS = classloader.getResourceAsStream("/rego/keto_internal_policy.rego")) {
                putDataJsonInTar(tOut);
                assert externalPolicyIS != null;
                putPolicyInTar(tOut, externalPolicyIS, "external_policy.rego");
                assert internalPolicyIS != null;
                putPolicyInTar(tOut, internalPolicyIS, "internal_policy.rego");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
        return Response.ok(output)
                .header("Content-Disposition", "attachment; filename=\"bundle.tar.gz\"")
                .build();
    }

    private void putDataJsonInTar(TarArchiveOutputStream tOut) throws IOException {
        TarArchiveEntry tarEntry = new TarArchiveEntry("data.json");
        String data = bundlesLoader.getExpandedRelationData();
        byte[] dataBytes = data.getBytes();
        tarEntry.setSize(dataBytes.length);
        tOut.putArchiveEntry(tarEntry);
        tOut.write(dataBytes);
        tOut.closeArchiveEntry();
    }

    private void putPolicyInTar(TarArchiveOutputStream tOut, InputStream is, String fileName) throws IOException {
        TarArchiveEntry policyTarEntry = new TarArchiveEntry(fileName);
        byte[] policyData = new byte[is.available()];
        policyTarEntry.setSize(is.read(policyData));
        tOut.putArchiveEntry(policyTarEntry);
        tOut.write(policyData);
        tOut.closeArchiveEntry();
    }

    @Inject
    public void setBundlesLoader(BundlesLoader bundlesLoader) {
        this.bundlesLoader = bundlesLoader;
    }

}


