package com.sap.cloud.sdk.demo.ad266;

import cds.gen.signupservice.SignUpContext;
import cds.gen.signupservice.SignupService_;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cloud.sdk.demo.ad266.remote.GoalServiceHandler;
import com.sap.cloud.sdk.demo.ad266.remote.RegistrationServiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ServiceName(SignupService_.CDS_NAME)
public class SignupHandler implements EventHandler
{
    @Autowired
    private RegistrationServiceHandler signupService;

    @Autowired
    private GoalServiceHandler goalService;

    @On
    public void signUp(SignUpContext context)
    {
        String session;
        if (context.getSession() != null) {
            session = context.getSession();
        } else {
            session = "Opening Keynote";
        }

        register(session);

        updateSFSF(session);

        context.setResult("Yay, we successfully signed you up for the session: " + session + ".\n"
                + "Also, we created an entry in your 'Learning and Growth' section in SAP SuccessFactors to reflect your efforts.");
    }

    private void register(String session) {
        // sign up for the event and the session
        signupService.signUpForTechEd();

        signupService.signUpForSession(session);
    }

    private void updateSFSF(String session) {
        // create a goal and related tasks in SFSF
        var goal = goalService.getLearningGoal();

        if ( goal == null ) {
            goal = goalService.createGoal();
        }

        goalService.createTask(goal, session);
    }
}
