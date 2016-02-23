package org.mule.modules.keycloak.exception;

/**
 * Created by moritz.moeller on 23.02.2016.
 */
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
