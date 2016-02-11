package org.mule.modules.keycloak.client.service;

import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.exception.UserNotFoundException;

import java.io.IOException;

/**
 * Created by moritz.moeller on 05.02.2016.
 */
public interface UserService {
    UserRepresentation createUser(String userString);
    UserRepresentation updateUser(String userString);
    void deleteUser(String userId);
    UserRepresentation getUser(String userId) throws UserNotFoundException, IOException;
}
