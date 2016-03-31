package org.mule.modules.keycloak.automation.unit

import org.codehaus.jackson.map.ObjectMapper
import org.keycloak.representations.AccessTokenResponse
import org.mule.modules.keycloak.client.filter.AdminSessionFilter
import org.mule.modules.keycloak.config.KeycloakAdminConfig
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.client.Invocation
import javax.ws.rs.client.WebTarget

/**
 * Test specification for the AdminSessionFilter
 *
 * @author Moritz Möller, AOE GmbH
 *
 */
class AdminSessionFilterSpec extends Specification{

    def keycloakConf = Mock(KeycloakAdminConfig)
    def adminSessionFilter = new AdminSessionFilter(keycloakConf)

    def "request admin access tokens"() {
        setup:
        def mapper = Mock(ObjectMapper)
        def client = Mock(Client)
        def target = Mock(WebTarget)
        adminSessionFilter.client = client
        adminSessionFilter.mapper = mapper
        keycloakConf.keycloakAdminTokenUri >> _
        client.target(_) >> target
        def builder = Mock(Invocation.Builder)
        target.request(_) >> builder
        builder.post(_, _) >> _
        mapper.readValue(_, _) >> new AccessTokenResponse()

        expect:
        assert adminSessionFilter.requestAccessToken() instanceof AccessTokenResponse
    }


    def "request admin access tokens returns null"() {
        setup:
        def mapper = Mock(ObjectMapper)
        def client = Mock(Client)
        def target = Mock(WebTarget)
        adminSessionFilter.client = client
        adminSessionFilter.mapper = mapper
        keycloakConf.keycloakAdminTokenUri >> _
        client.target(_) >> target
        def builder = Mock(Invocation.Builder)
        target.request(_) >> builder
        builder.post(_, _) >> { throw new ConnectException("Could not connect to Keycloak")}

        expect:
        adminSessionFilter.requestAccessToken() == null
    }
}
