package org.mule.modules.keycloak;

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

@Connector(name="keycloak", friendlyName="Keycloak")
public class KeycloakConnector {

    @Config
    ConnectorConfig config;

    private KeycloakClient keycloakClient;

    @Start
    public void init(){
        KeycloakAdminConfig keycloakConfig = new KeycloakAdminConfig(config);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(new AdminSessionFilter(keycloakConfig));
        clientConfig.register(new EndAdminSessionFilter(keycloakConfig));
        Client client = ClientBuilder.newClient(clientConfig);
        UserService userService = new UserService(keycloakConfig, client);
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
            return KeycloakAdminConfig.mapper.writeValueAsString(keycloakClient.readUserById(id));
        } catch (Exception e) {
            throw new ReadUserException(String.format("Retrieving User %s from Keycloak failed. Reason: %s", id, e.getMessage()));
        }
    }

    /**
     * Creates a new user on Keycloak
     * @param jsonString The JSON representation of the user
     */
    @Processor
    public void createUser(String jsonString) throws CreateUserException {
        try {
            //String location = keycloakClient.createUser(jsonString);
        	keycloakClient.createUser(jsonString);
        } catch (Exception e) {
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
            keycloakClient.deleteUserById(id);
        } catch (Exception e) {
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
            keycloakClient.updateUserById(id, jsonString);
        } catch (Exception e) {
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