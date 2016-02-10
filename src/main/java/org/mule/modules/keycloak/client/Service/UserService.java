package org.mule.modules.keycloak.client.service;

import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.Exception.UserNotFoundException;

/**
 * Created by moritz.moeller on 05.02.2016.
 */
public interface UserService {
    void createUser(String userString);
    void updateUser(String userString);
    void deleteUser(String userId);
    UserRepresentation getUser(String userId) throws UserNotFoundException;
}
