package org.mule.modules.keycloak.client.filter;

import org.mule.modules.keycloak.config.KeycloakAdminConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * This response filter can be registered at the http client. If registered the client ends an admin session at
 * keycloak after sending the request.
 *
 * @author Moritz Möller, AOE GmbH
 *
 */
public class EndAdminSessionFilter implements ClientResponseFilter {

    private final Logger logger = LoggerFactory.getLogger(EndAdminSessionFilter.class);

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

    /**
     * Calls the logout endpoint from keycloak with the refresh token from the KeycloakAdminConfig to end the admin
     * session
     */
    public void endAdminSession() {
        Form form = new Form();
        form.param("refresh_token", config.getTokens().getRefreshToken());
        form.param("client_id", "security-admin-console");
        WebTarget target = client.target(config.getKeycloakAdminLogoutUri());
        try {
            target.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        } catch (Exception e) {
            logger.error("Could not end admin session. Exception {} occurred: {}", e.getClass(), e.getMessage());
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
