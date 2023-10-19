package com.sap.cloud.sdk.demo.recap23;

import cds.gen.todogeneratorservice.GetTodoSuggestionContext;
import cds.gen.todogeneratorservice.TodoGeneratorService_;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ServiceName(TodoGeneratorService_.CDS_NAME)
public class ToDoController implements EventHandler
{
    @On( event = GetTodoSuggestionContext.CDS_NAME)
    public void getTodoSuggestion(final GetTodoSuggestionContext context)
    {
    }
}
