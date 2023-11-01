# Exercise 3 - Get and Import SuccessFactors Goal Plan Service

In the previous exercise, we added functionality to allow a user to register for an event.
What is left now is to add the functionality to create a goal and related tasks in SuccessFactors.

In this exercise, we will look at how to use the [CAP Remote Services](https://cap.cloud.sap/docs/java/remote-services) feature to connect to the [SAP SuccessFactors Goal API](https://api.sap.com/api/PerformanceandGoalsPMGM/resource/Goal_101).

We will do this in two parts: First, we'll get the [SuccessFactors Goal Plan Service API](https://api.sap.com/api/PerformanceandGoalsPMGM/overview) definition and import the service into our project.
Then we will use the imported service in the subsequent step (Exercise 4). 

## 3.1 Download Specification from SAP Business Accelerator Hub

Visit the SAP Business Accelerator Hub to fetch the [SuccessFactors Goal Plan](https://api.sap.com/api/PerformanceandGoalsPMGM/overview) API specification. You should see results like below:

![](images/03_01.png)

Scroll down the page to find API specification of the service listed.

- [ ] 🔨 **Download the EDMX file by clicking on the download button.** 
  - You might have to log in with your trial account credentials.
  ![](images/03_02.png)

- [ ] 🔨 **For ease of understanding, please rename the downloaded file to `Goal.edmx` and place it in a folder of your choice.**

> **Tip:** If you are facing issues with logging in, for your convenience we have also included the service definition file in the `exercises/resources` folder of this repository ([Goal.edmx](../resources/Goal.edmx)).

## 3.2 Add the Goal Plan Service to Your Project

- [ ] 🔨 **In your application's [srv/pom.xml](../../srv/pom.xml) file add the following dependency:**
   ```xml
   <!-- Remote Services -->
   <dependency>
       <groupId>com.sap.cds</groupId>
       <artifactId>cds-feature-remote-odata</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

This dependency is required to enable the [CAP Remote Services Feature](https://cap.cloud.sap/docs/java/remote-services#enabling-remote-services).
This feature allows you to directly consume remote service APIs via CQN queries in a CAP application.

Now we will import the remote service.

- [ ] 🔨 **From your project's root directory (not the `srv` directory) run the following command with the path to the downloaded service definition file as a parameter:** 
   
   ```bash
   cds import /path-to-edmx-file/Goal.edmx --as cds
   ```
   
The output will look like this:

```bash
[cds] - imported API to srv/external/Goal
> use it in your CDS models through the like of:

using { Goal as external } from './external/Goal'

[cds] - updated ./package.json
```
   
The command will copy the service definition file to the [srv/external](../../srv/external) folder of your project and convert it to CAP’s CDS format, which will be placed there as well ([srv/external/Goal.cds](../../srv/external/Goal.cds)).
   
Additionally, the remote service will be registered as a requirement in the [package.json](../../package.json) file:
   
```json
{
  "cds": {
    "requires": {
      "Goal": {
        "kind": "odata-v2",
        "model": "srv/external/Goal"
      }
    }
  }
}
```

## 3.3 Configure a Destination for the Remote API

Remember how we created a destination for the Registration Service in [exercise 2.3.2](../ex2/README.md#232-using-a-destination)?
We need to do the same the SuccessFactors Goal Plan Service.

So let's add a destination for the SuccessFactors Goal Plan Service to our environment variable.

- [ ] 🔨 **Update your `destinations` environment variable to include an entry for the SuccessFactors system as follows:**
  - Replace the username and password as provided to you in the session.
  - For CMD:
  ```cmd
  set destinations=[{name: "Registration-Service", url: "https://ad266-registration.cfapps.eu10-004.hana.ondemand.com/"},{"name":"SFSF-BASIC-ADMIN", "url":"https://apisalesdemo8.successfactors.com/", "type": "HTTP", "user": "USER", "password": "PASSWORD"}]
  ```
  - For PowerShell:
  ```ps
  $env:destinations='[{name: "Registration-Service", url: "https://ad266-registration.cfapps.eu10-004.hana.ondemand.com/"},{"name":"SFSF-BASIC-ADMIN", "url":"https://apisalesdemo8.successfactors.com/", "type": "HTTP", "user": "USER", "password": "PASSWORD"}]'
  ```

However, the destination will be used slightly differently compared to the OpenAPI service we used in the previous exercise.
Instead of loading the destination in the code, we'll configure it in the [application.yaml](../../srv/src/main/resources/application.yaml) file.

- [ ] 🔨 **In your application's [application.yaml](../../srv/src/main/resources/application.yaml) add the following:**

```yaml
cds:
 datasource:
   auto-config.enabled: false
 remote.services:
   - name: "Goal"
     destination:
       name: "SFSF-BASIC-ADMIN"
       type: "odata-v2"
       service: "/odata/v2"
``` 

- The `name` property simply refers to the destination (by its name) we would like to use for the remote service.
- The `type` property defines the protocol used by the remote API which is an OData v2 service in this case.
- The `service` property value would be appended to the url obtained from the destination.

> **Tip:** The name of the destination given here will be re-used to create the destination in the SAP BTP cockpit later on.

## 3.4 Update Project Files and Compile the Project

Now that we imported the remote service we can un-comment some the source code we prepared for the next exercises and build the project.

- [ ] 🔨 **Un-comment all Java code in the following Java classes:**
  - `GoalServiceHandler` ([here](../../srv/src/main/java/com/sap/cloud/sdk/demo/ad266/remote/GoalServiceHandler.java))
  - `GoalServiceFilter` ([here](../../srv/src/main/java/com/sap/cloud/sdk/demo/ad266/remote/GoalServiceFilter.java))
  - `GoalServiceController` ([here](../../srv/src/main/java/com/sap/cloud/sdk/demo/ad266/GoalServiceController.java))
  - `SignupHandler` ([here](../../srv/src/main/java/com/sap/cloud/sdk/demo/ad266/SignupHandler.java))
- [ ] 🔨 **Update the [service.cds](../../srv/service.cds) file as follows:**
  - Uncomment all commented out sections
  - Replace the `goal` entity inside the `GoalService` with the following code:
    ```cds
    entity Goal as projection on Goal_101 {
            id,
            name as title,
            metric as description,
        }
    ```
- [ ] 🔨 **Build the application with `mvn clean compile`.**

## Summary

You've now successfully added the SuccessFactors Goal Plan Service to your project.

Continue to - [Exercise 4 - Consuming the SAP SuccessFactors Goal API using the CAP Remote Services Feature](../ex4/README.md)

