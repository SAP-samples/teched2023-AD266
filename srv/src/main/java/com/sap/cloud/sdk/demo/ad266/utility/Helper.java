package com.sap.cloud.sdk.demo.ad266.utility;

import cds.gen.todoservice.GeneratedTodo;
import com.google.gson.JsonObject;
import com.sap.cds.services.EventContext;
import com.sap.cds.services.authentication.AuthenticationInfo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Helper {

    public static JsonObject getUserNavAsJson(String userName){
        JsonObject userNav = new JsonObject();
        JsonObject nestedUserNav = new JsonObject();
        nestedUserNav.addProperty("uri", "User('"+userName+"')");
        nestedUserNav.addProperty("type","SFOData.User");
        userNav.add("__metadata", nestedUserNav);
        return userNav;
    }

    public static Map<String, Object> getUserNavAsMap(String userName){
        Map<String, Object> userNav = new LinkedHashMap<>();
        Map<String, String> nestedUserNav = new LinkedHashMap<>();
        nestedUserNav.put("uri", "User('"+userName+"')");
        nestedUserNav.put("type","SFOData.User");
        userNav.put("__metadata", nestedUserNav);
        return userNav;
    }

    public static String getUser(final EventContext context)
    {
        // TODO extract SFSF user ID from the authenticated user's attributes
        // context.getUserInfo().getAttributes();

        // for testing use a query parameter or a default value
        String user = context.getParameterInfo().getQueryParameter("user");
        if ( user == null ) {
            user = "SF0001";
        }
        return user;
    }
}