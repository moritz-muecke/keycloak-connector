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

    public KeycloakAdminConfig(ConnectorConfig config) {
        this.adminUser = config.getAdminUser();
        this.adminPassword = config.getAdminPassword();
        this.keycloakBaseUri = buildUri(config.getKeycloakUrl(), config.getKeycloakPort(), "/auth");
        this.keycloakUserUri = enhanceUri(this.keycloakBaseUri, String.format("/admin/realms/%s/users", config.getRealm()));
        this.keycloakAdminTokenUri = enhanceUri(this.keycloakBaseUri, "/realms/master/protocol/openid-connect/token");
        this.keycloakAdminLogoutUri = enhanceUri(this.keycloakBaseUri, "/realms/master/protocol/openid-connect/logout");
    }

    public URI buildUri(String url, int port, String path) {
        UriBuilder builder = UriBuilder
                .fromUri(url)
                .port(port)
                .path(path);
        return builder.build();
    }

    public URI enhanceUri(URI baseUri, String path) {
        UriBuilder builder = UriBuilder
                .fromUri(baseUri)
                .path(path);
        return builder.build();
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
