# Exercise 3 - Add functionality to Event Handlers: Part 1- Use SAP Cloud SDK

In this exercise, we will look at adapting `RegistrationServiceHandler` ([RegistrationServiceHandler.java](../../srv/src/main/java/com/sap/cloud/sdk/demo/ad266/remote/RegistrationServiceHandler.java)) to add functionality to our application.

Let us outline the scenario we want to build.

// TODO maybe move this to exercise 0 or 1 and combine with starting exercise of IN260

We want a user to be able to sign up for an event and when they do, the following things should happen:
1. The user should get registered for the event.
2. A learning goal should be automatically created for them in SuccessFactors.
3. Any subsequent sessions that a user signs up for should also be registered and added as sub-goals to the goal.

The `SignupHandler` is the entry point of the application. And `signUp` is the action that will be called when a user signs up for an event or a session.

`RegistrationServiceHandler` would take care of registering the user. We would be interacting with a synthetic OpenAPI service to achieve this.
In this exercise, we will learn how you can leverage the SAP Cloud SDK to consume a remote OpenAPI service.

## Exercise 3.1 - Familiarising yourself with the remote OpenAPI service

 The OpenAPI service is available at `https://ad266-registration.cfapps.eu10-004.hana.ondemand.com`. For the sake of simplicity, we will assume that you don't have to authenticate yourself to access the service.

1. [ ] Head to https://ad266-registration.cfapps.eu10-004.hana.ondemand.com/api-docs and explore the OpenAPI specification of the service.

The most important endpoints, that we will be consuming in our application are:
   1. `/events`: Lists all the available events.
   2. `/events/{eventId}/register`: Allows you to register for an event.
   3. `/events/{eventId}/sessions/{sessionId}/register`: Allows you to register for a session.

Next, we will use the SAP Cloud SDK to consume this remote OpenAPI service.

## Exercise 3.2 - Add SAP Cloud SDK to your project and generate a typed OpenAPI client

In order to connect to the remote OpenAPI service we will generate a [typed OpenAPI client](https://sap.github.io/cloud-sdk/docs/java/v5/features/rest/overview).

//TODO adjust initial branch for these depenndency additions

- [ ] Head to the `<plugin>` section of the `srv/pom.xml` file and add the following plugin configuration:

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

This maven plugin will generate a set of classes into the `<outputDirectory>`.
Those classes can then be used to build and execute HTTP requests against the registration service.

Take note of the parameters in the `<configuration>` section above:

- `<inputSpec>`: This points to the OpenAPI specification of the remote service which is already included under `external/registration.json` in your project.
- `<outputDirectory>`: The output directory is the directory where the generated classes will be placed. We are using the `src/gen/java` directory of the project to indicate those are generated classes.
- `<apiPackage>` and `<modelPackage>`: The package names for the generated classes.
The input specification file is the OpenAPI specification of the remote service and is already available under `external/registration.json` in your project.

> **Tip**: You can find more details about the plugin parameters [here](https://sap.github.io/cloud-sdk/docs/java/v5/features/rest/generate-rest-client#available-parameters).

Next, we have to add some dependencies to the project to ensure these generated classes can be compiled and used.

- [ ] Add the following Cloud SDK dependencies to the dependency section of your `srv/pom.xml` file:
   
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

> **Tip:** We don't need to specify a `<version>` here, because we are already managing the versions of all relevant dependencies via a set of BOMs in the `<dependencyManagement>` section in the root `pom.xml` file.

Now the project is ready to be built.

- [ ] Compile the application using `mvn compile`.
 
You should see the generated classes under the new `srv/src/gen/java/cloudsdk.gen.signupservice` directory.

In order for the IDE to recognise the new directory as source code we need to mark it as such.

- [ ] For the IntelliJ IDE: right-click the directory `srv/src/gen/java` and select `Mark Directory as` -> `Generated Sources Root`.

> **Tip:** The generated sources are excluded from Git by the current `.gitignore` file.
> Generally this is typically a matter of preference and may also depend on how you set up the CI/CD of your project.

In the next step we will use the generated client to  write and run queries for the remote OpenAPI service.

## Exercise 3.3 - Use typed client to consume remote OpenAPI service

// TODO further refine this exercise

1. Let's start using the generated client in the `RegistrationServiceHandler` and first try to fetch a destination for the remote OpenAPI service.
   Replace the contents for `getDestination()` method with the following code: 
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

4. Finally, use the generated client to register for a session in the TechEd event.   
   
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

## Exercise 3.4 - Run your application locally

1. To run your application, you need to first define a destination.

2. Create a destination environment variable in your terminal using:
    ```bash
    set destinations=[{name: "Signup-Service", url: "https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/"}]
    ```
   Remember to use the same name as the destination name that is defined in the `RegistrationServiceHandler`.

3. Now run the application with `mvn spring-boot:run`

4. You can run `http://localhost:8080/rest/v1/getTechEdEvent` in your browser to see if the TechEd event details are fetched from the remote OpenAPI service correctly.
   (This should return the same results as `https://ad266-signup.cfapps.eu10-004.hana.ondemand.com/events/1`)

5. We will not test the endpoints for registering for TechEd or a session in TechEd now, but will do this is the subsequent exercises.

## Summary

You've now successfully learned how to use the SAP Cloud SDK to consume a remote OpenAPI service in a type safe manner.

Continue to - [Exercise 4 - Add functionality to Event Handlers: Part 2- Use CAP Remote Services](../ex4/README.md)
