package com.sap.cloud.sdk.demo.ad266.remote;

import cloudsdk.gen.signupservice.Event;
import cloudsdk.gen.signupservice.EventRegistrationApi;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class RegistrationServiceHandler {

    private Destination getDestination() {
        //Todo: implement
        return null;
    }

    public void signUpForTechEd() {
        //Todo: implement
    }

    public void signUpForSession(String sessionName) {
        //Todo: implement
    }

    @GetMapping( path = "/rest/v1/getTechEdEvent", produces = "application/json")
    public Event getTechEdEvent() {
        //Todo: implement
        return null;
    }
}
