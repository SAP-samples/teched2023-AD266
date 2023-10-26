package com.sap.cloud.sdk.demo.ad266.remote;

import static cds.gen.goal.Goal_.GOAL101;
import static cds.gen.goal.Goal_.GOAL_TASK101;

import cds.gen.goal.Goal101;
import cds.gen.goal.GoalTask101;
import cds.gen.goalservice.Goal;
import cds.gen.goalservice.GoalService_;
import cds.gen.goalservice.Goal_;
import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@ServiceName(GoalService_.CDS_NAME)
public class GoalServiceHandler implements EventHandler
{
    private static final String DEMO_ID = "ID00"; // TODO replace with your demo ID

    @Autowired
    @Qualifier(cds.gen.goal.Goal_.CDS_NAME)
    private CqnService goalService;

    @Autowired
    private CdsRuntime cdsRuntime;

    private List<Goal101> getLearningGoals()
    {
        var user = getUser();

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

        var goals = goalService.run(select).listOf(Goal101.class);

        var visibleGoals = goals
                .stream()
                .filter(g -> g.getPermissionNav().getView())
                .toList();

        log.info("Got the following goals from the server: {}", visibleGoals);

        return visibleGoals;
    }

    public Goal101 getLearningGoal() {
        return getLearningGoals().stream().findFirst().orElse(null);
    }

    public Goal101 createGoal( ) {
        return createGoal(getUser(), Goal.create());
    }

    private Goal101 createGoal(String user, Goal goal)
    {
        var draft = draftGoal(goal, user);
        var query = Insert.into(GOAL101).entry(draft);

        var result = goalService.run(query).single(Goal101.class);

        log.info("Created the following Goal in SFSF: {}", result);
        return result;
    }

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

    @On( entity = Goal_.CDS_NAME )
    public List<Goal> getLearningGoals(CdsReadEventContext context)
    {
        var goals = getLearningGoals();

        return goals.stream().map(GoalServiceHandler::toSimpleGoal).toList();
    }

    @On
    public Goal createGoal( CdsCreateEventContext context, Goal goal )
    {
        var result = createGoal(getUser(), goal);

        return toSimpleGoal(result);
    }

    @On( entity = Goal_.CDS_NAME )
    public Result deleteGoal( CdsDeleteEventContext context )
    {
        return goalService.run(context.getCqn());
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
        var simpleGoal = Goal.create();
        simpleGoal.setTitle(goal.getName());
        simpleGoal.setDescription(goal.getMetric());
        return simpleGoal;
    }
}
