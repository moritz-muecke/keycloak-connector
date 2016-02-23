package org.mule.modules.keycloak.client;

import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.exception.UserAlreadyExistsException;
import org.mule.modules.keycloak.exception.UserNotFoundException;
import org.mule.modules.keycloak.client.service.UserService;
import org.mule.modules.keycloak.client.service.UserServiceV18;
import org.mule.modules.keycloak.config.ConnectorConfig;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;

import java.io.IOException;

/**
 * Created by moritz.moeller on 10.02.2016.
 */
public class KeycloakClient {
    private UserService userService;

    public KeycloakClient(ConnectorConfig config) {
        KeycloakAdminConfig keycloakConfig = new KeycloakAdminConfig(config);
        this.userService = new UserServiceV18(keycloakConfig);
    }

    public String createUser(String payload) throws IOException, UserAlreadyExistsException {
        return userService.createUser(payload);
    }

    public UserRepresentation getUserById(String id) throws UserNotFoundException, IOException {
        return userService.getUser(id);
    }

    public void deleteUserById(String id) throws UserNotFoundException {
        userService.deleteUser(id);
    }

    public void editUserById(String id, String jsonString) throws UserNotFoundException, IOException {
        userService.editUser(id, jsonString);
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
