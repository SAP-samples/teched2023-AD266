package com.sap.cloud.sdk.demo.ad266;

import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipalFacade;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		setupCloudSdkFacades();

		SpringApplication.run(Application.class, args);
	}

	private static void setupCloudSdkFacades()
	{
		PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());
	}
}
