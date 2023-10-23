package com.sap.cloud.sdk.demo.ad266;

import cds.gen.signupservice.SignUpContext;
import cds.gen.signupservice.SignupService_;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cloud.sdk.demo.ad266.remote.GoalServiceHandler;
import com.sap.cloud.sdk.demo.ad266.remote.TodoServiceHandler;
import com.sap.cloud.sdk.demo.ad266.utility.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            goal = goalService.createGoal(user);
        }

        var sessionTitle = context.getParameterInfo().getQueryParameter("title");
        if ( sessionTitle == null ) {
            sessionTitle = "test sub goal";
        }
        goalService.createSubGoal(goal, sessionTitle);

        context.setCompleted();
    }
}
