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

        context.setResult("Signup will work with exercise completion");
    }

    private void register(String session) {
        //Todo: implement
    }

    private void updateSFSF(String session) {
        //Todo: implement
    }
}
