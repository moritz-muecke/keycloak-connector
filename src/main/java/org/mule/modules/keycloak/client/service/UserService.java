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
import java.io.IOException;
import java.util.List;

/**
 * This class is used to call the endpoints of the Keycloak admin API to execute CRUD operations on user profiles
 *
 * @author Moritz Möller, AOE GmbH
 * @see <a href="http://keycloak.github.io/docs/rest-api/">Keycloak Admin API</a>
 *
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

    /**
     * Takes a string which contains the JSON representation of a keycloak user profile and sends a http post request
     * to keycloak to create this user.
     *
     * @param userString JSON user representation as string
     * @return The location of the created user
     * @throws UserAlreadyExistsException if user already exists
     * @throws IOException if there are any connection problems
     * @throws CreateUserException if the response from keycloak contains an error
     */
    public String createUser(String userString) throws UserAlreadyExistsException, IOException, CreateUserException {
        KeycloakAdminConfig.mapper.readValue(userString, UserRepresentation.class);
        target = client.target(keycloakConfig.getKeycloakUserUri());
        logger.debug("Sending create user request to keycloak now. Request content: {}", userString);
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

    /**
     * Takes the user id and sends a http delete request to keycloak to delete the user linked to the id
     *
     * @param userId User to be deleted
     * @throws UserNotFoundException if user can't be found
     */
    public void deleteUser(String userId) throws UserNotFoundException {
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        logger.debug("Sending delete request for user {} to keycloak now", userId);
        response = target.request().delete();
        logger.debug("Received response status {} from delete request for user {}", response.getStatus(), userId);
        if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
            throw new UserNotFoundException("User not found");
        }
    }

    /**
     * Takes a string which contains the JSON representation of a keycloak user profile and the id of a user to
     * send a http put request to keycloak update the user of the given id
     *
     * @param userId User which should be updated
     * @param userString JSON user representation as string
     * @throws UserNotFoundException if user can't be found
     * @throws IOException if there are any connection problems
     */
    public void updateUser(String userId, String userString) throws UserNotFoundException, IOException {
        KeycloakAdminConfig.mapper.readValue(userString, UserRepresentation.class);
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        logger.debug("Sending update request for user {} to keycloak now", userId);
        response = target.request().put(Entity.entity(userString, MediaType.APPLICATION_JSON_TYPE));
        logger.debug("Received response status {} update request for user {}", response.getStatus(), userId);
        if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
            logger.debug("Updating User {} failed. Status: {} - Message: {}", userId, response.getStatus(), response
                    .readEntity(String.class));
            throw new UserNotFoundException("User not found");
        }
    }

    /**
     * Takes a user id and sends a http get request to keycloak to read the user linked to the id
     *
     * @param userId to be read
     * @return UserRepresentation object
     * @throws UserNotFoundException if user can't be found
     * @throws IOException if there are any connection problems
     */
    public UserRepresentation readUser(String userId) throws IOException, UserNotFoundException {
        target = client.target(keycloakConfig.getKeycloakUserUri()).path(userId);
        logger.debug("Sending read request for user {} to keycloak now", userId);
        response = target.request().get();
        logger.debug("Received response status {} from read request for user {}", response.getStatus(), userId);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return KeycloakAdminConfig.mapper.readValue(response.readEntity(String.class), UserRepresentation.class);
        } else throw new UserNotFoundException("User not found");
    }

    /**
     * Takes a list of credentials and a user id to send a http put request to keycloak to reset the password of the
     * user from the given id. This is used to activate a recent created user. If this leads to an exception the
     * recently created user will be deleted
     *
     * @param credentials List of credentials
     * @param userId ID of the user where password reset should be performed
     * @throws UserNotFoundException if user can't be found
     * @throws CreateUserException if the user could not be created
     */
    public void activateUser(List<CredentialRepresentation> credentials, String userId) throws
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

    /**
     * Registers the admin session filter to all requests send by the http client
     */
    public void registerAdminSessionFilter(){
        logger.debug("Create admin session filter registered for HTTP client");
        client.register(new AdminSessionFilter(keycloakConfig));
    }

    /**
     * Registers the end admin session filter to all responses received by the http client
     */
    public void registerEndAdminSessionFilter(){
        logger.debug("End admin session filter registered for HTTP client");
        client.register(new EndAdminSessionFilter(keycloakConfig));
    }
}
