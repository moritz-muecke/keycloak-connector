package org.mule.modules.keycloak.automation.unit

import org.codehaus.jackson.map.exc.UnrecognizedPropertyException
import org.keycloak.representations.idm.ErrorRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.mule.modules.keycloak.client.service.UserService
import org.mule.modules.keycloak.config.KeycloakAdminConfig
import org.mule.modules.keycloak.exception.UserAlreadyExistsException
import org.mule.modules.keycloak.exception.UserNotFoundException
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.client.Invocation
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response

/**
 * Created by moritz.moeller on 10.02.2016.
 */
class UserServiceSpec extends Specification{
    def config = Mock(KeycloakAdminConfig)
    def client = Mock(Client)
    def target = Mock(WebTarget)
    def response = Mock(Response)
    def builder = Mock(Invocation.Builder)
    def userService = new UserService(config, client)
    def validUserJson = '{"username" : "test", "email" : "test@test.de"}'
    def invalidUserJson = '{"name" : "test", "mail" : "test@test.de"}'


    def setup() {
        client.target(_) >> target
        target.request() >> builder
        target.request(_) >> builder
        target.path(_) >> target
    }

    def "read user from keycloak"() {
        setup:
        def userId = "abc123"
        builder.get(String.class) >> validUserJson

        expect:
        userService.readUser(userId).getUsername() == "test"
        userService.readUser(userId).getEmail() == "test@test.de"
    }


    def "read user from keycloak throws IOException"() {
        given:
        def userId = "abc123"

        when:
        userService.readUser(userId)

        then:
        1 * builder.get(String.class) >> invalidUserJson
        IOException exception = thrown()
        exception.message.contains("Unrecognized field")
    }


    def "create user on keycloak"() {
        setup:
        def location = "/users/abc123"
        builder.post(_) >> response
        response.getStatus() >> Response.Status.CREATED.getStatusCode()
        response.getHeaderString(HttpHeaders.LOCATION) >> location

        expect:
        userService.createUser(validUserJson) == location
    }

    def "create user on keycloak throws UserAlreadyExistsException"() {
        given:
        def errorJson = '{"errorMessage" : "User already exists"}'

        when:
        userService.createUser(validUserJson)

        then:
        1 * builder.post(_) >> response
        1 * response.getStatus() >> Response.Status.CONFLICT.getStatusCode()
        1 * response.readEntity(String.class) >> errorJson
        UserAlreadyExistsException exception = thrown()
        exception.message == "User already exists"
    }


    def "create user on keycloak throws IOException"() {
        when:
        userService.createUser(invalidUserJson)

        then:
        IOException exception = thrown()
        exception.message.contains("Unrecognized field")
    }

    def "update user on keycloak"() {
        when:
        userService.updateUser("abc123", validUserJson)

        then:
        1 * builder.put(_) >> response
        1 * response.getStatus() >> Response.Status.NO_CONTENT.getStatusCode()
    }

    def "update user on keycloak throws UserNotFoundException"() {
        when:
        userService.updateUser("def456", validUserJson)

        then:
        1 * builder.put(_) >> response
        1 * response.getStatus() >> Response.Status.NOT_FOUND.getStatusCode()
        UserNotFoundException exception = thrown()
        exception.message == "User not found"
    }

    def "update user on keycloak throws IOException"() {
        when:
        userService.updateUser("abc123", invalidUserJson)

        then:
        IOException exception = thrown()
        exception.message.contains("Unrecognized field")
    }

    def "delete user from keycloak"() {
        given:
        def userId = "abc123"

        when:
        userService.deleteUser(userId)

        then:
        1 * builder.delete() >> response
        1 * response.getStatus() >> Response.Status.NO_CONTENT.getStatusCode()
    }

    def "delete user from keycloak throws UserNotFoundException"() {
        given:
        def userId = "abc123"

        when:
        userService.deleteUser(userId)

        then:
        1 * builder.delete() >> response
        1 * response.getStatus() >> Response.Status.NOT_FOUND.getStatusCode()
        UserNotFoundException exception = thrown()
        exception.message == "User not found"
    }

}