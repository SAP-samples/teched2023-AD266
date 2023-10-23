package com.sap.cloud.sdk.demo.ad266;

import cds.gen.goal_101.Goal101;
import cds.gen.goal_101.Goal101Model_;
import cds.gen.goal_101.Goal101_;
import cds.gen.goalservice.Goal;
import cds.gen.goalservice.GoalService_;
import cds.gen.goalservice.Goal_;
import cds.gen.signupservice.SignUpContext;
import cds.gen.signupservice.SignupService_;
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
import com.sap.cloud.sdk.demo.ad266.remote.GoalServiceHandler;
import com.sap.cloud.sdk.demo.ad266.remote.TodoServiceHandler;
import com.sap.cloud.sdk.demo.ad266.utility.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;

import static com.sap.cloud.sdk.demo.ad266.utility.Helper.extractUser;

@Slf4j
@Component
@ServiceName(SignupService_.CDS_NAME)
public class SignupHandler implements EventHandler
{

    @Autowired
    TodoServiceHandler todoService;

    @Autowired
    GoalServiceHandler goalService;

    @On( event = SignUpContext.CDS_NAME)
    public void signUp(SignUpContext context)
    {
        var user = Helper.getUser(context);

        var goal = goalService.getLearningGoal(user);
        if ( goal == null ) {
            goalService.createGoal(user);
        }

        var sessionTitle = context.getParameterInfo().getQueryParameter("title");
        goalService.createSubGoal(user, sessionTitle);

        todoService.createTodo(user, sessionTitle);

        context.setCompleted();
    }
}
