package org.mule.modules.keycloak.client.service;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.ErrorRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.client.filter.AdminSessionFilter;
import org.mule.modules.keycloak.client.filter.EndAdminSessionFilter;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;
import org.mule.modules.keycloak.exception.CreateUserException;
import org.mule.modules.keycloak.exception.UserAlreadyExistsException;
import org.mule.modules.keycloak.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.IOException;
import java.util.List;

/**
 * Created by moritz.moeller on 10.02.2016.
 */
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private KeycloakAdminConfig keycloakConfig;
    private Client client;
    private WebTarget target;
    private Response response;

    public UserService(KeycloakAdminConfig keycloakConfig, Client client) {
        this.keycloakConfig = keycloakConfig;
        this.client = client;
    }

    public String createUser(String userString) throws UserAlreadyExistsException, IOException, CreateUserException {
        KeycloakAdminConfig.mapper.readValue(userString, UserRepresentation.class);
        target = client.target(keycloakConfig.getKeycloakUserUri());
        logger.debug("Sending create user request to keycloak now");
        response = target.request().post(Entity.entity(userString, MediaType.APPLICATION_JSON_TYPE));
        logger.debug("Received status {} due user creation", response.getStatus());
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            return response.getHeaderString(HttpHeaders.LOCATION);
        } else if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()){
            ErrorRepresentation error = KeycloakAdminConfig.mapper
                    .readValue(response.readEntity(String.class), ErrorRepresentation.class);
            throw new UserAlreadyExistsException(error.getErrorMessage());
        } else {
            throw new CreateUserException("User creation failed with status: " + response.getStatus());
        }
    }

    public void deleteUser(String userId) throws UserNotFoundException {
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        logger.debug("Sending delete request for user {} to keycloak now", userId);
        response = target.request().delete();
        logger.debug("Received response status {} from delete request for user {}", response.getStatus(), userId);
        if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
            throw new UserNotFoundException("User not found");
        }
    }

    public void updateUser(String userId, String userString) throws UserNotFoundException, IOException {
        KeycloakAdminConfig.mapper.readValue(userString, UserRepresentation.class);
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        logger.debug("Sending update request for user {} to keycloak now", userId);
        response = target.request().put(Entity.entity(userString, MediaType.APPLICATION_JSON_TYPE));
        logger.debug("Received response status {} update request for user {}", response.getStatus(), userId);
        if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
            throw new UserNotFoundException("User not found");
        }
    }

    public UserRepresentation readUser(String userId) throws IOException, UserNotFoundException {
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        logger.debug("Sending read request for user {} to keycloak now", userId);
        response = target.request().get();
        logger.debug("Received response status {} from read request for user {}", response.getStatus(), userId);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return KeycloakAdminConfig.mapper.readValue(response.readEntity(String.class), UserRepresentation.class);
        } else throw new UserNotFoundException("User not found");
    }

    public void resetUserPassword(List<CredentialRepresentation> credentials, String userId) throws
            UserNotFoundException, CreateUserException {
        for (CredentialRepresentation cred: credentials){
            try {
                target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId).path("/reset-password");
                logger.debug("Sending reset password request to activate user {} to keycloak now", userId);
                response = target.request().put(Entity.entity(
                        KeycloakAdminConfig.mapper.writeValueAsString(cred), MediaType.APPLICATION_JSON_TYPE)
                );
                logger.debug("Received status {} from reset password request for user {}",
                        response.getStatus(), userId);
                if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) throw new Exception();
            } catch (Exception e) {
                deleteUser(userId);
                throw new CreateUserException("Password reset to activate user failed");
            }
        }
    }

    public void registerAdminSessionFilter(){
        logger.debug("Create admin session filter registered for HTTP client");
        client.register(new AdminSessionFilter(keycloakConfig));
    }

    public void registerEndAdminSessionFilter(){
        logger.debug("End admin session filter registered for HTTP client");
        client.register(new EndAdminSessionFilter(keycloakConfig));
    }
}
