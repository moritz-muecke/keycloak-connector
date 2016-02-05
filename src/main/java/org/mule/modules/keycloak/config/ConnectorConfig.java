package org.mule.modules.keycloak.config;

import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.param.Default;

@Configuration(friendlyName = "Configuration")
public class ConnectorConfig {

    /**
     * Keycloak Server URL
     */
    @Configurable
    @Default("http://localhost")
    private String keycloakUrl;

    /**
     * Keycloak Server Port
     */
    @Configurable
    @Default("8080")
    private int keycloakPort;

    /**
     * Keycloak administration User
     */
	@Configurable
	private String adminUser;

    /**
     * Keycloak administration User password
     */
    @Configurable
    private String adminPassword;

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getKeycloakUrl() {
		return keycloakUrl;
	}

	public void setKeycloakUrl(String keycloakUrl) {
		this.keycloakUrl = keycloakUrl;
	}

	public int getKeycloakPort() {
		return keycloakPort;
	}

	public void setKeycloakPort(int keycloakPort) {
		this.keycloakPort = keycloakPort;
	}

    
    

}