package org.mule.modules.keycloak.client.service;

import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientResponse;
import org.keycloak.representations.idm.ErrorRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.client.filter.AuthHeaderFilter;
import org.mule.modules.keycloak.client.filter.EndAdminSessionFilter;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;
import org.mule.modules.keycloak.exception.UserAlreadyExistsException;
import org.mule.modules.keycloak.exception.UserNotFoundException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by moritz.moeller on 10.02.2016.
 */
public class UserServiceV18 implements UserService {

    private KeycloakAdminConfig keycloakConfig;
    private Client client;
    private ClientConfig clientConfig;
    private WebTarget target;

    public UserServiceV18(KeycloakAdminConfig keycloakConfig) {
        this.keycloakConfig = keycloakConfig;
        this.clientConfig = new ClientConfig();
        this.clientConfig.register(new AuthHeaderFilter(this.keycloakConfig));
        this.clientConfig.register(new EndAdminSessionFilter(this.keycloakConfig));
        this.client = ClientBuilder.newClient(clientConfig);
    }

    @Override
    public String createUser(String userString) throws IOException, UserAlreadyExistsException {
        KeycloakAdminConfig.mapper.readValue(userString, UserRepresentation.class);
        target = client.target(keycloakConfig.getKeycloakUserUri());
        Response response = target.request().post(Entity.entity(userString, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            return response.getHeaderString(HttpHeaders.LOCATION);
        } else {
            ErrorRepresentation error = KeycloakAdminConfig.mapper.readValue(response.readEntity(String.class), ErrorRepresentation.class);
            throw new UserAlreadyExistsException(error.getErrorMessage());
        }
    }

    @Override
    public UserRepresentation updateUser(String userString) {
        return null;
    }

    @Override
    public void deleteUser(String userId) throws UserNotFoundException {
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        Response response = target.request().delete();
        if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public void editUser(String userId, String userString) throws UserNotFoundException, IOException {
        KeycloakAdminConfig.mapper.readValue(userString, UserRepresentation.class);
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        Response response = target.request().put(Entity.entity(userString, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public UserRepresentation getUser(String userId) throws IOException {
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        String jsonUser = target.request(MediaType.APPLICATION_JSON).get(String.class);
        return KeycloakAdminConfig.mapper.readValue(jsonUser, UserRepresentation.class);
    }

    public KeycloakAdminConfig getKeycloakConfig() {
        return keycloakConfig;
    }

    public void setKeycloakConfig(KeycloakAdminConfig keycloakConfig) {
        this.keycloakConfig = keycloakConfig;
    }
}
