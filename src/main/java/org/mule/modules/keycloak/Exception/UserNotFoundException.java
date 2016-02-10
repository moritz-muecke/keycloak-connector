package org.mule.modules.keycloak.Exception;

/**
 * Created by moritz.moeller on 10.02.2016.
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
