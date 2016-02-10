package org.mule.modules.keycloak.client.service.v18;

import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.client.service.UserService;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;

/**
 * Created by moritz.moeller on 10.02.2016.
 */
public class UserServiceV18 implements UserService {

    private KeycloakAdminConfig config;

    public UserServiceV18(KeycloakAdminConfig config) {
        this.config = config;
    }

    @Override
    public void createUser(String userString) {
    }

    @Override
    public void updateUser(String userString) {

    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    public UserRepresentation getUser(String userId) {
        return null;
    }

    public KeycloakAdminConfig getConfig() {
        return config;
    }

    public void setConfig(KeycloakAdminConfig config) {
        this.config = config;
    }
}
