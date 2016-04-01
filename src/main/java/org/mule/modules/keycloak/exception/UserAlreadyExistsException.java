package org.mule.modules.keycloak.exception;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
