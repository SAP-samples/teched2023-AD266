[![REUSE status](https://api.reuse.software/badge/github.com/SAP-samples/teched2023-AD266)](https://api.reuse.software/info/github.com/SAP-samples/teched2023-AD266)

# AD266 - Learn Once, Apply Everywhere: Code-First Extensions Across SAP Solutions

## Description

This repository contains the material for the SAP TechEd 2023 session AD266 - Learn Once, Apply Everywhere: Code-First Extensions Across SAP Solutions.


## Overview

This session dives into how tools such as SAP Cloud Application Programming Model, SAP Cloud SDK, and others can be used to develop applications that connect to SAP applications of any kind through APIs.

SAP Cloud Application Programming Model or [CAP](https://cap.cloud.sap/docs/) is a framework of languages, libraries, and tools for building enterprise-grade services and applications. 
It guides developers through proven best practices and accelerates the development process. 

[SAP Cloud SDK](https://sap.github.io/cloud-sdk/docs/overview/overview-cloud-sdk) is a set of libraries and tools for developers to build cloud-native applications on the SAP Business Technology Platform (SAP BTP).
CAP internally uses the SAP Cloud SDK for service consumption.

Over the course of this workshop, you will create an application that fetches data from SAP SuccessFactors [Goal Plan API](https://api.sap.com/api/PerformanceandGoalsPMGM/overview) service by using CAP's [Remote Services](https://cap.cloud.sap/docs/java/remote-services#configuring-remote-services).
You will also add functionality to create goals and tasks and delete them by interacting with the SuccessFactors service via the application.

You could then follow similar steps to connect to any other SAP application that exposes an API and easily build extensions for them in the cloud.

## Requirements

The requirements to follow the exercises in this repository are:

- Java 17
- Maven 3.9
- Node 18+
- NPM 9+

## Exercises

- [Getting Started](exercises/ex0/)
- [Exercise 1 - Get and Import SuccessFactors Goal Plan Service](exercises/ex1/)
    - [Exercise 1.1 - Download specification from SAP Business Accelerator Hub](exercises/ex1#exercise-11-download-specification-from-sap-business-accelerator-hub)
    - [Exercise 1.2 - Add the Goal Plan service to your project](exercises/ex1#exercise-12-add-the-goal-plan-service-to-your-project)
    - [Exercise 1.3 - Configure a destination for the remote API](exercises/ex1#exercise-12-configure-a-destination-for-the-remote-api)
- [Exercise 2 - Understand the existing Project setup](exercises/ex2/)
    - [Exercise 2.1 - CDS Maven Plugin](exercises/ex2#exercise-21-cds-maven-plugin)
    - [Exercise 2.2 - Understanding Service Definitions](exercises/ex2#exercise-22-understanding-service-definitions)
    - [Exercise 2.3 - Understanding Event Handlers](exercises/ex2#exercise-23-understanding-event-handlers)
- [Exercise 3 - Add functionality to Event Handlers](exercises/ex3/)
  - [Exercise 3.1 - Add Business Logic to GoalServiceHandler](exercises/ex3#exercise-31---add-business-logic-to-goalservicehandler)
  - [Exercise 3.2 - Add Business Logic to SignupHandler](exercises/ex3#exercise-32---add-business-logic-to-signuphandler)
  - [Exercise 3.3 - Run your application locally](exercises/ex3#exercise-33---run-your-application-locally)
- [Exercise 4 - Use SAP Cloud SDK](exercises/ex4/)
- [Exercise 5 - Deploying the application to SAP Business Technology Platform](exercises/ex5/)
  - [Exercise 5.1 - Creating a destination for SuccessFactors API endpoint](exercises/ex5#exercise-41-creating-a-destination-for-successfactors-api-endpoint)
  - [Exercise 5.2 - Creating a destination service instance](exercises/ex5#exercise-42-creating-a-destination-service-instance)
  - [Exercise 5.3 Adjusting the deployment descriptor - manifest.yml](exercises/ex5#exercise-43-adjusting-the-deployment-descriptor---manifestyml)
  - [Exercise 5.4 Deploy the application and Testing](exercises/ex5#exercise-44-deploy-the-application-and-testing)

**IMPORTANT**

Your repo must contain the .reuse and LICENSES folder and the License section below. DO NOT REMOVE the section or folders/files. Also, remove all unused template assets(images, folders, etc) from the exercises folder. 

## Contributing
Please read the [CONTRIBUTING.md](./CONTRIBUTING.md) to understand the contribution guidelines.

## Code of Conduct
Please read the [SAP Open Source Code of Conduct](https://github.com/SAP-samples/.github/blob/main/CODE_OF_CONDUCT.md).

## How to obtain support

Support for the content in this repository is available during the actual time of the online session for which this content has been designed. Otherwise, you may request support via the [Issues](../../issues) tab.

## License
Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved. This project is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSES/Apache-2.0.txt) file.
