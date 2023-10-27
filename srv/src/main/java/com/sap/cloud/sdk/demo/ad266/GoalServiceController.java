package com.sap.cloud.sdk.demo.ad266;

import cds.gen.goal.Goal101;
import cds.gen.goalservice.Goal;
import cds.gen.goalservice.GoalService_;
import cds.gen.goalservice.Goal_;
import com.sap.cds.Result;
import com.sap.cds.ResultBuilder;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cloud.sdk.demo.ad266.remote.GoalServiceHandler;
import com.sap.cloud.sdk.demo.ad266.utility.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ServiceName(GoalService_.CDS_NAME)
public class GoalServiceController implements EventHandler {
    /*
     * Helper class for development purposes to easily invoke the logic inside the GoalServiceHandler class.
     */
    @Autowired
    private GoalServiceHandler goalService;

    @Autowired
    private Helper helper;

    @On( entity = Goal_.CDS_NAME )
    public List<Goal> getLearningGoals(CdsReadEventContext context)
    {
        var goals = goalService.getLearningGoals();

        return goals.stream().map(GoalServiceController::toSimpleGoal).toList();
    }

    @On
    public Goal createGoal(CdsCreateEventContext context, Goal goal )
    {
        var result = goalService.createGoal(helper.getUser(), goal);

        return toSimpleGoal(result);
    }

    @On( entity = Goal_.CDS_NAME )
    public Result deleteGoal(CdsDeleteEventContext context )
    {
        return goalService.deleteGoal(context.getCqn());
    }

    private static Goal toSimpleGoal(Goal101 goal ) {
        var simpleGoal = Goal.create();
        simpleGoal.setId(goal.getId());
        simpleGoal.setTitle(goal.getName());
        simpleGoal.setDescription(goal.getMetric());
        return simpleGoal;
    }
}
