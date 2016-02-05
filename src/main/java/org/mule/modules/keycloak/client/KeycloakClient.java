package org.mule.modules.keycloak.client;

import org.keycloak.representations.idm.UserRepresentation;

/**
 * Created by moritz.moeller on 05.02.2016.
 */
public interface KeycloakClient {
    void createUser(String userString);
    void updateUser(String userString);
    void deleteUser(String userId);
    UserRepresentation getUser(String userId);
}
