package com.sap.cloud.sdk.demo.recap23.remote;

import cds.gen.goal_101.Goal101;
import cds.gen.goal_101.Goal101Model_;
import cds.gen.goal_101.Goal101_;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;

import static com.sap.cloud.sdk.demo.recap23.utility.Helper.extractUser;

@Slf4j
@Component
@ServiceName(GoalService_.CDS_NAME)
public class GoalServiceHandler implements EventHandler
{
    @Autowired
    @Qualifier(Goal101Model_.CDS_NAME)
    private CqnService goalService;

    @On( event = CqnService.EVENT_READ, entity = Goal_.CDS_NAME)
    public void getAllGoals(CdsReadEventContext context)
    {
        var user = extractUser(context);

        var query = Select.from(Goal101_.class)
                .where(e -> e.userId().eq(user))
                .limit(10);

        var goals = goalService.run(query).listOf(Goal101.class);

        log.info("Got the following ToDos from the server: {}", goals);

        context.setResult(goals.stream().map(GoalServiceHandler::toSimpleGoal).toList());
    }
    @On( event = CqnService.EVENT_CREATE, entity = Goal_.CDS_NAME)
    public void createGoal( CdsCreateEventContext context, Goal goal )
    {
        var user = extractUser(context);
        var draft = draftGoal(goal, user);
        var query = Insert.into(Goal101_.class).entry(draft);

        var result = goalService.run(query).single(Goal101.class);

        log.info("Created the following Goal in SFSF: {}", result);

        context.setResult(Collections.singleton(toSimpleGoal(result)));
    }

    @On( event = CqnService.EVENT_DELETE, entity = Goal_.CDS_NAME)
    public void deleteGoal( CdsDeleteEventContext context )
    {
        Result result = goalService.run(context.getCqn());

        context.setResult(result);
    }

    private static Goal101 draftGoal(Goal draft, String user)
    {
        var goal = Goal101.create();
        goal.setName(draft.getTitle());
        goal.setDone(draft.getCompletion());
        goal.setMetric(draft.getDescription());
        goal.setCategory("Learning and Growth");
        goal.setType("user");
        goal.setFlag(1);
        goal.setUserId(user);
        goal.setState("On Track");
        goal.setDue(LocalDate.now());
        return goal;
    }

    private static Goal toSimpleGoal( Goal101 goal ) {
        var simpleGoal = cds.gen.goalservice.Goal.create();
        simpleGoal.setTitle(goal.getName());
        simpleGoal.setCompletion(goal.getDone());
        simpleGoal.setDescription(goal.getMetric());
        return simpleGoal;
    }
}
