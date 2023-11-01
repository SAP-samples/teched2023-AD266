package com.sap.cloud.sdk.demo.ad266.remote;

/*import cloudsdk.gen.registrationservice.Event;
import cloudsdk.gen.registrationservice.EventRegistrationApi;*/
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Component
@RestController
public class RegistrationServiceHandler {

    private Destination getDestination() {
        return null;
    }

    public void signUpForTechEd() {

    }

    public void signUpForSession(String sessionName) {

    }

    /*@GetMapping( path = "/rest/v1/getTechEdEvent", produces = "application/json")
    public Event getTechEdEvent() {
        return null;
    }*/
}
