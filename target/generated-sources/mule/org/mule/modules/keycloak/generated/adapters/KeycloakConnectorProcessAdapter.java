
package org.mule.modules.keycloak.generated.adapters;

import javax.annotation.Generated;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.devkit.ProcessAdapter;
import org.mule.api.devkit.ProcessTemplate;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.filter.Filter;
import org.mule.modules.keycloak.KeycloakConnector;
import org.mule.security.oauth.callback.ProcessCallback;


/**
 * A <code>KeycloakConnectorProcessAdapter</code> is a wrapper around {@link KeycloakConnector } that enables custom processing strategies.
 * 
 */
@SuppressWarnings("all")
@Generated(value = "Mule DevKit Version 3.8.0", date = "2016-02-05T03:04:00+01:00", comments = "Build UNNAMED.2762.e3b1307")
public class KeycloakConnectorProcessAdapter
    extends KeycloakConnectorLifecycleInjectionAdapter
    implements ProcessAdapter<KeycloakConnectorCapabilitiesAdapter>
{


    public<P >ProcessTemplate<P, KeycloakConnectorCapabilitiesAdapter> getProcessTemplate() {
        final KeycloakConnectorCapabilitiesAdapter object = this;
        return new ProcessTemplate<P,KeycloakConnectorCapabilitiesAdapter>() {


            @Override
            public P execute(ProcessCallback<P, KeycloakConnectorCapabilitiesAdapter> processCallback, MessageProcessor messageProcessor, MuleEvent event)
                throws Exception
            {
                return processCallback.process(object);
            }

            @Override
            public P execute(ProcessCallback<P, KeycloakConnectorCapabilitiesAdapter> processCallback, Filter filter, MuleMessage message)
                throws Exception
            {
                return processCallback.process(object);
            }

        }
        ;
    }

}
