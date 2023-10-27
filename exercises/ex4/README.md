# Exercise 4 - Add functionality to Event Handlers: Part 2- Use CAP Remote Services

In this exercise, we will look at adapting the `GoalServiceHandler` and `SignupHandler` to add functionality to our application.

Let us outline the scenario we want to build again and see what is still left to be built. 

We want a user to be able to sign up for an event and when they do so the following things should happen:
1. The user should get registered for the event
2. A learning goal should be automatically created for them in SuccessFactors.
3. Any subsequent sessions that a user signs up for should also be registered and added as sub-goals to the goal.

We have already added the functionality to register the user for the event in the previous exercise.
What is left is to add the functionality to create a goal in SuccessFactors.
And to put both these parts together inside the `signUp` action in `SignupHandler`.

`GoalServiceHandler` would be the Event handler that would be attached to the `GoalService`. 
This class would use remote services to create a goal in SuccessFactors and add sub-goals to it. 

Let's enhance the `GoalServiceHandler` first.

## Exercise 4.1 - Fetch all learning goals of a user in GoalServiceHandler

1. If not already done yet, please also additionally assign a value for `DEMO_ID` at the top of the `GoalServiceHandler` class.
   ```java
   private static final String DEMO_ID = "ID"+"<add your desk number here>";
   ```
   This is necessary because all participants will use the same credentials for the SuccessFactors system and we need to make sure that the goals created by each participant are unique.

