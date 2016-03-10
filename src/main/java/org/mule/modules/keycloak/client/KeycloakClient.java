package org.mule.modules.keycloak.client;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;
import org.mule.modules.keycloak.exception.CreateUserException;
import org.mule.modules.keycloak.exception.UserAlreadyExistsException;
import org.mule.modules.keycloak.exception.UserNotFoundException;
import org.mule.modules.keycloak.client.service.UserService;

import java.io.IOException;
import java.util.List;

/**
 * Created by moritz.moeller on 10.02.2016.
 */
public class KeycloakClient {
    private UserService userService;

    public KeycloakClient(UserService userService) {
        this.userService = userService;
    }

    public String createUser(String jsonString) throws IOException, UserAlreadyExistsException, CreateUserException, UserNotFoundException {
        UserRepresentation user = KeycloakAdminConfig.mapper.readValue(jsonString, UserRepresentation.class);
        String location = userService.createUser(jsonString);

        if (user.getCredentials() != null && !user.getCredentials().isEmpty()) {
            String userId = location.substring(location.lastIndexOf('/') + 1);
            userService.resetUserPassword(user.getCredentials(), userId);
        }

        return location;
    }

    public UserRepresentation readUserById(String id) throws UserNotFoundException, IOException {
        return userService.readUser(id);
    }

    public void deleteUserById(String id) throws UserNotFoundException {
        userService.deleteUser(id);
    }

    public void updateUserById(String id, String jsonString) throws UserNotFoundException, IOException {
        userService.updateUser(id, jsonString);
    }
}
