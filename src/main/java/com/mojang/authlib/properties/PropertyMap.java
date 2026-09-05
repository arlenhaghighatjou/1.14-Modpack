package com.mojang.authlib.properties;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.json.JSONArray;
import org.json.JSONObject;

public class PropertyMap extends ForwardingMultimap<String, Property> {
    private final Multimap<String, Property> properties = LinkedHashMultimap.create();

    public PropertyMap() {
    }

    public static PropertyMap fromJson(Object json) {
        PropertyMap result = new PropertyMap();
        if (json instanceof JSONObject) {
            JSONObject object = (JSONObject) json;
            for (String key : object.keySet()) {
                JSONArray arr = object.optJSONArray(key);
                if (arr != null) {
                    for (int i = 0; i < arr.length(); i++) {
                        Object element = arr.get(i);
                        if (element instanceof String) {
                            result.put(key, new Property(key, (String) element));
                        } else if (element instanceof JSONObject) {
                            JSONObject propObj = (JSONObject) element;
                            String value = propObj.optString("value", null);
                            String signature = propObj.optString("signature", null);
                            if (value != null) {
                                if (signature != null) {
                                    result.put(key, new Property(key, value, signature));
                                } else {
                                    result.put(key, new Property(key, value));
                                }
                            }
                        }
                    }
                }
            }
        } else if (json instanceof JSONArray) {
            JSONArray arr = (JSONArray) json;
            for (int i = 0; i < arr.length(); i++) {
                Object element = arr.get(i);
                if (element instanceof JSONObject) {
                    JSONObject propObj = (JSONObject) element;
                    String name = propObj.optString("name", null);
                    String value = propObj.optString("value", null);
                    String signature = propObj.optString("signature", null);
                    if (name != null && value != null) {
                        if (signature != null) {
                            result.put(name, new Property(name, value, signature));
                        } else {
                            result.put(name, new Property(name, value));
                        }
                    }
                }
            }
        }
        return result;
    }

    protected Multimap<String, Property> delegate() {
        return this.properties;
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public JSONArray toJsonArray() {
        JSONArray result = new JSONArray();
        for (Property property : values()) {
            JSONObject object = new JSONObject();
            object.put("name", property.getName());
            object.put("value", property.getValue());
            if (property.hasSignature()) {
                object.put("signature", property.getSignature());
            }
            result.put(object);
        }
        return result;
    }

    public JSONObject toJsonObject() {
        JSONObject result = new JSONObject();
        for (String key : properties.keySet()) {
            JSONArray arr = new JSONArray();
            for (Property property : properties.get(key)) {
                JSONObject object = new JSONObject();
                object.put("name", property.getName());
                object.put("value", property.getValue());
                if (property.hasSignature()) {
                    object.put("signature", property.getSignature());
                }
                arr.put(object);
            }
            result.put(key, arr);
        }
        return result;
    }
} 