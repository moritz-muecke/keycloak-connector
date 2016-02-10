
package org.mule.modules.keycloak.generated.adapters;

import javax.annotation.Generated;
import org.mule.api.devkit.capability.Capabilities;
import org.mule.api.devkit.capability.ModuleCapability;
import org.mule.modules.keycloak.KeycloakConnector;


/**
 * A <code>KeycloakConnectorCapabilitiesAdapter</code> is a wrapper around {@link KeycloakConnector } that implements {@link org.mule.api.Capabilities} interface.
 * 
 */
@SuppressWarnings("all")
@Generated(value = "Mule DevKit Version 3.8.0", date = "2016-02-10T03:00:36+01:00", comments = "Build UNNAMED.2762.e3b1307")
public class KeycloakConnectorCapabilitiesAdapter
    extends KeycloakConnector
    implements Capabilities
{


    /**
     * Returns true if this module implements such capability
     * 
     */
    public boolean isCapableOf(ModuleCapability capability) {
        if (capability == ModuleCapability.LIFECYCLE_CAPABLE) {
            return true;
        }
        return false;
    }

}
