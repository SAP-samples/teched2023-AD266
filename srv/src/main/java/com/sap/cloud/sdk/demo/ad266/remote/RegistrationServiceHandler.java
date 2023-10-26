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
        return DestinationAccessor.getDestination("Signup-Service");
    }

    public void signUpForTechEd() {
        var event = getTechEdEvent();
        var api = new EventRegistrationApi(getDestination());
        api.registerForEvent(event.getId());
    }

    public void signUpForSession(String sessionName) {
        var event = getTechEdEvent();

        var api = new EventRegistrationApi(getDestination());
        var session = api.getSessions(event.getId())
                .stream()
                .filter(s -> s.getTitle().equalsIgnoreCase(sessionName))
                .findFirst()
                .orElseThrow();

        api.registerForSession(event.getId(), session.getId());
    }

    @GetMapping( path = "/rest/v1/getTechEdEvent", produces = "application/json")
    public Event getTechEdEvent() {
        var api = new EventRegistrationApi(getDestination());
        return api.getEvents()
                .stream()
                .filter(e -> e.getName().equals("TechEd 2023"))
                .findFirst()
                .orElseThrow();
    }
}
