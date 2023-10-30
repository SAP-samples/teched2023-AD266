package com.sap.cloud.sdk.demo.ad266.remote;

/*import cds.gen.goal.Goal_;
import cds.gen.goal.Goal101;
import cds.gen.goal.GoalTask101;

import static cds.gen.goal.Goal_.GOAL101;
import static cds.gen.goal.Goal_.GOAL_TASK101;
*/
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.services.cds.CqnService;
import com.sap.cloud.sdk.demo.ad266.utility.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnDelete;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.ql.Insert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class GoalServiceHandler implements EventHandler
{
    @Autowired
    private Helper helper;

    /*
    public List<Goal101> getLearningGoals()
    {
        var user = helper.getUser();

        return Collections.emptyList();
    }

    public Goal101 getLearningGoal() {
        return getLearningGoals().stream().findFirst().orElse(null);
    }

    public Goal101 createGoal( ) {
        return createGoal(helper.getUser());
    }

    public Goal101 createGoal(String user)
    {

    }

    public void createTask(Goal101 goal, String title )
    {
        var description = "Attend the session '" + title + "' and share what you learned!";

    }

    public Result deleteGoal(CqnDelete delete){
        return goalService.run(delete);
    }

    private static Goal101 draftGoal(String user)
    {

    }*/
}
