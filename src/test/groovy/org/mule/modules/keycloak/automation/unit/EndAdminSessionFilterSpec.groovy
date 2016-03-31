package org.mule.modules.keycloak.automation.unit

import org.keycloak.representations.AccessTokenResponse
import org.mule.modules.keycloak.client.filter.EndAdminSessionFilter
import org.mule.modules.keycloak.config.KeycloakAdminConfig
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.client.Invocation
import javax.ws.rs.client.WebTarget

/**
 * Test specification for the EndAdminSessionFilter
 *
 * @author Moritz Möller, AOE GmbH
 *
 */
class EndAdminSessionFilterSpec extends Specification{
    def adminConf = Mock(KeycloakAdminConfig)
    def endSessionFilter = new EndAdminSessionFilter(adminConf)
    def client = Mock(Client)

    def setup() {
        endSessionFilter.client = client
    }


    def "end admin session"() {
        given:
        def target = Mock(WebTarget)
        def builder = Mock(Invocation.Builder)
        def tokens = Mock(AccessTokenResponse)

        when:
        endSessionFilter.endAdminSession()

        then:
        1 * adminConf.tokens >> tokens
        1 * tokens.refreshToken >> _
        1 * adminConf.keycloakAdminLogoutUri
        1 * client.target(_) >> target
        1 * target.request() >> builder
        1 * builder.post(_)
    }
}
