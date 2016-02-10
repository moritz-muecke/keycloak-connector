package org.mule.modules.keycloak.automation.unit

import org.keycloak.representations.idm.UserRepresentation
import org.mule.modules.keycloak.Exception.UserNotFoundException
import org.mule.modules.keycloak.client.KeycloakClient
import org.mule.modules.keycloak.client.service.UserService
import org.mule.modules.keycloak.config.ConnectorConfig
import spock.lang.Specification

/**
 * Created by moritz.moeller on 10.02.2016.
 */

class KeycloakClientSpec extends Specification {
    UserService userService = Mock(UserService)
    ConnectorConfig config = Mock(ConnectorConfig)
    KeycloakClient client = new KeycloakClient(config)

    def setup(){
        client.setUserService(userService)
    }

    def "should call user-service to create a new user from json string"(){
        given:
        def payload = '{"username":"test" , "email":"test@test.de"}'

        when:
        client.createUserFromPayload(payload)

        then:
        1 * userService.createUser(payload)
    }

    def "should call user-service and retrieve a user by id"() throws Exception{
        given:
        def existingId = "abc123"
        def nonExistingId = "def456"
        UserRepresentation user = new UserRepresentation()
        user.setId(existingId)

        userService.getUser(existingId) >> user
        userService.getUser(nonExistingId) >> { throw new UserNotFoundException("User does not exist") }

        when:
        client.getUserById(existingId)
        client.getUserById(nonExistingId)

        then:
        1 * userService.getUser(existingId) >> user
        UserNotFoundException exception = thrown()
        1 * userService.getUser(nonExistingId) >> { throw new UserNotFoundException("User does not exist") }
    }
}