package com.sap.cloud.sdk.demo.ad266;

import cds.gen.goalservice.Goal;
import cds.gen.goalservice.GoalService_;
import cds.gen.goalservice.Goal_;
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

import static com.sap.cloud.sdk.demo.ad266.utility.Helper.toSimpleGoal;

@Component
@ServiceName(GoalService_.CDS_NAME)
public class GoalServiceController implements EventHandler {
    /*
     * Helper class for development purposes to easily invoke the logic inside the GoalServiceHandler class.
     */
    @Autowired
    private GoalServiceHandler goalService;

    @Autowired
    Helper helper;

    @On( entity = Goal_.CDS_NAME )
    public List<Goal> getLearningGoals(CdsReadEventContext context)
    {
        var goals = goalService.getLearningGoals();

        return goals.stream().map(Helper::toSimpleGoal).toList();
    }

    @On
    public Goal createGoal(CdsCreateEventContext context, Goal goal )
    {
        var result = goalService.createGoal(helper.getUser(), goal);

        return toSimpleGoal(result);
    }

    @On( entity = Goal_.CDS_NAME )
    public void deleteGoal(CdsDeleteEventContext context )
    {
        goalService.deleteGoal(context.getCqn());
        context.setCompleted();
    }
}
