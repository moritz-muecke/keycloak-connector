package org.mule.modules.keycloak.client.service;

import org.keycloak.representations.idm.ErrorRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;
import org.mule.modules.keycloak.exception.UserAlreadyExistsException;
import org.mule.modules.keycloak.exception.UserNotFoundException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by moritz.moeller on 10.02.2016.
 */
public class UserService {

    private KeycloakAdminConfig keycloakConfig;
    private Client client;
    private WebTarget target;
    private Response response;

    public UserService(KeycloakAdminConfig keycloakConfig, Client client) {
        this.keycloakConfig = keycloakConfig;
        this.client = client;
    }

    public String createUser(String userString) throws IOException, UserAlreadyExistsException {
        KeycloakAdminConfig.mapper.readValue(userString, UserRepresentation.class);
        target = client.target(keycloakConfig.getKeycloakUserUri());
        response = target.request().post(Entity.entity(userString, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            return response.getHeaderString(HttpHeaders.LOCATION);
        } else {
            ErrorRepresentation error = KeycloakAdminConfig.mapper.readValue(response.readEntity(String.class), ErrorRepresentation.class);
            throw new UserAlreadyExistsException(error.getErrorMessage());
        }
    }

    public void deleteUser(String userId) throws UserNotFoundException {
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        response = target.request().delete();
        if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
            throw new UserNotFoundException("User not found");
        }
    }

    public void updateUser(String userId, String userString) throws UserNotFoundException, IOException {
        KeycloakAdminConfig.mapper.readValue(userString, UserRepresentation.class);
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        response = target.request().put(Entity.entity(userString, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
            throw new UserNotFoundException("User not found");
        }
    }

    public UserRepresentation readUser(String userId) throws IOException, UserNotFoundException {
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        response = target.request().get();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return KeycloakAdminConfig.mapper.readValue(response.readEntity(String.class), UserRepresentation.class);
        } else throw new UserNotFoundException("User not found");
    }
}
