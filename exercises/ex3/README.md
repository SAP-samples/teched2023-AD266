# Exercise 3 - Add functionality to Event Handlers: Part 1- Use SAP Cloud SDK

In this exercise, we will inplement our business logic in the `RegistrationServiceHandler` class.

### Requirement
Let us outline the scenario we want to build.

An end user will sign up for an event (e.g., TechEd 2023) and the backend should handle the following:
1. The end user should get registered for the event. (OpenAPI based Registration Service consumption)
2. A learning goal (with user id) should be automatically created for them in SuccessFactors. (SuccessFactors OData v2 service consumption)
3. Any subsequent sessions (e.g., AD266) that a user signs up for should also be registered and added as sub-goals to the goal. (SuccessFactors OData v2 service consumption)

The `SignupHandler` class (please add the path) contains the implementation of the `SignupService` endpoint (OData v4 action). And `signUp` is the function that will be called when an end user signs up for an event or a session.

The `RegistrationServiceHandler` (please add the path) would take care of registering the user (`#1` in the requirement). We would be interacting with a synthetic OpenAPI service to achieve this.
In this exercise, we will learn how you can leverage SAP Cloud SDK to consume a remote OpenAPI service.

## Exercise 3.1 - Familiarise yourself with the Registration Service

1. The OpenAPI based Registration service is available at `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com`.
For the sake of simplicity, we assume that you don't have to authenticate yourself to access the service. 

2. You can find all the available endpoints of the service at: `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/api-docs`

3. The most important endpoints, that we will be consuming in our application are:
   1. List all the available events: `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events`.
   2. List all the available sessions from a given event: `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/{eventId}/sessions`.
   3. Register for an event for yourself: `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/{eventId}/register`.
   4. register for a session for yourself: `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/{eventId}/sessions/{sessionId}/register`.

## Exercise 3.2 - Add SAP Cloud SDK to your project and generate a typed OpenAPI client

