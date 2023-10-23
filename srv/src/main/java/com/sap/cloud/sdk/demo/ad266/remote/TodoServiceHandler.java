package com.sap.cloud.sdk.demo.ad266.remote;

import cds.gen.todoentryv2.TodoEntryV2;
import cds.gen.todoentryv2.TodoEntryV2Model_;
import cds.gen.todoentryv2.TodoEntryV2_;
import cds.gen.todoservice.AddTodoContext;
import cds.gen.todoservice.GetTodoSuggestionContext;
import cds.gen.todoservice.QuitContext;
import cds.gen.todoservice.TodoService_;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cloud.sdk.demo.ad266.utility.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sap.cloud.sdk.demo.ad266.utility.Helper.extractUser;

@Slf4j
@Component
@ServiceName(TodoService_.CDS_NAME)
public class TodoServiceHandler implements EventHandler
{
    @Autowired
    @Qualifier(TodoEntryV2Model_.CDS_NAME)
    private CqnService todoService;

    private List<TodoEntryV2> getTodos(String user)
    {
        var query = Select.from(TodoEntryV2_.class)
                .where(e -> e.userId().eq(user))
                .limit(10);

        var toDos = todoService.run(query).listOf(TodoEntryV2.class);

        log.info("Got the following ToDos from the server:");
        return toDos;
    }

    @On( event = AddTodoContext.CDS_NAME)
    public void addSuggestedToDo(final AddTodoContext context)
    {
        var toDo = TodoEntryV2.create();

        var userNav = Helper.getUserNavAsMap(extractUser(context));
        toDo.put("userNav", userNav);
        toDo.setStatus(3);
        toDo.setTodoEntryName(context.getTodo().getTitle());

        var query = Insert.into(TodoEntryV2_.class).entry(toDo);
        todoService.run(query).single(TodoEntryV2.class);

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
