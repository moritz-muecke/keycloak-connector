package org.mule.modules.keycloak;

import org.keycloak.representations.idm.UserRepresentation;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;

import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.param.OutboundHeaders;
import org.mule.module.http.api.HttpConstants;
import org.mule.modules.keycloak.client.KeycloakClient;
import org.mule.modules.keycloak.config.ConnectorConfig;
import org.mule.modules.keycloak.config.KeycloakAdminConfig;
import org.mule.modules.keycloak.exception.UserAlreadyExistsException;
import org.mule.modules.keycloak.exception.UserNotFoundException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

@Connector(name="keycloak", friendlyName="Keycloak")
public class KeycloakConnector {

    @Config
    ConnectorConfig config;

    private KeycloakClient client;

    @Start
    public void init(){
        client = new KeycloakClient(config);
    }

    /**
     * Retrieves user from Keycloak by user ID
     * @param id The id of the user which should be returned
     */
    @Processor
    public Object getUserById(@OutboundHeaders Map<String, Object> headers, String id) {
        UserRepresentation user = null;
        try {
            user = client.getUserById(id);
            headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            return KeycloakAdminConfig.mapper.writeValueAsString(user);
        } catch (UserNotFoundException e) {
            setHttpResponseStatus(headers, Response.Status.NOT_FOUND);
            return e.getMessage();
        } catch (IOException e) {
            setHttpResponseStatus(headers, Response.Status.BAD_REQUEST);
            return e.getMessage();
        }
    }

    /**
     * Creates a new user on Keycloak
     * @param headers Injected by mule
     * @param jsonString The JSON representation of the user
     */
    @Processor
    public void createUser(@OutboundHeaders Map<String, Object> headers, String jsonString) {
        try {
            String location = client.createUser(jsonString);
            headers.put(HttpHeaders.LOCATION, location);
            setHttpResponseStatus(headers, Response.Status.CREATED);
        } catch (UserAlreadyExistsException e) {
            setHttpResponseStatus(headers, Response.Status.CONFLICT);
        } catch (IOException e) {
            setHttpResponseStatus(headers, Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Deletes user on Keycloak by user ID
     * @param headers Injected by mule
     * @param id The id of the user which should be deleted
     * @return
     */
    @Processor
    public Object deleteUserById(@OutboundHeaders Map<String, Object> headers, String id) {
        try {
            client.deleteUserById(id);
            setHttpResponseStatus(headers, Response.Status.NO_CONTENT);
            return "";
        } catch (UserNotFoundException e) {
            setHttpResponseStatus(headers, Response.Status.NOT_FOUND);
            return e.getMessage();
        }
    }

    /**
     * Edits user on Keycloak by user ID
     * @param headers Injected by mule
     * @param id The id of the user which should be edited
     * @param jsonString The JSON representation of the user
     * @return
     */
    @Processor
    public Object editUserById(@OutboundHeaders Map<String, Object> headers, String id, String jsonString) {
        try {
            client.editUserById(id, jsonString);
            setHttpResponseStatus(headers, Response.Status.NO_CONTENT);
            return "";
        } catch (UserNotFoundException e) {
            setHttpResponseStatus(headers, Response.Status.NOT_FOUND);
            return e.getMessage();
        } catch (IOException e) {
            setHttpResponseStatus(headers, Response.Status.BAD_REQUEST);
            return e.getMessage();
        }
    }

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

    private void setHttpResponseStatus(Map<String, Object> headers, Response.StatusType statusType) {
        headers.put(HttpConstants.ResponseProperties.HTTP_STATUS_PROPERTY, statusType.getStatusCode());
        headers.put(HttpConstants.ResponseProperties.HTTP_REASON_PROPERTY, statusType.getReasonPhrase());
    }

}