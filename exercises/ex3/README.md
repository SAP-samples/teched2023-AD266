# Exercise 3 - Add functionality to Event Handlers: Part 1- Use SAP Cloud SDK

In this exercise, we will look at adapting `SignupHandler` and `RegistrationServiceHandler` to add functionality to our application.

Let us outline the scenario we want to build.

We want a user to be able to sign up for an event and when they do so the following things should happen:
1. The user should get registered for the event
2. A learning goal should be automatically created for them in SuccessFactors.
3. Any subsequent sessions that a user signs up for should also be registered and added as sub-goals to the goal.

The `SignupHandler` is the entry point of the application. And `signUp` is the action that will be called when a user signs up for an event or a session.

`RegistrationServiceHandler` would take care of registering the user. We would be interacting with a synthetic OpenAPI service to achieve this.
In this exercise, we will learn how you can leverage SAP Cloud SDK to consume a remote OpenAPI service.

## Exercise 3.1 - Familiarising yourself with the remote OpenAPI Service

1. The OpenAPI service is available at `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com`.For the sake of simplicity, we will assume that you don't have to authenticate yourself to access the service. 

2. You can find all the available endpoints of the service at: `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/api-docs`

3. The most important endpoints, that we will be consuming in our application are:
   1. `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events`: Lists all the events available.
   2. `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/{eventId}/register`: Allows you to register for an event.
   3. `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/{eventId}/sessions/{sessionId}/register`: Allows you to register for a session.

## Exercise 3.2 - Add SAP Cloud SDK to your project and generate a typed OpenAPI client

1. In your project's application `pom.xml`(/srv/pom.xml) file add the following dependency to the dependencies section:
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
   Additionally, for type safe access to the remote OpenAPI service, we will generate a [typed OpenAPI client](https://sap.github.io/cloud-sdk/docs/java/v5/features/rest/overview).
   
   Add the following lines under the `<plugin>` section in your application `pom.xml` file.

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

2. Compile the application now using `mvn compile` from your IDE's terminal, you can see that under `srv/src/gen/java/cloudsdk.gen.signupservice` folder the typed OpenAPI client classes are generated. 

3. You can now use the generated client to consume the remote OpenAPI service for the next exercise.

## Exercise 3.3 - Use Typed OData client to consume remote OpenAPI service

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
        var api = new EventRegistrationApi(getDestination());   //Instantiate the generated client with the destination
        return api.getEvents()
                .stream()
                .filter(e -> e.getName().equals("TechEd 2023"))
                .findFirst()
                .orElseThrow();
    }
```
   Here we are using the generated client to fetch the TechEd event from the remote OpenAPI service. 

3. Let's now use the generated client to register for the TechEd event.
   
   Registering for TechEd: (Populate the `signUpForTechEd()` method with the following code)
```java
    public void signUpForTechEd() {
        var event = getTechEdEvent();
        var api = new EventRegistrationApi(getDestination());
        api.registerForEvent(event.getId()); //Use the generated client to register for the event
        }   
```
   Here we are using the generated client to register for the TechEd event, this piece of code would in the end call `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/{eventId}/register` end point of the service. 

4. Let's now use the generated client to register for a session in the TechEd event.   
   
   Registering for a session in TechEd: (Populate the `signUpForSession(sessionName)` method with the following code)
```java
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
```
   Here we are using the generated client to register for a session in TechEd event, this piece of code would in the end call `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/{eventId}/sessions/{sessionId}/register` end point of the service.

## Exercise 3.4 - Run the application locally

1. To run your application, you need to first define a destination.

2. Create a destination environment variable in your terminal using:
    ```bash
    set destinations=[{name: "Signup-Service", url: "https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/"}]
    ```
   Remember to use the same name as the destination name that is defined in the `RegistrationServiceHandler`.
   export destinations= '[{name: "Signup-Service", url: "https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/"}]'

3. Now run the application with `mvn spring-boot:run`

4. You can run `http://localhost:8080/rest/v1/getTechEdEvent` in your browser to see if the TechEd event details are fetched from the remote OpenAPI service correctly.
   (This should return the same results as `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/1`)

5. We will not test the endpoints for registering for TechEd or a session in TechEd now, but will do this is the subsequent exercises.

## Summary

You've now successfully learnt how to use SAP Cloud SDK to consume SuccessFactors OData service in a type safe manner.

Continue to - [Exercise 5 - Deploying the application to SAP Business Technology Platform](../ex5/README.md)
