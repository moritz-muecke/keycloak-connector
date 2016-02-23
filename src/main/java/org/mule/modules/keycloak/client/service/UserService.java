package org.mule.modules.keycloak.client.service;

import org.keycloak.representations.idm.UserRepresentation;
import org.mule.modules.keycloak.exception.UserAlreadyExistsException;
import org.mule.modules.keycloak.exception.UserNotFoundException;

import java.io.IOException;

/**
 * Created by moritz.moeller on 05.02.2016.
 */
public interface UserService {
    String createUser(String userString) throws IOException, UserAlreadyExistsException;
    UserRepresentation updateUser(String userString);
    void deleteUser(String userId) throws UserNotFoundException;
    void editUser(String userId, String userString) throws UserNotFoundException, IOException;
    UserRepresentation getUser(String userId) throws UserNotFoundException, IOException;
}
