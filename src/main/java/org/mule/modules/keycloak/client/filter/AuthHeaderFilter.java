package org.mule.modules.keycloak.client.filter;

import org.codehaus.jackson.map.ObjectMapper;
import org.keycloak.representations.AccessTokenResponse;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by moritz.moeller on 11.02.2016.
 */
public class AuthHeaderFilter implements ClientRequestFilter {


    private KeycloakAdminConfig config;
    private ObjectMapper mapper;
    private Client client;

    public AuthHeaderFilter(KeycloakAdminConfig config) {
        this.config = config;
        this.client = ClientBuilder.newClient();
        this.mapper = new ObjectMapper();
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        config.setTokens(requestAccessToken());
        requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, "Bearer " + config.getTokens().getToken());
    }

    public AccessTokenResponse requestAccessToken() throws IOException {
        Form form = new Form();
        form.param("grant_type", "password");
        form.param("client_id", "security-admin-console");
        form.param("username", config.getAdminUser());
        form.param("password", config.getAdminPassword());

        WebTarget target = client.target(config.getKeycloakAdminTokenUri());
        String jsonAccessToken = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        return mapper.readValue(jsonAccessToken, AccessTokenResponse.class);
    }
}
