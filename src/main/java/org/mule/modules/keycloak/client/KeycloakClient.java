package org.mule.modules.keycloak.client;

import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.Exception.UserNotFoundException;
import org.mule.modules.keycloak.client.service.UserService;
import org.mule.modules.keycloak.client.service.v18.UserServiceV18;
import org.mule.modules.keycloak.config.ConnectorConfig;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;

/**
 * Created by moritz.moeller on 10.02.2016.
 */
public class KeycloakClient {
    private UserService userService;
    private ConnectorConfig config;

    public KeycloakClient(ConnectorConfig config) {
        this.config = config;
        KeycloakAdminConfig keycloakConfig = new KeycloakAdminConfig(config.getAdminUser(), config.getAdminPassword());
        this.userService = new UserServiceV18(keycloakConfig);
    }

    public void createUserFromPayload(String payload) {
        userService.createUser(payload);
    }

    public UserRepresentation getUserById(String id) throws UserNotFoundException {
        return userService.getUser(id);
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
