# Exercise 4 - Add functionality to Event Handlers: Part 2- Use CAP Remote Services

In this exercise, we will look at adapting the `GoalServiceHandler` and `SignupHandler` to add functionality to our application.

Let us outline the scenario we want to build again and see what is still left to be built. 

We want a user to be able to sign up for a conference and when they do so the following things should happen:
1. The user should get registered for the conference
2. A learning goal should be automatically created for them in SuccessFactors.
3. Any subsequent sessions that a user signs up for should also be registered and added as sub-goals to the goal.

We have already added the functionality to register the user for the conference in the previous exercise.
What is left is to add the functionality to create a goal in SuccessFactors.
And to put both these parts together inside the `signUp` action in `SignupHandler`.

`GoalServiceHandler` would be the Event handler that would be attached to the `GoalService`. 
This class would use remote services to create a goal in SuccessFactors and add sub-goals to it. 

Let's enhance the `GoalServiceHandler` first.

## Exercise 4.1 - Add Business Logic to GoalServiceHandler

1. Let's start by trying to fetch all Learning goals for a user from SuccessFactors. 
   
   Add the following code snippet inside the `getLearningGoals()`  method:
    ```java
    private List<Goal101> getLearningGoals()
    {
        var user = getUser();
        
        //Build a CQL query to fetch all learning goals for the user with a specific name. This would be used as a where clause in our final query
        var query = CQL.get(Goal101.CATEGORY).eq("Learning and Growth")
                .and( CQL.get(Goal101.NAME).eq(DEMO_ID + ": Learn something at TechEd 2023"))
                .and( CQL.get(Goal101.STATE).ne("Completed"))
                .and(CQL.get(Goal101.USER_ID).eq(user));

        //Build an OData system query select to fetch all fields and navigation properties tasks and permissionNav with the above query as a where clause
        var select = Select.from(Goal101_.class)
                .columns(
                        StructuredType::_all,
                        g -> g.tasks().expand(),
                        g -> g.permissionNav().expand())
                .where(query);

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
    Note that we are using the `goalService` object to run the query. This is a Remote Service object from CAP, which acts as a client to the remote SuccessFactors API.
    It was injected into the class earlier by:
    ```java
    @Autowired
    @Qualifier(cds.gen.goal.Goal_.CDS_NAME)
    private CqnService goalService;
    ```
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

