package com.sap.cloud.sdk.demo.recap23.utility;

import cds.gen.todogeneratorservice.GeneratedTodo;
import com.google.gson.JsonObject;
import com.sap.cds.services.EventContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Helper {

    public static JsonObject getUserNav(String userName){
        JsonObject userNav = new JsonObject();
        JsonObject nestedUserNav = new JsonObject();
        nestedUserNav.addProperty("uri", "User('"+userName+"')");
        nestedUserNav.addProperty("type","SFOData.User");
        userNav.add("__metadata", nestedUserNav);
        return userNav;
    }

    public static Map<String, Object> getUserNavMap(String userName){
        Map<String, Object> userNav = new LinkedHashMap<>();
        Map<String, String> nestedUserNav = new LinkedHashMap<>();
        nestedUserNav.put("uri", "User('"+userName+"')");
        nestedUserNav.put("type","SFOData.User");
        userNav.put("__metadata", nestedUserNav);
        return userNav;
    }


    public static String extractUser(final EventContext context) {
        String user = context.getParameterInfo().getQueryParameter("user");
        if ( user == null ) {
            user = "sfadmin";
        }
        return user;
    }

    public static GeneratedTodo generateTodoSuggestion(List<String> input) {
        // TODO: Implement sophisticated algorithm to generate a ToDo suggestion based on the existing ToDos
        var result = GeneratedTodo.create();
        result.setTitle("Hello World!");
        if( input.isEmpty() ) {
            result.setDescription("Write a 'Hello World!' application with CAP Java.");
        } else {
            result.setDescription("Write a 'Hello World!' application with CAP Java, then finish the other " + input.size() + " ToDos ;)");
        }
        return result;
    }
}