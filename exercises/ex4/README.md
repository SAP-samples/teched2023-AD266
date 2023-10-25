# Exercise 4 - Use SAP Cloud SDK for consuming a remote OpenAPI service

In the previous exercises you learnt how to use CAP to consume a remote SuccessFactors OData service.
But, currently consuming a remote OpenAPI service is not supported by CAP.

In this exercise, we will learn how you can leverage SAP Cloud SDK to consume a remote OpenAPI service.
We will introduce a synthetic OpenAPI Signup Service.
//Todo: Explain a bit about what end points the service offers

## Exercise 4.1 - Add SAP Cloud SDK to your project and generate a typed OpenAPI client

1. In your project's application `pom.xml`(/srv/pom.xml) file add the following dependency to the dependencies section:
    ```xml
        <!-- Cloud SDK Dependency for Cloud Foundry -->
        <dependency>
            <groupId>com.sap.cloud.sdk.cloudplatform</groupId>
            <artifactId>scp-cf</artifactId>
        </dependency>
   
        <!-- Cloud SDK OData & Destinations -->
        <dependency>
            <groupId>com.sap.cloud.sdk.datamodel</groupId>
            <artifactId>odata-client</artifactId>
        </dependency>
        <dependency>
            <groupId>com.sap.cloud.sdk.cloudplatform</groupId>
            <artifactId>cloudplatform-connectivity</artifactId>
        </dependency>
   
        <!-- Cloud SDK OData Generated VDM -->
        <!--<dependency>
            <groupId>com.sap.cloud.sdk.datamodel</groupId>
            <artifactId>odata-v4-core</artifactId>
        </dependency>-->
        <dependency>
            <groupId>com.sap.cloud.sdk.datamodel</groupId>
            <artifactId>odata-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>javax.inject</groupId>
            <artifactId>javax.inject</artifactId>
            <scope>provided</scope>
        </dependency>
    ```
   Additionally, for type safe access to the SuccessFactors OData service, we will generate a [typed OData client](https://sap.github.io/cloud-sdk/docs/java/features/odata/vdm-generator).
   Add the following lines under the `<plugin>` section in your application `pom.xml` file.

   ```xml
            <!-- Cloud SDK OData VDM Generator -->
            <plugin>
                <groupId>com.sap.cloud.sdk.datamodel</groupId>
                <artifactId>odata-generator-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <id>generate-consumption</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                        <configuration>
                            <inputDirectory>${project.basedir}/external/TodoEntryV2.edmx</inputDirectory>
                            <outputDirectory>${project.basedir}/src/gen/java</outputDirectory>
                            <deleteOutputDirectory>false</deleteOutputDirectory>
                            <packageName>cloudsdk.gen</packageName>
                            <defaultBasePath>odata/v2/</defaultBasePath>
                            <compileScope>COMPILE</compileScope>
                            <serviceMethodsPerEntitySet>true</serviceMethodsPerEntitySet>
                            <serviceNameMappingFile>${project.build.directory}/serviceNameMappings.properties</serviceNameMappingFile>
                            <overwriteFiles>true</overwriteFiles>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
   ```
   You could find more details about the plugin parameters [here](https://sap.github.io/cloud-sdk/docs/java/features/odata/vdm-generator#available-parameters)

2. In your project's root `pom.xml` file add the following dependency to the dependency management section as the last entry (below the `cds-services-bom` and `spring-boot-dependencies`):
    ```xml
    <!-- Cloud SDK -->
    <dependency>
        <groupId>com.sap.cloud.sdk</groupId>
        <artifactId>sdk-bom</artifactId>
        <version>4.24.0</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
    ```
3. Compile the application now using `mvn compile`, you can see that under `/src/gen/java/cloudsdk.gen` folder the typed OData client classes are generated.
4. You can now use the generated client to consume the SuccessFactors OData service for the same set of tasks accomplished in the previous exercise. 

## Exercise 4.2 - Use Typed OData client to consume SuccessFactors Todo Service

1. Let's start using the generated client in the `GeneratedCodeRemoteServiceHandler`.
   Add the following code inside the class. 
   ```java
   private static final TodoEntryV2Service service = new DefaultTodoEntryV2Service().withServicePath("/odata/v2/TodoEntryV2");

   private HttpDestination getDestination() {
   return DestinationAccessor.getDestination("TODO_SERVICE").asHttp();
   }
   ```
   `DestinationAccessor` is a utility class that helps fetching destinations either from the environmental variables or from the destination service depending upon where the application is running.
   `DefaultTodoEntryV2Service` is the generated client class that provides the typed access to the SuccessFactors OData service.
   We have to modify the service path here using the `withServicePath` method as the service behaves slightly different from what is defined in the specification file.

   1. Now let's write code for the `@On( event = GetTodoSuggestionContext.CDS_NAME)` event.
      Add the following code inside the `getCurrentToDos(user)` method.
   ```java
      public List<TodoEntryV2> getCurrentToDos(String userName) {
          var destination = getDestination();
          final ValueBoolean filter = FieldReference.of("userId").asString().equalTo(userName);
          return service.getAllTodoEntryV2()
                        .filter(new ExpressionFluentHelper<>(filter))
                        .executeRequest(destination);
      }      
     ```
      Here we are using the generated client to fetch all the todo entries from the SuccessFactors OData service and filter them based on a particular username.
   
   2. Now let's write code for the `@On( event = AddTodoContext.CDS_NAME)`.
      Add the following code under the `addSuggestedToDo(final AddTodoContext context)` method:
   ```java
      @On( event = AddTodoContext.CDS_NAME)
      public void addSuggestedToDo(final AddTodoContext context)
      {
        final TodoEntryV2 todoEntryV2 = TodoEntryV2.builder().todoEntryName(context.getTodo().getTitle())
                .status(3)
                .build();

        todoEntryV2.setCustomField("userNav", Helper.getUserNavMap(extractUser(context)));

        final ModificationResponse<TodoEntryV2> todoEntryV2ModificationResponse = service.createTodoEntryV2(todoEntryV2).executeRequest(getDestination());
        context.setCompleted();
      }
    ```
   Here we are using the generated client to create a new todo entry in the SuccessFactors OData service. 
   You can see that the `userNav` field is set using the `Helper.getUserNavMap(extractUser(context))` method.
   This is required to be explicitly sent while creating a Todo as explained by the [SuccessFactors OData service documentation](https://help.sap.com/docs/SAP_SUCCESSFACTORS_PLATFORM/d599f15995d348a1b45ba5603e2aba9b/cb29d40f60594c559dd1251e084cdcf7.html#use-case-2%3A-create-a-to-do-item-for-user).

## Summary

You've now successfully learnt how to use SAP Cloud SDK to consume SuccessFactors OData service in a type safe manner.

Continue to - [Exercise 5 - Deploying the application to SAP Business Technology Platform](../ex5/README.md)