2. Let's start by trying to fetch all Learning goals for a user from SuccessFactors named "<your-DEMO_ID>: Learn something at TechEd 2023". 
   
   Add the following code snippet inside the `getLearningGoals()`  method:

   ```java
       private List<Goal101> getLearningGoals()
       {
           var user = getUser();
           
           //Build an OData system query select to fetch all fields and navigation properties tasks and permissionNav with a where clause that selects a particular goal for a user
           var select = Select.from(GOAL101)
                   .columns(
                           g -> g._all(),
                           g -> g.tasks().expand(),
                           g -> g.permissionNav().expand())
                   .where(
                           g -> g.category().eq("Learning and Growth")
                           .and(g.name().eq(DEMO_ID + ": Learn something at TechEd 2023"))
                           .and(g.state().ne("Completed"))
                           .and(g.userId().eq(user)));
   
           //Execute the select query and parse the response into a list of Goal101 objects
           var goals = goalService.run(select).listOf(Goal101.class);
   
           //Filter out the goals that the user doesn't have permission to view
           var visibleGoals = goals
                   .stream()
                   .filter(g -> g.getPermissionNav().getView())
                   .toList();
   
           log.info("Got the following goals from the server: {}", visibleGoals);
   
           return visibleGoals;
       }  
   ```

    Note that we use a CQL Statement builder [Select](https://cap.cloud.sap/docs/java/query-api#the-cql-statement-builders) to build the select query.
    
    Subsequently, we are using the `goalService` object to run the query. This is a Remote Service object from CAP, which acts as a client to the remote SuccessFactors API.
    
    It was injected into the class earlier by:
   ```java
       @Autowired
       @Qualifier(cds.gen.goal.Goal_.CDS_NAME)
       private CqnService goalService;
   ```
   
3. We want to call this method when there is a Read event for the `Goal` entity. On the method `getLearningGoals(context)` annotated with `@On( event = CqnService.EVENT_READ, entity = Goal_.CDS_NAME)` add the following code:
   ```java
       @On( event = CqnService.EVENT_READ, entity = Goal_.CDS_NAME)
       public void getLearningGoals(CdsReadEventContext context)
       {
           var goals = getLearningGoals();
   
           context.setResult(goals.stream().map(GoalServiceHandler::toSimpleGoal).toList());
       }
   ```
   `toSimpleGoal(goal)` method above just converts the fetched `Goal101` SuccessFactor entity to a `Goal` projection object we defined in the earlier exercises in `srv/service.cds` file.

5. You can run the application at this point, and try to fetch the goals for a user. You would get an empty list. This is because we haven't created a goal for the user yet.

   To run the application follow [Exercise 4.5](../ex4#exercise-45---run-your-application-locally) and then open `http://localhost:8080/odata/v4/GoalService/Goal` in your browser to test the fetch goals endpoint.

   You can also see the following logs in your IDE's terminal which confirms that no goal were fetched from SuccessFactors:
   ```
   INFO 75073 --- [nio-8080-exec-1] c.s.c.s.d.a.remote.GoalServiceHandler    : Got the following goals from the server: []
   ```

## Exercise 4.2 - Create a learning goal for a user via GoalServiceHandler

1. The first time you try to fetch the goals, you would get an empty list. This is because we haven't created a goal for the user yet. Let's add the functionality to create a goal for the user.

2. We want to create a goal when there is a Create event for the `Goal` entity. On the method `createGoal( CdsCreateEventContext context, Goal goal )` annotated with `@On` add the following code:
   ```java
       @On
       public void createGoal( CdsCreateEventContext context, Goal goal )
       {
           var result = createGoal(getUser(), goal);
   
           return toSimpleGoal(result);
       }
   ```

3. Let's add the `createGoal(user,goal)` method. Add the following code snippet inside the `createGoal(user,goal)` method:

   ```java
       private Goal101 createGoal(String user, Goal goal)
       {
           var draft = draftGoal(goal, user);
           var query = Insert.into(GOAL101).entry(draft);
   
           var result = goalService.run(query).single(Goal101.class);
   
           log.info("Created the following Goal in SFSF: {}", result);
           return result;
       }
   ```
   `draftGoal(Goal draft, String user)` method just creates a `Goal101` object with some predefined values. You can find the code for this method in the `GoalServiceHandler` class.

    We use a CQL Statement builder [Insert](https://cap.cloud.sap/docs/java/query-api#single-insert) to build the insert query and using the `goalService` Remote service object again, we run the query and parse the response into a `Goal101` object.

## Exercise 4.3 - Create a sub goal for a user via GoalServiceHandler

1. We want to add sub goals to an already created goal when a user registers for session. Sub-goals are represented by `Task101` entity in SuccessFactors. Let's add the functionality to create a sub-goal for the user.

2. Add the following code snippet inside the `createTask(goal, title )` method:

   ```java
       public void createTask(Goal101 goal, String title )
           {
           var description = "Attend the session '" + title + "' and share what you learned!";
           var task = GoalTask101.create();
           task.setObjId(goal.getId());
           task.setDescription(description);
           task.setDone(10d);
   
           var insert = Insert.into(GOAL_TASK101).entry(task);
           goalService.run(insert);
           }
   ```
    We use a CQL Statement builder [Insert](https://cap.cloud.sap/docs/java/query-api#single-insert) to build the insert query and using the `goalService` Remote service object again.

## Exercise 4.4 - Add functionality to SignupHandler

As we now have all parts to create a goal and sub-goals, let's add the functionality to create a goal when a user registers for a session.

1. You can see that in `SignupHandler`, we have injected both `GoalServiceHandler` and `RegistrationServiceHandler` objects. We will use these to register and create goals for the user.

2. Add the following code snippet inside the `register(String session)` method:

   ```java
       private void register(String session) {
           // sign up for the event and the key note session
           signupService.signUpForTechEd();
   
           //signup for any additional sessions
           signupService.signUpForSession(session);
           }
   ```
    We use the `signupService` to call the methods we built in the earlier exercises to sign up for TechEd and sessions.

3. Add the following code snippet inside the `updateSFSF(String session)` method:

   ```java
       private void updateSFSF(String session) {
           // create a goal and related tasks in SFSF
           
           var goal = goalService.getLearningGoal();
   
           if ( goal == null ) {
           goal = goalService.createGoal();
           }
   
           goalService.createTask(goal, session);
           }
   ```
    We use the `goalService` to call the methods we built in the earlier exercises to create a goal and sub-goal for the user.

4. Inside the `signUp(SignUpContext context)` method change the result set in the context to the following:
   ```java
        context.setResult("Yay, we successfully signed you up for the session: " + session + ".\n"
                + "Also, we created an entry in your 'Learning and Growth' section in SAP SuccessFactors to reflect your efforts.");   
   ```

## Exercise 4.5 - Run your application locally

1. You need to first define a destination for the SuccessFactors remote service(if not already created).

2. Enhance the already created destination environment variable in your terminal using:

    ```shell
    set destinations=[{"name":"SFSF-BASIC-ADMIN", "url":"https://apisalesdemo8.successfactors.com/", "type": "HTTP", "user": USER, "password":password, "URL.headers.accept-encoding": "identity"}, {"name":"Signup-Service","url":"https://ad266-signup.cfapps.eu10-004.hana.ondemand.com"}]
    ```

3. Replace USER and PASSWORD with the credentials provided to you.
   Remember to use the same name as the destination name that is defined in the `application.yaml` file.

4. Now run the application with `mvn spring-boot:run`

## Exercise 4.6 - Testing SignupHandler

1. For your convenience, we have a built a small UI to test the signup functionality. You can access it at `http://localhost:8080/`.

2. Click on any of the sessions to register for it. You can see the following logs in your IDE's terminal:
   ```
    DEBUG 77782 --- [nio-8080-exec-2] com.sap.cds.services.impl.ServiceImpl    : Finished emit of 'Goal' for event 'CREATE', entity 'Goal.GoalTask_101'
    DEBUG 77782 --- [nio-8080-exec-2] com.sap.cds.services.impl.ServiceImpl    : Finished emit of 'SignupService' for event 'signUp', entity ''
   ```
   
3. You can now log in to [SuccessFactors](https://pmsalesdemo8.successfactors.com/) with USER and PASSWORD provided and check if the goal and sub-goal have been created for the user.

## Summary

You've now successfully used CAP Remote services to consume SuccessFactors Goal Plan service. 

Continue to - [Exercise 5 - Deploying the application to SAP Business Technology Platform](../ex5/README.md)

