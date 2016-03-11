package org.mule.modules.keycloak.automation.unit

import org.mule.modules.keycloak.config.ConnectorConfig
import org.mule.modules.keycloak.config.KeycloakAdminConfig
import spock.lang.Specification


/**
 * Created by moritz.moeller on 11.03.2016.
 */
class KeycloakAdminConfigSpec extends Specification {
    def uriString = "http://localhost"
    def port = 8080
    def uri = new URI(uriString)
    def path = "/test"
    def connectorConfig = Mock(ConnectorConfig)

    def setup(){
        connectorConfig.keycloakUrl >> "http://localhost"
        connectorConfig.keycloakPort >> 8080
        connectorConfig.realm >> "testRealm"
    }

    def "build uri from url, port and path"() {
        setup:
        def keycloakConfig = new KeycloakAdminConfig(connectorConfig)

        expect:
        keycloakConfig.buildUri(uriString, port, path).toString() == "http://localhost:8080/test"
    }

    def "extend Uri with path"() {
        setup:
        def keycloakConfig = new KeycloakAdminConfig(connectorConfig)

        expect:
        keycloakConfig.extendUri(uri, path).toString() == "http://localhost/test"
    }
}