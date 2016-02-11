package org.mule.modules.keycloak.client.service;

import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.client.filter.AuthHeaderFilter;
import org.mule.modules.keycloak.client.filter.EndAdminSessionFilter;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by moritz.moeller on 10.02.2016.
 */
public class UserServiceV18 implements UserService {

    private KeycloakAdminConfig keycloakConfig;
    private Client client;
    private ClientConfig clientConfig;
    private ObjectMapper mapper;
    private WebTarget target;

    public UserServiceV18(KeycloakAdminConfig keycloakConfig) {
        this.keycloakConfig = keycloakConfig;
        this.clientConfig = new ClientConfig();
        this.clientConfig.register(new AuthHeaderFilter(this.keycloakConfig));
        this.clientConfig.register(new EndAdminSessionFilter(this.keycloakConfig));
        this.client = ClientBuilder.newClient(clientConfig);
        this.mapper = new ObjectMapper();
    }

    @Override
    public UserRepresentation createUser(String userString) {
        return null;
    }

    @Override
    public UserRepresentation updateUser(String userString) {
        return null;
    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    public UserRepresentation getUser(String userId) throws IOException {
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        String jsonUser = target.request(MediaType.APPLICATION_JSON).get(String.class);
        return mapper.readValue(jsonUser, UserRepresentation.class);
    }

    public KeycloakAdminConfig getKeycloakConfig() {
        return keycloakConfig;
    }

    public void setKeycloakConfig(KeycloakAdminConfig keycloakConfig) {
        this.keycloakConfig = keycloakConfig;
    }
}
