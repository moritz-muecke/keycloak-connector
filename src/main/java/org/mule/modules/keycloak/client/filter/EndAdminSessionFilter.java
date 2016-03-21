package org.mule.modules.keycloak.client.filter;

import org.mule.modules.keycloak.config.KeycloakAdminConfig;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by moritz.moeller on 11.02.2016.
 */
public class EndAdminSessionFilter implements ClientResponseFilter {

    private KeycloakAdminConfig config;
    private Client client;

    public EndAdminSessionFilter(KeycloakAdminConfig config) {
        this.config = config;
        this.client = ClientBuilder.newClient();
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if ((responseContext.getStatus() > Response.Status.OK.getStatusCode() &&
                responseContext.getStatus() < Response.Status.NO_CONTENT.getStatusCode())) {
            endAdminSession();
        }
    }

    public void endAdminSession() {
        Form form = new Form();
        form.param("refresh_token", config.getTokens().getRefreshToken());
        form.param("client_id", "security-admin-console");
        WebTarget target = client.target(config.getKeycloakAdminLogoutUri());
        target.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
    }
}
