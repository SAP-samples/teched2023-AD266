# Exercise ? - Use SAP Cloud SDK to make your application resilient

In this exercise, we will learn about SAP Cloud SDK offering for various resilience patterns to make your application more robust.
Applying patterns like the following will help to make your code more resilient against failures it might encounter:
* Time Limiter
* Retry
* Rate Limiter
* Circuit Breaker
* Bulkhead

For now, we'll be focussing on the first two patterns in this exercise.

### Motivation

The exercise explores optional code improvements.
The benefits may not be directly visible, but they are important for a productive application runtime.
If your application serves multiple tenants or principals, or if it interacts with a multitude of external systems, then it will probably receive unbalanced computational load - depending on the context.
You will want to avoid the situation where one operation with error-prone context disrupts the session for other customers and users, or worst case - brings the whole application to a halt.

In order to protect yourself from this, you can use the resilience patterns provided by the SAP Cloud SDK.

### Prerequisites

* You can start the application locally with `mvn spring-boot:run` or by running/debugging the `Application` class.
* You can access the running application frontend at `http://localhost:8080` and it displays a list of sessions.

## Exercise ?.1 - Add the required dependencies to your project

1. In your project's application `pom.xml`(/srv/pom.xml) file add the following dependencies to the dependencies section:
    ```xml
     <!-- SAP Cloud SDK Resilience -->
     <dependency>
         <groupId>com.sap.cloud.sdk.cloudplatform</groupId>
         <artifactId>resilience</artifactId>
     </dependency>
     <dependency>
         <groupId>com.sap.cloud.sdk.frameworks</groupId>
         <artifactId>resilience4j</artifactId>
     </dependency>
    ```
   While the first dependency offers the API to interact with SAP Cloud SDK, the second one actually provides the internal implementation of the resilience patterns.

## Exercise ?.2 - Use the Resilience API

Imagine, the interaction with SAP SuccessFactors is not very reliable, maybe it loads too long without any result.
To improve user experience in the web application, it would be worthwhile to interrupt the lasting request and show a message to the user. 

1. Locate the `Application` class in your application.
2. Extend the `main` method:
   ```diff
   + ResilienceDecorator.setDecorationStrategy(new Resilience4jDecorationStrategy());
   ```
   This will activate the correct resilience decorator for the whole application.
3. Locate the `SignupHandler` class in your application.
4. Declare a static field on the class to hold the resilience configuration:
   ```
   private static final ResilienceConfiguration RESILIENCE_CONFIG = ResilienceConfiguration.of("get-goals")
       .timeLimiterConfiguration(ResilienceConfiguration.TimeLimiterConfiguration.of(Duration.ofSeconds(2)));
   ```
   For every Resilience API usage, providing an operation identifier is a requirement.
   The identifier is used to provide a unique context for the resilience patterns.
   By default, the resilience properties are applied with tenant and principal isolation activated.
   The configuration above is named "get-goals" and will limit the execution of the decorated operation to 2 seconds.
5. Improve the `updateSFSF` method:
   ```diff
     var goal =
   -   goalService.getLearningGoal();
   +   ResilienceDecorator.executeSupplier(() -> goalService.getLearningGoal(), RESILIENCE_CONFIG);
   ```
   Good, now the retrieval of the learning goal from SAP SuccessFactors is limited to 2 seconds.
   If the operation takes longer, it will be aborted and the user will be notified.

The resilience configuration allows for more configuration.
But for now let's focus on the timeout.

## Exercise ?.3 - Locally Test the Resilience Patterns

In order to test the resilience patterns locally, we need to direct our requests from SAP SuccessFactors to a locally provided mock server.

1. Change the destination url of `SFSF-BASIC-ADMIN` to `http://localhost:8080`.
2. Run the application locally with `mvn spring-boot:run` or by running/debugging the `Application` class.
3. Open the application frontend at `http://localhost:8080/#resilience`.
   Notice the minor URL change.
   In addition to the list of sessions, you will see two adjustable input fields for artificial "delay" and "fault rate".
4. Enter a delay of `3000` and keep the fault rate at `0`.
5. When clicking on one session, you will see the loading indicator for 2 seconds, after which an error message appears.

![Alert for timeout exception](images%2Fscreen-error-2.png)

Congratulations, you have successfully tested the timeout resilience pattern.
Please feel free to play around with the delay and resilience timeout configuration to see how the application behaves.

## Exercise ?.4 - (Optional) Use the Retry Pattern

1. Locate the `SignupHandler` class in your application.
2. Extend the `RESILIENCE_CONFIG` declaration:
   ```diff
     private static final ResilienceConfiguration RESILIENCE_CONFIG = ResilienceConfiguration.of("get-goals")
         .timeLimiterConfiguration(ResilienceConfiguration.TimeLimiterConfiguration.of(Duration.ofSeconds(2)))
   +     .retryConfiguration(ResilienceConfiguration.RetryConfiguration.of(3, Duration.ofSeconds(1)));
   ```
   The configuration above add a retry mechanism to repeat the operation upon failure. 
   The number of invocations is limited to 3.
   Between each invocation, there will be a 1-second delay.

That's it, now let's test the retry resilience pattern.

1. Open the application frontend at `http://localhost:8080/#resilience`.
2. Enter a failure rate percentage of `100` and keep the delay at `0`.
5. When clicking on a session, you will receive an error message.

![Alert for Goal error](images%2Fscreen-error-0.png)

In the application logs you will notice the additional retries.
Please feel free to play around with number of delays, to make the effect more visible.
If you add delay, you can check which one fails first - the retry or time-limiter.
In production, you will need to make a reasonable decision for these settings, depending on the target system.


## Exercise ?.5 - (Optional) Use the Rate-Limiter Pattern


1. Locate the `SignupHandler` class in your application.
2. Extend the `RESILIENCE_CONFIG` declaration:
   ```diff
     private static final ResilienceConfiguration RESILIENCE_CONFIG = ResilienceConfiguration.of("get-goals")
         .timeLimiterConfiguration(ResilienceConfiguration.TimeLimiterConfiguration.of(Duration.ofSeconds(2)))
         .retryConfiguration(ResilienceConfiguration.RetryConfiguration.of(3, Duration.ofSeconds(1)))
   +     .rateLimiterConfiguration(ResilienceConfiguration.RateLimiterConfiguration.of(Duration.ofSeconds(1), Duration.ofSeconds(2), 10));
   ```
   The configuration above adds a rate-limiter, to limit the number of times the decorated operation is invoked in a moving time window.
   The number of invocations is limited to 10.
   The moving time window has a duration of 30 seconds.
   The punishment for exceeding the limit is a 1-second delay.

That's it, now let's test the rate limiter resilience pattern.


1. Open the application frontend at `http://localhost:8080/#resilience`.
2. Keep failure rate and delay at `0`.
5. Click and register for all sessions, reload the website if necessary.
   At some point, you will notice a 1s delay.

Please feel free to play around with the time-limiter and rate-limiter configuration to make this effect more visible.


## Summary

You've now successfully learnt how to use resilience patterns of SAP Cloud SDK to improve application robustness.
There are more patterns available, like the circuit breaker or bulkhead.
Also caching can be used with the same `Resilience API`.

Continue to - Exercise ?