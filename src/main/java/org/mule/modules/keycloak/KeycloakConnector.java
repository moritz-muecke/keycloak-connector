package org.mule.modules.keycloak;

import org.glassfish.jersey.client.ClientConfig;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Email;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.param.OutboundHeaders;
import org.mule.modules.keycloak.client.KeycloakClient;
import org.mule.modules.keycloak.client.service.UserService;
import org.mule.modules.keycloak.config.ConnectorConfig;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;
import org.mule.modules.keycloak.exception.CreateUserException;
import org.mule.modules.keycloak.exception.DeleteUserException;
import org.mule.modules.keycloak.exception.ReadUserException;
import org.mule.modules.keycloak.exception.UpdateUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This connector provides endpoints to administrate user profiles of the Keycloak Single Sign On Service. It
 * retrieves admin tokens and attaches them to HTTP request to make valid API calls. The connector provides create,
 * read, update and delete (CRUD) operations for the user profiles.
 *
 * @author Moritz Möller, AOE GmbH
 *
 */
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
     * Retrieves user from Keycloak by its user ID
     *
     * {@sample.xml ../../../doc/keycloak.xml.sample keycloak:get-user-by-id}
     *
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
     *
     * {@sample.xml ../../../doc/keycloak.xml.sample keycloak:create-user-from-json}
     *
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
     * {@sample.xml ../../../doc/keycloak.xml.sample keycloak:create-user-from-form}
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
     * @throws CreateUserException if user creation fails
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
     *
     * {@sample.xml ../../../doc/keycloak.xml.sample keycloak:delete-user-by-id}
     *
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
     * Updates user on Keycloak from JSON string by user ID
     *
     * {@sample.xml ../../../doc/keycloak.xml.sample keycloak:update-user-from-json}
     *
     * @param id The id of the user which should be updated
     * @param jsonString The JSON representation of the user
     */
    @Processor
    public void updateUserFromJson(String id, String jsonString) throws UpdateUserException {
        try {
            logger.debug("Update request for user with id {} from JSON string received", id);
            keycloakClient.updateUserById(id, jsonString);
            logger.debug("User with id {} was updated from JSON string", id);
        } catch (Exception e) {
            logger.debug("Updating user with ID {} failed. Reason: {}", id, e.getMessage());
            throw new UpdateUserException(String.format("Update of user %s failed. Reason: %s", id, e.getMessage()));
        }
    }


    /**
     * /**
     * Updates user on Keycloak from form data
     *
     * {@sample.xml ../../../doc/keycloak.xml.sample keycloak:create-user-from-form}
     *
     * @param id ID of user which should be updated
     * @param username Username
     * @param email Email address of the user
     * @param firstName First name
     * @param lastName Last name
     * @param emailVerified Should the email be set to verified?
     * @param attributes Custom attribute set map (String, Object)
     * @param realmRoles List of strings with realm roles
     * @throws UpdateUserException if user update fails
     */
    @Processor
    public void updateUserFromForm(
            @Placement(tab="General", group="User data", order = 0) String id,
            @Placement(tab="General", group="User data", order = 1) String username,
            @Placement(tab="General", group="User data", order = 2) String email,
            @Placement(tab="General", group="User data", order = 4) @Optional String firstName,
            @Placement(tab="General", group="User data", order = 5) @Optional String lastName,
            @Placement(tab="General", group="User data", order = 6) @Default("false") Boolean emailVerified,
            @Placement(tab="General", group="User data", order = 8) @Optional Map<String, Object> attributes,
            @Placement(tab="General", group="User data", order = 9) @Optional List<String> realmRoles
    ) throws UpdateUserException {
        try {
            logger.debug("Update request for user with id {} from form data received", id);
            UserRepresentation user = new UserRepresentation();
            user.setId(id);
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmailVerified(emailVerified);
            user.setAttributes(attributes);
            user.setRealmRoles(realmRoles);
            keycloakClient.updateUserById(id, KeycloakAdminConfig.mapper.writeValueAsString(user));
            logger.debug("User with id {} was updated from form data", id);
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