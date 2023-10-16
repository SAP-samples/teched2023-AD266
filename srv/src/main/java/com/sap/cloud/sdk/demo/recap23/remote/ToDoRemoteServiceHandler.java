package com.sap.cloud.sdk.demo.recap23.remote;

import cds.gen.todoentryv2.TodoEntryV2;

import java.util.List;

public interface ToDoRemoteServiceHandler {
    List<TodoEntryV2> getCurrentToDos();

    TodoEntryV2 addToDo(TodoEntryV2 todo);

    String quit();

}