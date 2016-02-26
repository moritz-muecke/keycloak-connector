package org.mule.modules.keycloak.client;

import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.exception.UserAlreadyExistsException;
import org.mule.modules.keycloak.exception.UserNotFoundException;
import org.mule.modules.keycloak.client.service.UserService;

import java.io.IOException;

/**
 * Created by moritz.moeller on 10.02.2016.
 */
public class KeycloakClient {
    private UserService userService;

    public KeycloakClient(UserService userService) {
        this.userService = userService;
    }

    public String createUser(String payload) throws IOException, UserAlreadyExistsException {
        return userService.createUser(payload);
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
