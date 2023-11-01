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

For the OData part, you will learn how to fetch data from SAP SuccessFactors [Goal Plan API](https://api.sap.com/api/PerformanceandGoalsPMGM/overview) by using CAP's [Remote Services](https://cap.cloud.sap/docs/java/remote-services#configuring-remote-services).
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

- [Getting Started](exercises/ex0/README.md)
  - [Prerequisites](exercises/ex0/README.md#prerequisites)
  - [Understanding the use case](exercises/ex0/README.md#understanding-the-use-case)
- [Exercise 1 - Understand the Existing Project Setup](exercises/ex1/README.md)
    - [Exercise 1.1 - Understanding the Project Structure](exercises/ex1/README.md#11-understanding-the-project-structure)
    - [Exercise 1.2 - Understanding Service Definitions](exercises/ex1/README.md#12-understanding-service-definitions)
    - [Exercise 1.3 - CDS Maven Plugin](exercises/ex1/README.md#13-cds-maven-plugin)
    - [Exercise 1.4 - Understanding EventHandlers](exercises/ex1/README.md#14-understanding-eventhandlers)
    - [Exercise 1.5 - Run Your Application Locally](exercises/ex1/README.md#15-run-your-application-locally)
- [Exercise 2 - Consuming the Registration API using the SAP Cloud SDK](exercises/ex2/README.md)
    - [Exercise 2.1 - Familiarising Yourself with the Remote OpenAPI Service](exercises/ex2/README.md#21-familiarising-yourself-with-the-remote-openapi-service)
    - [Exercise 2.2 - Add SAP Cloud SDK to Your Project and Generate a Typed OpenAPI Client](exercises/ex2/README.md#22-add-sap-cloud-sdk-to-your-project-and-generate-a-typed-openapi-client)
    - [Exercise 2.3 - Use the Typed Client to Consume Remote OpenAPI Service](exercises/ex2/README.md#23-use-the-typed-client-to-consume-remote-openapi-service)
    - [Exercise 2.4 - Completing the Registration Flow](exercises/ex2/README.md#24-completing-the-registration-flow)
- [Exercise 3 - Get and Import SuccessFactors Goal Plan Service](exercises/ex3/README.md)
  - [Exercise 3.1 - Download Specification from SAP Business Accelerator Hub](exercises/ex3/README.md#31-download-specification-from-sap-business-accelerator-hub)
  - [Exercise 3.2 - Add the Goal Plan Service to Your Project](exercises/ex3/README.md#32-add-the-goal-plan-service-to-your-project)
  - [Exercise 3.3 - Configure a Destination for the Remote API](exercises/ex3/README.md#33-configure-a-destination-for-the-remote-api)
  - [Exercise 3.4 - Update Project Files and Compile the Project](exercises/ex3/README.md#34-update-project-files-and-compile-the-project)
- [Exercise 4 - Consuming the SAP SuccessFactors Goal API using the CAP Remote Services Feature](exercises/ex4/README.md)
  - [Exercise 4.1 - Create a Remote Service Object](exercises/ex4/README.md#41-create-a-remote-service-object)
  - [Exercise 4.2 - Fetch all learning goals of a user in GoalServiceHandler](exercises/ex4/README.md#42---fetch-all-learning-goals-of-a-user-in-goalservicehandler)
  - [Exercise 4.3 - Create a Learning Goal](exercises/ex4/README.md#43-create-a-learning-goal)
  - [Exercise 4.4 - Create a Task](exercises/ex4/README.md#44---create-a-task)
  - [Exercise 4.5 - (Optional) Add a Custom HTTP Header to Requests](exercises/ex4/README.md#45-optional-add-a-custom-http-header-to-requests)
  - [Exercise 4.6 - (Optional) Understanding the Delete Goal Implementation](exercises/ex4/README.md#46-optional-understanding-the-delete-goal-implementation)
  - [Exercise 4.7 - (Optional) How to deal with Custom Fields or Incomplete Metadata](exercises/ex4/README.md#47-optional-how-to-deal-with-custom-fields-or-incomplete-metadata)
  - [Exercise 4.8 - (Optional) Understanding and Improving the `GoalServiceFilter`](exercises/ex4/README.md#48-optional-understanding-and-improving-the-goalservicefilter)
- [Exercise 5 - (Optional) Deploying the Application to SAP Business Technology Platform](exercises/ex5/README.md)
  - [Exercise 5.1 - Creating a Destination for SuccessFactors API Endpoint and the Synthetic OpenAPI Service](exercises/ex5/README.md#51-creating-a-destination-for-successfactors-api-endpoint-and-the-synthetic-openapi-service)
  - [Exercise 5.2 - Creating a Destination Service Instance](exercises/ex5/README.md#52-creating-a-destination-service-instance)
  - [Exercise 5.3 - Adjusting the Deployment Descriptor - manifest.yml](exercises/ex5/README.md#53-adjusting-the-deployment-descriptor---manifestyml)
  - [Exercise 5.4 - Deploy and Test the Application](exercises/ex5/README.md#54-deploy-and-test-the-application)
- [Share your feedback](https://github.com/SAP-samples/teched2023-AD266/issues/new/choose)

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
