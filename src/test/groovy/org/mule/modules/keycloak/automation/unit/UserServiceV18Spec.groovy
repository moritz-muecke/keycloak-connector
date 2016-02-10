package org.mule.modules.keycloak.automation.unit

import org.mule.modules.keycloak.client.service.UserService
import org.mule.modules.keycloak.client.service.v18.UserServiceV18
import org.mule.modules.keycloak.config.KeycloakAdminConfig
import spock.lang.Specification

/**
 * Created by moritz.moeller on 10.02.2016.
 */
class UserServiceV18Spec extends Specification{
    KeycloakAdminConfig config = Mock(KeycloakAdminConfig)
    UserService userService = new UserServiceV18(config)

}
