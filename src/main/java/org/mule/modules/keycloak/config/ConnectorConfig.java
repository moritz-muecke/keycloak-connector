package org.mule.modules.keycloak.config;

import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.Default;

@Configuration(friendlyName = "Configuration")
public class ConnectorConfig {

    /**
     * Keycloak Server URL
     */
    @Configurable
    @Default("http://localhost")
    @Placement(tab="General", group="General", order = 0)
    private String keycloakUrl;

    /**
     * Keycloak Server Port
     */
    @Configurable
    @Default("8080")
    @Placement(tab="General", group="General", order = 1)
    private int keycloakPort;

    /**
     * Keycloak administration User
     */
	@Configurable
    @Placement(tab="General", group="General", order = 2)
    private String adminUser;

    /**
     * Keycloak administration User password
     */
    @Configurable
    @Password
    @Placement(tab="General", group="General", order = 3)
    private String adminPassword;

    /**
     * Keycloak Realm to administrate
     */
    @Configurable
    @Placement(tab="General", group="General", order = 4)
    private String realm;

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

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