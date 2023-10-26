# Exercise 4 - Add functionality to Event Handlers: Part 2- Use CAP Remote Services

In this exercise, we will look at adapting the `GoalServiceHandler` to add functionality to our application.

Let us outline the scenario we want to build. 

We want a user to be able to sign up for a conference and when they do so the following things should happen:
1. The user should get registered for the conference
2. A learning goal should be automatically created for them in SuccessFactors.
3. Any subsequent sessions that a user signs up for should also be registered and added as sub-goals to the goal.

The `SignupHandler` is the entry point of the application. And `signUp` is the action that will be called when a user signs up for a conference or a session.

`RegistrationServiceHandler` would take care of registering the user. We would be interacting with a synthetic OpenAPI service to achieve this.

`GoalServiceHandler` would be the Event handler that would be attached to the `GoalService`. 
This class would use remote services to create a goal in SuccessFactors and add sub-goals to it. 

Let's enhance the `GoalServiceHandler` first.

## Exercise 4.1 - Add Business Logic to GoalServiceHandler

1. Open the file `srv/src/main/java/com/sap/cloud/samples/successfactors/SignupHandler.java` in your IDE. Let's add logic to the `signUp` method.

2. We want to create a goal for the user in SuccessFactors if a goal doesn't already exist.

3. And also add each session the user registers for as a task to the goal.

## Exercise 4.2 - Add Business Logic to SignupHandler

1. Open the file `srv/src/main/java/com/sap/cloud/samples/successfactors/SignupHandler.java` in your IDE. Let's add logic to the `signUp` method.

2. We want to create a goal for the user in SuccessFactors if a goal doesn't already exist. 

3. And also add each session the user registers for as a task to the goal.


## Exercise 4.3 - Run your application locally

1. To run your application, you need to first define a destination.

2. Create a destination environment variable in your terminal using:
    ```bash
    set destinations=[{name: "SFSF-BASIC-ADMIN", url: "https://apisalesdemo8.successfactors.com", "username": "USER", "password": "PASSWORD"}]
    ```
    Replace USER and PASSWORD with the credentials provided to you. 
    Remember to use the same name as the destination name that is defined in the `application.yaml` file.

3. Now run the application with `mvn spring-boot:run`

4. //Todo: Add the endpoints of SuccessFactors and demo application

## Summary

You've now successfully used CAP Remote services to consume SuccessFactors Goal Plan service. 

Continue to - [Exercise 5 - Deploying the application to SAP Business Technology Platform](../ex5/README.md)

