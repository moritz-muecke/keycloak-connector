package org.mule.modules.keycloak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.glassfish.jersey.client.ClientConfig;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;

import org.mule.api.annotations.lifecycle.Start;
import org.mule.modules.keycloak.client.KeycloakClient;
import org.mule.modules.keycloak.client.filter.AdminSessionFilter;
import org.mule.modules.keycloak.client.filter.EndAdminSessionFilter;
import org.mule.modules.keycloak.client.service.UserService;
import org.mule.modules.keycloak.config.ConnectorConfig;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;
import org.mule.modules.keycloak.exception.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.List;
import java.util.Map;

@Connector(name="keycloak", friendlyName="Keycloak")
public class KeycloakConnector {

    @Config
    ConnectorConfig config;

    private KeycloakClient keycloakClient;

    private final Logger logger = LoggerFactory.getLogger(KeycloakConnector.class);

    @Start
    public void init(){
        KeycloakAdminConfig keycloakConfig = new KeycloakAdminConfig(config);
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        UserService userService = new UserService(keycloakConfig, client);
        userService.registerAdminSessionFilter();
        userService.registerEndAdminSessionFilter();
        keycloakClient = new KeycloakClient(userService);
    }

    /**
     * Retrieves user from Keycloak by user ID
     * @param id The id of the user which should be returned
     * @return The JSON representation of an user
     */
    @Processor
    public Object getUserById(String id) throws ReadUserException {
        try {
            logger.debug("Read request for user with ID {} received.", id);
            return KeycloakAdminConfig.mapper.writeValueAsString(keycloakClient.readUserById(id));
        } catch (Exception e) {
            logger.debug("Read User {} failed. Reason: {}", id, e.getMessage());
            throw new ReadUserException(String.format("Retrieving User %s from Keycloak failed. Reason: %s", id, e.getMessage()));
        }
    }

    /**
     * Creates a new user on Keycloak from JSON string
     * @param jsonString The JSON representation of the user
     */
    @Processor
    public void createUserFromJson(String jsonString) throws CreateUserException {
        try {
            logger.debug("Creating user request from JSON string received");
            //TODO location has to be stored in mule message outbound property
            String location = keycloakClient.createUser(jsonString);
            logger.debug("User was created. Location: {}", location);
        } catch (Exception e) {
            logger.debug("Creating user from JSON string failed. Reason: {}", e.getMessage());
            throw new CreateUserException(String.format("Creation of user failed. Reason: %s", e.getMessage()));
        }
    }

    /**
     * Creates a new user on Keycloak from form data
     *
     * @param email User E-Mail address
     * @param username Username
     * @param firstname First name
     * @param lastname Last name
     * @param enabled Should user be enabled?
     * @param emailVerified Should the email be set to verified?
     * @param attributes Custom attribute set
     * @param realmRoles List of realm roles
     * @param clientRoles List of client roles
     * @throws CreateUserException if user creation failes
     */
    @Processor
    public void createUserFromForm(
            String email,
            String username,
            String firstname,
            String lastname,
            Boolean enabled,
            Boolean emailVerified,
            Boolean totp,
            Map<String, Object> attributes,
            List<String> realmRoles,
            List<String> clientRoles
    ) throws CreateUserException {
        try {
            logger.debug("Creating user request from form data reveived");
            //TODO location has to be stored in mule message outbound property
            logger.debug("User was created. Location:");
        } catch (Exception e) {
            logger.debug("Creating user from form data failed. Reason: {}", e.getMessage());
            throw new CreateUserException(String.format("Creation of user failed. Reason: %s", e.getMessage()));
        }
    }

    /**
     * Deletes user on Keycloak by user ID
     * @param id The id of the user which should be deleted
     */
    @Processor
    public void deleteUserById(String id) throws DeleteUserException {
        try {
            logger.debug("Delete request for user with ID {} received", id);
            keycloakClient.deleteUserById(id);
            logger.debug("User with ID was deleted {}", id);
        } catch (Exception e) {
            logger.debug("Deleting user with ID {} failed. Reason: {}", id, e.getMessage());
            throw new DeleteUserException(String.format("Deletion of user %s failed. Reason: %s", id, e.getMessage()));
        }
    }

    /**
     * Updates user on Keycloak by user ID
     * @param id The id of the user which should be updated
     * @param jsonString The JSON representation of the user
     */
    @Processor
    public void updateUserById(String id, String jsonString) throws UpdateUserException {
        try {
            logger.debug("Update request for user with id {} from JSON string received", id);
            keycloakClient.updateUserById(id, jsonString);
            logger.debug("User with id {} was updated from JSON string", id);
        } catch (Exception e) {
            logger.debug("Updating user with ID {} failed. Reason: {}", id, e.getMessage());
            throw new UpdateUserException(String.format("Update of user %s failed. Reason: %s", id, e.getMessage()));
        }
    }

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }
}