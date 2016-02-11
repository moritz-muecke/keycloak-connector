package org.mule.modules.keycloak;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;

import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.param.Optional;
import org.mule.modules.keycloak.client.KeycloakClient;
import org.mule.modules.keycloak.config.ConnectorConfig;
import org.mule.modules.keycloak.exception.UserNotFoundException;

import java.io.IOException;

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
     * Creates a user from json
     * @param id The id of the user which should be returned
     */
    @Processor
    public void getUserById(String id) throws UserNotFoundException, IOException {
        client.getUserById(id);
    }

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

}