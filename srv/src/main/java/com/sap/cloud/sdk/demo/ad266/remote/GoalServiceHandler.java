package com.sap.cloud.sdk.demo.ad266.remote;

import cds.gen.goal.Goal101;
import cds.gen.goal.GoalTask101;
import cds.gen.goal.GoalTask101_;
import cds.gen.goal.Goal_;
import cds.gen.goal.Goal101_;
import cds.gen.goalservice.Goal;
import cds.gen.goalservice.GoalService_;
import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.StructuredType;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.runtime.CdsRuntime;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty;
import com.sap.cloud.sdk.demo.ad266.utility.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@ServiceName(GoalService_.CDS_NAME)
public class GoalServiceHandler implements EventHandler
{
    private static final String DEMO_ID = "ID01"; // TODO replace with your demo ID

    @Autowired
    @Qualifier(Goal_.CDS_NAME)
    private CqnService goalService;

    @Autowired
    private CdsRuntime cdsRuntime;

    @On( event = CqnService.EVENT_READ, entity = Goal_.CDS_NAME)
    public void getLearningGoals(CdsReadEventContext context)
    {
       var goals = getLearningGoals();

        context.setResult(goals.stream().map(GoalServiceHandler::toSimpleGoal).toList());
    }

    public List<Goal101> getLearningGoals( )
    {
        var user = getUser();
        var query = CQL.get(Goal101.CATEGORY).eq("Learning and Growth")
                .and( CQL.get(Goal101.NAME).eq(DEMO_ID + ": Learn something at TechEd 2023"))
                .and( CQL.get(Goal101.STATE).ne("Completed"))
                .and(CQL.get(Goal101.USER_ID).eq(user));

        var select = Select.from(Goal101_.class)
                .columns(
                        StructuredType::_all,
                        g -> g.tasks().expand(),
                        g -> g.permissionNav().expand())
                .where(query);

        var goals = goalService.run(select).listOf(Goal101.class);

        var visibleGoals = goals
                .stream()
                .filter(g -> g.getPermissionNav().getView())
                .toList();

        log.info("Got the following ToDos from the server: {}", visibleGoals);

        return visibleGoals;
    }

    public Goal101 getLearningGoal() {
        return getLearningGoals().stream().findFirst().orElse(null);
    }

    @On( event = CqnService.EVENT_CREATE, entity = Goal_.CDS_NAME)
    public void createGoal( CdsCreateEventContext context, Goal goal )
    {
        var result = createGoal(getUser(), goal);

        context.setResult(Collections.singleton(toSimpleGoal(result)));
    }

    public Goal101 createGoal( ) {
        return createGoal(getUser(), Goal.create());
    }

    private Goal101 createGoal(String user, Goal goal)
    {
        var draft = draftGoal(goal, user);
        var query = Insert.into(Goal101_.class).entry(draft);

        var result = goalService.run(query).single(Goal101.class);

        log.info("Created the following Goal in SFSF: {}", result);
        return result;
    }

    public void createSubGoal( Goal101 goal, String title )
    {
        var task = GoalTask101.create();
        task.setObjId(goal.getId());
        task.setDescription(title);
        task.setDone(10d);

        var insert = Insert.into(GoalTask101_.CDS_NAME).entry(task);

        goalService.run(insert).single(Goal101.class);

    }

    @On( event = CqnService.EVENT_DELETE, entity = Goal_.CDS_NAME)
    public void deleteGoal( CdsDeleteEventContext context )
    {
        Result result = goalService.run(context.getCqn());

        context.setResult(result);
    }

    private String getUser()
    {
        var destinationName = cdsRuntime
                .getEnvironment()
                .getCdsProperties()
                .getRemote()
                .getService("Goal")
                .getDestination()
                .getName();

        var email = DestinationAccessor.getDestination(destinationName)
                .get(DestinationProperty.BASIC_AUTH_USERNAME).get();
        return email.split("@")[0];
    }

    private static Goal101 draftGoal(Goal draft, String user)
    {
        var goal = Goal101.create();

        if ( draft.getTitle() != null) {
            goal.setName(draft.getTitle());
        } else {
            goal.setName(DEMO_ID + ": Learn something at TechEd 2023");
        }
        if ( draft.getDescription() != null) {
            goal.setMetric(draft.getDescription());
        } else {
            goal.setMetric("Attend sessions at TechEd 2023");
        }
        goal.setCategory("Learning and Growth");
        goal.setType("user");
        goal.setFlag(0);
        goal.setWeight(0d);
        goal.setUserId(user);
        goal.setState("On Track");
        goal.setStart(LocalDate.now());
        goal.setDue(LocalDate.now().plusDays(14));
        return goal;
    }

    private static Goal toSimpleGoal( Goal101 goal ) {
        var simpleGoal = cds.gen.goalservice.Goal.create();
        simpleGoal.setTitle(goal.getName());
        simpleGoal.setDescription(goal.getMetric());
        return simpleGoal;
    }
}
