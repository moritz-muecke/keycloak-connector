package org.mule.modules.keycloak;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;

import org.mule.api.annotations.param.Optional;
import org.mule.modules.keycloak.config.ConnectorConfig;

@Connector(name="keycloak", friendlyName="Keycloak")
public class KeycloakConnector {

    @Config
    ConnectorConfig config;

    /**
     * Creates a user from json
     */
    @Processor
    public void createUserFromPayload() {
        
    }

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

}