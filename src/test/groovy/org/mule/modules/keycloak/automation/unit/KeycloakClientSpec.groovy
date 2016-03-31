package org.mule.modules.keycloak.automation.unit

import org.keycloak.representations.idm.UserRepresentation
import org.mule.modules.keycloak.client.KeycloakClient
import org.mule.modules.keycloak.client.service.UserService
import org.mule.modules.keycloak.exception.UserAlreadyExistsException
import org.mule.modules.keycloak.exception.UserNotFoundException
import spock.lang.Specification

/**
 * Test specification for the KeycloakClient
 *
 * @author Moritz Möller, AOE GmbH
 *
 */
class KeycloakClientSpec extends Specification {

    UserService userService = Mock(UserService)
    KeycloakClient client = new KeycloakClient(userService)


    def "call user-service to create a user and retrieve the location path"(){
        given:
        def location = "/users/abc123"
        def payload = '{"username":"test" , "email":"test@test.de"}'
        userService.createUser(payload) >> location

        when:
        client.createUser(payload)

        then:
        1 * userService.createUser(payload)
        client.createUser(payload) == location
    }

    def "call user-service to create a user with password resetand retrieve the location path"() {
        given:
        def location = "/users/abc123"
        def payload = '{"username":"test","email":"test@test.de","credentials":[{"type":"password","value":"password","temporary": false}]}'

        when:
        client.createUser(payload)

        then:
        1 * userService.createUser(payload) >> location
        1 * userService.resetUserPassword(_, "abc123")
    }

    def "call user-service to create a user and and throw UserAlreadyExistsException"(){
        given:
        def payload = '{"username":"test" , "email":"test@test.de"}'
        userService.createUser(payload) >> { throw new UserAlreadyExistsException("User already exists") }

        when:
        client.createUser(payload)

        then:
        UserAlreadyExistsException exception = thrown()
        exception.message == "User already exists"
    }

    def "call user-service to retrieve user by id"() {
        given:
        def userId = "abc123"
        def user = new UserRepresentation()
        user.setId(userId)
        userService.readUser(userId) >> user
        userService.readUser("wrongId") >> { throw new UserNotFoundException("User does not exist") }

        when:
        client.readUserById(userId)
        client.readUserById("wrongId")

        then:
        1 * userService.readUser(userId)
        client.readUserById(userId) == user
        UserNotFoundException exception = thrown()
        exception.message == "User does not exist"
    }

    def "call user-service to retrieve user and throw UserNotFoundException"() {
        given:
        def userId = "def456"
        userService.readUser(userId) >> { throw new UserNotFoundException("User does not exist") }

        when:
        client.readUserById(userId)

        then:
        UserNotFoundException exception = thrown()
        exception.message == "User does not exist"
    }

    def "call user-service to delete user"() {
        given:
        def userId = "abc123"

        when:
        client.deleteUserById(userId)

        then:
        1 * userService.deleteUser(userId)
    }

    def "call user-service to delete user and throw UserNotFoundException"(){
        given:
        def userId = "def456"
        userService.deleteUser(userId) >> { throw new UserNotFoundException("User does not exist") }

        when:
        client.deleteUserById(userId)

        then:
        UserNotFoundException exception = thrown()
        exception.message == "User does not exist"
    }

    def "call user-service to update a user"(){
        given:
        def userId = "abc123"
        def payload = '{"username":"test" , "email":"test@test.de"}'

        when:
        client.updateUserById(userId, payload)

        then:
        1 * userService.updateUser(userId, payload)
    }

    def "call user-service to update a user and throw UserNotFoundException"(){
        given:
        def userId = "abc123"
        def payload = '{"username":"test" , "email":"test@test.de"}'
        userService.updateUser(userId, payload) >> { throw new UserNotFoundException("User does not exist") }

        when:
        client.updateUserById(userId, payload)

        then:
        UserNotFoundException exception = thrown()
        exception.message == "User does not exist"
    }
}