1. In the CAP project `pom.xml`(`/srv/pom.xml`), you can already find the following Cloud SDK dependencies like below:
```xml
        <!-- Cloud SDK OpenAPI & Destinations -->
        <dependency>
            <groupId>com.sap.cloud.sdk.datamodel</groupId>
            <artifactId>openapi-core</artifactId>
        </dependency>
        <dependency>
            <groupId>com.sap.cloud.sdk.cloudplatform</groupId>
            <artifactId>connectivity-apache-httpclient5</artifactId>
        </dependency>
        <dependency>
            <groupId>com.sap.cloud.sdk.cloudplatform</groupId>
            <artifactId>cloudplatform-connectivity</artifactId>
        </dependency>
```
   These dependencies are required to consume a remote OpenAPI service.
   Additionally, for type safe access to the remote OpenAPI service, we will generate a [typed OpenAPI client](https://sap.github.io/cloud-sdk/docs/java/v5/features/rest/overview).
   
   You can also already find the following lines under the `<plugin>` section in the CAP project `pom.xml`(`/srv/pom.xml`) file.

```xml
            <!-- Cloud SDK OData VDM Generator -->
            <plugin>
                <groupId>com.sap.cloud.sdk.datamodel</groupId>
                <artifactId>openapi-generator-maven-plugin</artifactId>
                <version>5.0.0-SNAPSHOT</version>
                <executions>
                    <execution>
                        <id>generate-signup-service</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                        <configuration>
                            <inputSpec>${project.basedir}/external/registration.json</inputSpec>
                            <outputDirectory>${project.basedir}/src/gen/java</outputDirectory>
                            <deleteOutputDirectory>false</deleteOutputDirectory>
                            <apiPackage>cloudsdk.gen.signupservice</apiPackage>
                            <modelPackage>cloudsdk.gen.signupservice</modelPackage>
                            <compileScope>COMPILE</compileScope>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```
   
   You could find more details about the plugin parameters [here](https://sap.github.io/cloud-sdk/docs/java/v5/features/rest/generate-rest-client#available-parameters)
   The input specification file is the OpenAPI specification of the remote service and is already available under `external/registration.json` in your project.
  Note: We are managing the versions of the artifacts we imported above using the SAP Cloud SDK BOM, you can find this already added in your project's `<dependencyManagement>` section in the root `pom.xml`:
    
```xml
    <!-- Cloud SDK -->
    <dependency>
        <groupId>com.sap.cloud.sdk</groupId>
        <artifactId>sdk-modules-bom</artifactId>
        <version>5.0.0-SNAPSHOT</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
```

2. Compile the application now using `mvn compile` from which path?, you can see that under `srv/src/gen/java/cloudsdk.gen.signupservice` folder the typed OpenAPI client classes are generated. 

3. You can now use the generated client to consume the Registration Service for the next exercise.

## Exercise 3.3 - Use typed client to consume remote OpenAPI service

1. Let's start using the generated client in the `RegistrationServiceHandler` and first try to fetch a destination for the remote OpenAPI service.
   Add the following code inside the class. 
   ```java
    private Destination getDestination() {
        return DestinationAccessor.getDestination("Signup-Service");
    }
   ```
   `DestinationAccessor` is a utility class that helps in fetching destinations either from the environmental variables or from the destination service depending upon where the application is running. 
    We will create a destination with the name `Signup-Service` as an environment variable in the next exercise.

2. `EventRegistrationApi` is the generated client class that provides the type safe access to the remote OpenAPI service. Let's use it to fetch the TechEd event details.
   Add the following code inside the `getTechEdEvent()` method.
```java
    @GetMapping( path = "/rest/v1/getTechEdEvent", produces = "application/json")
    public Event getTechEdEvent() {
        var api = new EventRegistrationApi(getDestination());   // Instantiate the generated client with the destination
        return api.getEvents()                                  // GET https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events
                .stream()
                .filter(e -> e.getName().equals("TechEd 2023")) // Filter by name from client side
                .findFirst()
                .orElseThrow();
    }
```
   Here we are using the generated client to fetch the TechEd event from the Registration Service.

3. Let's now use the generated client to register for the TechEd event.
   
   Registering for TechEd: (Populate the `signUpForTechEd()` method with the following code)
```java
    public void signUpForTechEd() {
        var event = getTechEdEvent();
        var api = new EventRegistrationApi(getDestination());
        api.registerForEvent(event.getId()); // POST https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/{eventId}/register
    }   
```

4. Let's now use the generated client to register for a session in the TechEd event.   
   
   Registering for a session in TechEd: (Populate the `signUpForSession(sessionName)` method with the following code)
```java
    public void signUpForSession(String sessionName) {
        var event = getTechEdEvent();

        var api = new EventRegistrationApi(getDestination());
        var session = api.getSessions(event.getId())                 // GET https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/{eventId}/sessions
            .stream()
            .filter(s -> s.getTitle().equalsIgnoreCase(sessionName)) // Filter by name from client side
            .findFirst()
            .orElseThrow();

        api.registerForSession(event.getId(), session.getId());      // POST https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/{eventId}/sessions/{sessionId}/register
    }
```

## Exercise 3.4 - Run your application locally

1. To run your application, you need to first define a destination.

2. Create a destination environment variable in your terminal using:
    ```bash
    set destinations=[{name: "Signup-Service", url: "https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/"}]
    ```
   Remember to use the same name as the destination name that is defined in the `RegistrationServiceHandler`.

3. Now run the application with `mvn spring-boot:run` (from which path?)

4. You can run `http://localhost:8080/rest/v1/getTechEdEvent` in your browser to see if the TechEd event details are fetched from the Registration Service correctly.
   (This should return the same results as `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/1`)

5. We will not test the endpoints for registering for TechEd or a session in TechEd now, but will do this is the subsequent exercises.

## Summary

You've now successfully learnt how to use SAP Cloud SDK to consume a remote OpenAPI service (Registration Service) in a type safe manner.

Continue to - [Exercise 4 - Add functionality to Event Handlers: Part 2- Use CAP Remote Services](../ex4/README.md)
