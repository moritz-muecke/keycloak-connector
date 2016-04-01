package org.mule.modules.keycloak.client;

import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.client.service.UserService;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;
import org.mule.modules.keycloak.exception.CreateUserException;
import org.mule.modules.keycloak.exception.UserAlreadyExistsException;
import org.mule.modules.keycloak.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The KeycloakClient is used to access the services for the different keycloak admin api calls.
 *
 * @author Moritz Möller, AOE GmbH
 *
 */
public class KeycloakClient {

    private final Logger logger = LoggerFactory.getLogger(KeycloakClient.class);

    private UserService userService;

    public KeycloakClient(UserService userService) {
        this.userService = userService;
    }

    /**
     * Calls the user service to create a user by its json representation. If credentials are provided too, the user
     * service is called a second time to activate the user with the submitted credentials
     *
     * @param jsonString User representation
     * @return String from response, representing the location where the new user can be found
     * @throws IOException if the service can't connect to keycloak
     * @throws UserAlreadyExistsException if a user already exists
     * @throws CreateUserException if keycloak can't create the given user
     * @throws UserNotFoundException if the user could not be activated
     */
    public String createUser(String jsonString) throws
            IOException, UserAlreadyExistsException, CreateUserException, UserNotFoundException {
        UserRepresentation user = KeycloakAdminConfig.mapper.readValue(jsonString, UserRepresentation.class);
        String location = userService.createUser(jsonString);

        if (user.getCredentials() != null && !user.getCredentials().isEmpty()) {
            String userId = location.substring(location.lastIndexOf('/') + 1);
            logger.debug("Credentials were submitted, activating user {} now", userId);
            userService.activateUser(user.getCredentials(), userId);
        }

        return location;
    }

    /**
     * Calls the user service to read a user by a given id
     *
     * @param id user to be read
     * @return The UserRepresentation object
     * @throws UserNotFoundException if user can't be found
     * @throws IOException if the service can't connect to keycloak
     */
    public UserRepresentation readUserById(String id) throws UserNotFoundException, IOException {
        return userService.readUser(id);
    }

    /**
     * Calls the user service to delete a user by a given id
     *
     * @param id user to be deleted
     * @throws UserNotFoundException if the user can't be found
     */
    public void deleteUserById(String id) throws UserNotFoundException {
        userService.deleteUser(id);
    }

    /**
     * Calls the user service to update a user by given id and its representation
     *
     * @param id user to be updated
     * @param jsonString User representation
     * @throws UserNotFoundException if the user can't be found
     * @throws IOException if the service can't connect to keycloak
     */
    public void updateUserById(String id, String jsonString) throws UserNotFoundException, IOException {
        userService.updateUser(id, jsonString);
    }
}
