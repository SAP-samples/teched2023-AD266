package com.sap.cloud.sdk.demo.ad266.remote;

import cloudsdk.gen.signupservice.Event;
import cloudsdk.gen.signupservice.SignupApi;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController("/rest/v1")
public class SignupServiceHandler {

    private HttpDestination getDestination() {
        return DestinationAccessor.getDestination("Signup-Service").asHttp();
    }

    public void signUpForTechEd() {
        var event = getTechEdEvent();
        var api = new SignupApi(getDestination());
        api.eventSignup(event.getId());
    }
    public void signUpForSession(String sessionName) {
        var event = getTechEdEvent();

        var api = new SignupApi(getDestination());
        var session = api.getSessions(event.getId())
                .stream()
                .filter(s -> s.getTitle().equalsIgnoreCase(sessionName))
                .findFirst()
                .orElseThrow();

        api.sessionSignup(event.getId(), session.getId());
    }

    @GetMapping("/getTechEdEvent")
    public Event getTechEdEvent() {
        var api = new SignupApi(getDestination());
        return api.getEvents()
                .stream()
                .filter(e -> e.getName().equals("TechEd 2023"))
                .findFirst()
                .orElseThrow();
    }
}
