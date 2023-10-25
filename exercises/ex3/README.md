# Exercise 3 - Add functionality to Event Handlers

In this exercise, we will look at adapting `SignupHandler` and `GoalServiceHandler` to add functionality to our application.

Let us outline the scenario we want to build. 

We want a user to be able to sign up for a conference and when they do so, we want a goal to be automatically created for them in SuccessFactors.
Any subsequent sessions that a user signs up for should be added as tasks to the goal.

The `SignupHandler` is the entry point of the application. And `signUp` is the action that will be called when a user signs up for a conference or a session.
`GoalServiceHandler` would be the Event handler that would be attached to the `GoalService`. 
This class would use remote services to create a goal in SuccessFactors and add tasks to it. 

## Exercise 3.1 - Add Business Logic to GoalServiceHandler


## Exercise 3.2 - Add Business Logic to SignupHandler

1. Open the file `srv/src/main/java/com/sap/cloud/samples/successfactors/SignupHandler.java` in your IDE. Let's add logic to the `signUp` method.

2. We want to create a goal for the user in SuccessFactors if a goal doesn't already exist. 

3. And also add each session the user registers for as a task to the goal.


## Exercise 3.3 - Run your application locally

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

You've now successfully added business logic to your application and tested it locally.

Continue to - [Exercise 4 - Use SAP Cloud SDK](../ex3/README.md)
