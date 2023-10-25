package com.sap.cloud.sdk.demo.ad266.utility;

import com.google.gson.JsonObject;
import com.sap.cds.services.EventContext;

import java.util.LinkedHashMap;
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

    public static Map<String, Object> getUserNavAsMap(String userName)
    {
        Map<String, Object> userNav = new LinkedHashMap<>();
        Map<String, String> nestedUserNav = new LinkedHashMap<>();
        nestedUserNav.put("uri", "User('"+userName+"')");
        nestedUserNav.put("type","SFOData.User");
        userNav.put("__metadata", nestedUserNav);
        return userNav;
    }
}