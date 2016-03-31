package org.mule.modules.keycloak;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Email;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.param.OutboundHeaders;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Connector(name = "keycloak", friendlyName = "Keycloak", minMuleVersion = "3.5", description = "Keycloak Connector")
public class KeycloakConnector {

    @Config
    ConnectorConfig config;

    private KeycloakClient keycloakClient;

    private final Logger logger = LoggerFactory.getLogger(KeycloakConnector.class);

    @Start
    public void init(){
        logger.debug("Initializing Keycloak-Admin-Connector");
        KeycloakAdminConfig keycloakConfig = new KeycloakAdminConfig(config);
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        UserService userService = new UserService(keycloakConfig, client);
        userService.registerAdminSessionFilter();
        userService.registerEndAdminSessionFilter();
        logger.debug("Instantiating Client");
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
            throw new ReadUserException(
                    String.format("Retrieving User %s from Keycloak failed. Reason: %s", id, e.getMessage())
            );
        }
    }

    /**
     * Creates a new user on Keycloak from JSON string
     * @param jsonString The JSON representation of the user
     * @param outboundHeaders Outbound properties of the current mule message
     */
    @Processor
    public void createUserFromJson(String jsonString, @OutboundHeaders Map<String, Object> outboundHeaders) throws
            CreateUserException {
        try {
            logger.debug("Creating user request from JSON string");
            String location = keycloakClient.createUser(jsonString);
            outboundHeaders.put("Location", location);
            logger.debug("User was created. Location: {}", location);
        } catch (Exception e) {
            logger.debug("Creating user from JSON string failed. Reason: {}", e.getMessage());
            throw new CreateUserException(String.format("Creation of user failed. Reason: %s", e.getMessage()));
        }
    }

    /**
     * /**
     * Creates a new user on Keycloak from form data
     *
     * @param email User E-Mail address
     * @param username Username
     * @param firstName First name
     * @param lastName Last name
     * @param enabled Should user be enabled?
     * @param emailVerified Should the email be set to verified?
     * @param totp Time-based One-time Password
     * @param attributes Custom attribute set map (String, Object)
     * @param realmRoles List of strings with realm roles
     * @param outboundHeaders Outbound properties of the current mule message
     * @throws CreateUserException if user creation failes
     */
    @Processor
    public void createUserFromForm(
            @Placement(tab="General", group="User data", order = 0) @Email String email,
            @Placement(tab="General", group="User data", order = 1) String username,
            @Placement(tab="General", group="User data", order = 2) @Optional String password,
            @Placement(tab="General", group="User data", order = 3) @Optional String firstName,
            @Placement(tab="General", group="User data", order = 4) @Optional String lastName,
            @Placement(tab="General", group="User data", order = 5) @Default("false") Boolean enabled,
            @Placement(tab="General", group="User data", order = 6) @Default("false") Boolean emailVerified,
            @Placement(tab="General", group="User data", order = 7) @Default("false") Boolean totp,
            @Placement(tab="General", group="User data", order = 8) @Optional Map<String, Object> attributes,
            @Placement(tab="General", group="User data", order = 9) @Optional List<String> realmRoles,
            @OutboundHeaders Map<String, Object> outboundHeaders
    ) throws CreateUserException {
        try {
            logger.debug("Creating user request from form data");
            UserRepresentation user = new UserRepresentation();
            user.setEmail(email);
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(enabled);
            user.setEmailVerified(emailVerified);
            user.setTotp(totp);
            user.setAttributes(attributes);
            user.setRealmRoles(realmRoles);
            if (password != null) {
                CredentialRepresentation cred = new CredentialRepresentation();
                if (totp) {
                    cred.setType(CredentialRepresentation.TOTP);
                } else cred.setType(CredentialRepresentation.PASSWORD);
                cred.setValue(password);
                List<CredentialRepresentation> creds = new ArrayList<>();
                creds.add(cred);
                user.setCredentials(creds);
            }
            String location = keycloakClient.createUser(KeycloakAdminConfig.mapper.writeValueAsString(user));
            outboundHeaders.put("Location", location);
            logger.debug("User was created. Location: {}", location);
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