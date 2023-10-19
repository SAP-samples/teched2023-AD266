package com.sap.cloud.sdk.demo.recap23;

import cds.gen.todoentryv2.TodoEntryV2;
import cds.gen.todoentryv2.TodoEntryV2Model_;
import cds.gen.todoentryv2.TodoEntryV2_;
import cds.gen.todogeneratorservice.AddTodoContext;
import cds.gen.todogeneratorservice.GeneratedTodo;
import cds.gen.todogeneratorservice.GetTodoSuggestionContext;
import cds.gen.todogeneratorservice.QuitContext;
import cds.gen.todogeneratorservice.TodoGeneratorService_;
import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestDelete;
import com.sap.cloud.sdk.datamodel.odata.client.request.UriEncodingStrategy;
import com.sap.cloud.sdk.demo.recap23.remote.utility.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sap.cloud.sdk.demo.recap23.remote.utility.Helper.extractUser;
import static com.sap.cloud.sdk.demo.recap23.remote.utility.Helper.generateTodoSuggestion;
import static io.vavr.API.print;

@Slf4j
@Component
@ServiceName(TodoGeneratorService_.CDS_NAME)
public class CqnRemoteServiceHandler implements EventHandler
{
    @Autowired
    @Qualifier(TodoEntryV2Model_.CDS_NAME)
    private CqnService cqnToDoService;

    @On( event = GetTodoSuggestionContext.CDS_NAME)
    public void getTodoSuggestion(final GetTodoSuggestionContext context)
    {
        String user = extractUser(context);

        final var toDos = getTodoEntryV2s(user);
        final List<String> entryNames = toDos.stream()
                .peek(e -> log.info("{}", e))
                .map(TodoEntryV2::getTodoEntryName).collect(Collectors.toList());
        final GeneratedTodo generatedTodo = generateTodoSuggestion(entryNames);
        context.setResult(generatedTodo);
    }

    private List<TodoEntryV2> getTodoEntryV2s(String user) {
        var query = Select.from(TodoEntryV2_.class)
                .where(e -> e.userId().eq(user))
                .limit(10);

        var toDos = cqnToDoService.run(query).listOf(TodoEntryV2.class);

        log.info("Got the following ToDos from the server:");
        return toDos;
    }

    @On( event = AddTodoContext.CDS_NAME)
    public void addSuggestedToDo(final AddTodoContext context)
    {
        final TodoEntryV2 toDo = TodoEntryV2.create();

        final Object userNav = Helper.getUserNavMap(extractUser(context));
        toDo.put("userNav", userNav);
        toDo.setStatus(3);
        toDo.setTodoEntryName(context.getTodo().getTitle());

        var query = Insert.into(TodoEntryV2_.class).entry(toDo);
        cqnToDoService.run(query).single(TodoEntryV2.class);

        context.setCompleted();
    }

    @On(event = QuitContext.CDS_NAME)
    public void quit( QuitContext context )
    {
        return;
        /*final List<TodoEntryV2> todos = getTodoEntryV2s(extractUser(context));

        var deleteRequests = todos.stream()
                .map(TodoEntryV2::getTodoEntryId)
                .map(id -> Delete.from(TodoEntryV2_.class).byId(id))
                .toList();

        int failedDeletes = 0;
        for (Delete<TodoEntryV2_> deleteRequest : deleteRequests) {
            try {
                cqnToDoService.run(deleteRequest);
            } catch (Exception e) {
                log.warn("Failed to delete ToDo from the server: {}", e.getMessage());
                failedDeletes++;
            }
        }

        String resultMessage = String.format("Deleted %s ToDos from the server.", deleteRequests.size() - failedDeletes);
        log.info(resultMessage);
        if (failedDeletes > 0) {
            resultMessage += String.format(" Failed to delete %s ToDos from the server.", failedDeletes);
        }*/
    }
}
