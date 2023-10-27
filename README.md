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

Over the course of this workshop, you will create an application that consumes both an OpenAPI and an OData service. 

For the OData part, you will learn how to fetch data from SAP SuccessFactors [Goal Plan API](https://api.sap.com/api/PerformanceandGoalsPMGM/overview) service by using CAP's [Remote Services](https://cap.cloud.sap/docs/java/remote-services#configuring-remote-services).
You will also add functionality to create goals and sub-goals and delete them by interacting with the SuccessFactors service via the application.

For the OpenAPI part, you will learn how to use the SAP Cloud SDK to conveniently interact with the service in a type-safe manner. 

You could then follow similar steps to connect to any other SAP application that exposes an API and easily build extensions for them in the cloud.

## Requirements

The requirements to follow the exercises in this repository are:

- Java 17
- Maven 3.9
- Node 18+
- NPM 9+
- cdsdk 7.0+
- cf cli 7.7+

## Exercises

// TODO: update once structure is final

- [Getting Started](exercises/ex0/)
- [Exercise 1 - Get and Import SuccessFactors Goal Plan Service](exercises/ex1/)
    - [Exercise 1.1 - Download specification from SAP Business Accelerator Hub](exercises/ex1#exercise-11-download-specification-from-sap-business-accelerator-hub)
    - [Exercise 1.2 - Add the Goal Plan service to your project](exercises/ex1#exercise-12-add-the-goal-plan-service-to-your-project)
    - [Exercise 1.3 - Configure a destination for the remote API](exercises/ex1#exercise-12-configure-a-destination-for-the-remote-api)
- [Exercise 2 - Understand the existing Project setup](exercises/ex2/)
    - [Exercise 2.1 - CDS Maven Plugin](exercises/ex2#exercise-21-cds-maven-plugin)
    - [Exercise 2.2 - Understanding Service Definitions](exercises/ex2#exercise-22-understanding-service-definitions)
    - [Exercise 2.3 - Understanding Event Handlers](exercises/ex2#exercise-23-understanding-event-handlers)
    - [Exercise 2.4 - Run your application locally](exercises/ex2#exercise-24---run-your-application-locally)
- [Exercise 3 - Consuming the Registration API using the SAP Cloud SDK](exercises/ex3/)
  - [Exercise 3.1 - Familiarising yourself with the remote OpenAPI Service](exercises/ex3#exercise-31---familiarising-yourself-with-the-remote-openapi-service)
  - [Exercise 3.2 - Add SAP Cloud SDK to your project and generate a typed OpenAPI client](exercises/ex3#exercise-32---add-sap-cloud-sdk-to-your-project-and-generate-a-typed-openapi-client)
  - [Exercise 3.3 - Use typed client to consume remote OpenAPI service](exercises/ex3#exercise-33---use-typed-client-to-consume-remote-openapi-service)
  - [Exercise 3.4 - Run your application locally](exercises/ex3#exercise-34---run-your-application-locally)
- [Exercise 4 - Consuming the SAP SuccessFactors Goal API using the CAP Remote Services Feature](exercises/ex4/)
  - [Exercise 4.1 - Fetch all learning goals of a user in GoalServiceHandler](exercises/ex4#exercise-41---fetch-all-learning-goals-of-a-user-in-goalservicehandler)
  - [Exercise 4.2 - Create a learning goal for a user via GoalServiceHandler](exercises/ex4#exercise-42---create-a-learning-goal-for-a-user-via-goalservicehandler)
  - [Exercise 4.3 - Create a sub goal for a user via GoalServiceHandler](exercises/ex4#exercise-43---create-a-sub-goal-for-a-user-via-goalservicehandler)
  - [Exercise 4.4 - Add functionality to SignupHandler](exercises/ex4#exercise-44---add-functionality-to-signuphandler)
  - [Exercise 4.5 - Run your application locally](exercises/ex4#exercise-45---run-your-application-locally)
  - [Exercise 4.6 - Testing SignupHandler](exercises/ex4#exercise-46---testing-signuphandler)
- [Exercise 5 - (Optional) Deploying the application to SAP Business Technology Platform](exercises/ex5/)
  - [Exercise 5.1 - Creating a destination for SuccessFactors API endpoint](exercises/ex5#exercise-51-creating-a-destination-for-successfactors-api-endpoint)
  - [Exercise 5.2 - Creating a destination service instance](exercises/ex5#exercise-52-creating-a-destination-service-instance)
  - [Exercise 5.3 Adjusting the deployment descriptor - manifest.yml](exercises/ex5#exercise-53-adjusting-the-deployment-descriptor---manifestyml)
  - [Exercise 5.4 Deploy the application and Testing](exercises/ex5#exercise-54-deploy-the-application-and-testing)

## Useful Links
- [SAP Customer Influence](https://influence.sap.com/sap/ino/#/campaign/1175) for SAP S/4HANA Cloud APIs.

## Contributing
Please read the [CONTRIBUTING.md](./CONTRIBUTING.md) to understand the contribution guidelines.

## Code of Conduct
Please read the [SAP Open Source Code of Conduct](https://github.com/SAP-samples/.github/blob/main/CODE_OF_CONDUCT.md).

## How to obtain support

Support for the content in this repository is available during the actual time of the online session for which this content has been designed. Otherwise, you may request support via the [Issues](../../issues) tab.

## License
Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved. This project is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSES/Apache-2.0.txt) file.
