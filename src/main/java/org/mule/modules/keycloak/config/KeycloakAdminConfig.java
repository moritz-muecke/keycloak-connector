package org.mule.modules.keycloak.config;

import org.codehaus.jackson.map.ObjectMapper;
import org.keycloak.representations.AccessTokenResponse;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by moritz.moeller on 10.02.2016.
 */
public class KeycloakAdminConfig {
    private String adminUser;
    private String adminPassword;
    private AccessTokenResponse tokens;
    private URI keycloakBaseUri;
    private URI keycloakUserUri;
    private URI keycloakAdminTokenUri;
    private URI keycloakAdminLogoutUri;

    public static final ObjectMapper mapper = new ObjectMapper();

    private static final String ADMIN_TOKEN_ENDPOINT = "/realms/master/protocol/openid-connect/token";
    private static final String ADMIN_LOGOUT_ENDPOINT = "/realms/master/protocol/openid-connect/logout";
    private static final String USER_ENDPOINT = "/admin/realms/%s/users";
    private static final String BASIC_ENDPOINT = "/auth";

    public KeycloakAdminConfig(ConnectorConfig config) {
        this.adminUser = config.getAdminUser();
        this.adminPassword = config.getAdminPassword();
        this.keycloakBaseUri = UriBuilder
                .fromUri(config.getKeycloakUrl())
                .port(config.getKeycloakPort())
                .path(BASIC_ENDPOINT)
                .build();
        this.keycloakUserUri = UriBuilder
                .fromUri(this.keycloakBaseUri)
                .path(String.format(USER_ENDPOINT, config.getRealm()))
                .build();
        this.keycloakAdminTokenUri = UriBuilder.
                fromUri(this.keycloakBaseUri)
                .path(ADMIN_TOKEN_ENDPOINT)
                .build();
        this.keycloakAdminLogoutUri = UriBuilder
                .fromUri(this.keycloakBaseUri)
                .path(ADMIN_LOGOUT_ENDPOINT)
                .build();
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

    public URI getKeycloakBaseUri() {
        return keycloakBaseUri;
    }

    public void setKeycloakBaseUri(URI keycloakBaseUri) {
        this.keycloakBaseUri = keycloakBaseUri;
    }

    public URI getKeycloakUserUri() {
        return keycloakUserUri;
    }

    public void setKeycloakUserUri(URI keycloakUserUri) {
        this.keycloakUserUri = keycloakUserUri;
    }

    public URI getKeycloakAdminTokenUri() {
        return keycloakAdminTokenUri;
    }

    public void setKeycloakAdminTokenUri(URI keycloakAdminTokenUri) {
        this.keycloakAdminTokenUri = keycloakAdminTokenUri;
    }

    public URI getKeycloakAdminLogoutUri() {
        return keycloakAdminLogoutUri;
    }

    public void setKeycloakAdminLogoutUri(URI keycloakAdminLogoutUri) {
        this.keycloakAdminLogoutUri = keycloakAdminLogoutUri;
    }

    public AccessTokenResponse getTokens() {
        return tokens;
    }

    public void setTokens(AccessTokenResponse tokens) {
        this.tokens = tokens;
    }
}
