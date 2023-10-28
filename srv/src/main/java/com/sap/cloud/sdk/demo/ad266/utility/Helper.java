package com.sap.cloud.sdk.demo.ad266.utility;


import com.sap.cds.services.runtime.CdsRuntime;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class Helper
{
    public static final String DEMO_ID = "ID00"; // TODO replace with your demo ID

    @Autowired
    CdsRuntime cdsRuntime;

    public String getUser()
    {
        var destinationName = cdsRuntime
                .getEnvironment()
                .getCdsProperties()
                .getRemote()
                .getService("Goal")
                .getDestination()
                .getName();

        var email = DestinationAccessor.getDestination(destinationName)
                .get(DestinationProperty.BASIC_AUTH_USERNAME)
                .getOrElseThrow(() -> new NoSuchElementException("user"));

        return email.split("@")[0];
    }
}
