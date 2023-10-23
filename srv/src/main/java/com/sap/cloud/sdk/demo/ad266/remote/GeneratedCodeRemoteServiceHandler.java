package com.sap.cloud.sdk.demo.ad266.remote;

import cds.gen.todoservice.TodoService_;
import cloudsdk.gen.namespaces.todoentryv2.TodoEntryV2;
import cloudsdk.gen.services.DefaultTodoEntryV2Service;
import cloudsdk.gen.services.TodoEntryV2Service;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;
import com.sap.cloud.sdk.datamodel.odata.helper.ExpressionFluentHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@ServiceName(TodoService_.CDS_NAME)
public class GeneratedCodeRemoteServiceHandler implements EventHandler
{
    private static final TodoEntryV2Service service = new DefaultTodoEntryV2Service().withServicePath("/odata/v2/TodoEntryV2");

    private HttpDestination getDestination() {
        return DestinationAccessor.getDestination("SFSF-BASIC-ADMIN").asHttp();
    }

    public List<TodoEntryV2> getCurrentToDos(String userName) {
        var destination = getDestination();

        final ValueBoolean filter = FieldReference.of("userId").asString().equalTo(userName);

        return service.getAllTodoEntryV2()
                .filter(new ExpressionFluentHelper<>(filter))
                .executeRequest(destination);
    }

/*
    @On(event = QuitContext.CDS_NAME)
    public void quit( QuitContext context )
    {
        var todos = getCurrentToDos(Helper.getUser(context));

        var changeSet = service.batch().beginChangeSet();
        todos.forEach(changeSet::deleteTodoEntryV2);
        var batch = changeSet.endChangeSet();

        // execute & check the results
        int failedDeletes = 0;
        try (BatchResponse batchResponse = batch.executeRequest(getDestination())) {
            for( int i = 0; i< todos.size(); i++ ) {
                try{
                    batchResponse.get(i);
                } catch (ODataResponseException e) {
                    log.warn("Failed to delete ToDo from the server: {}", e.getMessage());
                    failedDeletes++;
                }
            }
        } catch (ODataResponseException e ){
            log.error("Failed to delete ToDos from the server: {}", e.getMessage());
            log.error(e.getHttpBody().get());
            throw e;
        }
        String resultMessage = String.format("Deleted %s ToDos from the server.", todos.size() - failedDeletes);
        log.info(resultMessage);
        if( failedDeletes > 0 ) {
            resultMessage += String.format(" Failed to delete %s ToDos from the server.", failedDeletes);
        }
        context.setResult(resultMessage);
    }*/
}
