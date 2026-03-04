package com.invaders99.util;

import java.lang.reflect.Field;

/**
 * Serializes plain Java objects into JSON for Firebase REST APIs.
 * Supports: String, long, int, double, float, boolean.
 */
public final class FirebaseJson {

    private FirebaseJson() {}

    /** Object to Firestore document JSON: {"fields":{...}} */
    public static String toFirestoreDocument(Object obj) {
        StringBuilder sb = new StringBuilder("{\"fields\":{");
        appendFields(sb, obj, true);
        sb.append("}}");
        return sb.toString();
    }

    /** Plain JSON for Realtime Database */
    public static String toJson(Object obj) {
        StringBuilder sb = new StringBuilder("{");
        appendFields(sb, obj, false);
        sb.append("}");
        return sb.toString();
    }

    private static void appendFields(StringBuilder sb, Object obj, boolean firestoreFormat) {
        Field[] fields = obj.getClass().getDeclaredFields();
        boolean first = true;
        for (Field field : fields) {
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                continue;
            }
            if (value == null) continue;

            if (!first) sb.append(",");
            first = false;

            sb.append("\"").append(field.getName()).append("\":");

            if (firestoreFormat) {
                appendFirestoreValue(sb, value);
            } else {
                appendJsonValue(sb, value);
            }
        }
    }

    private static void appendFirestoreValue(StringBuilder sb, Object value) {
        if (value instanceof String) {
            sb.append("{\"stringValue\":\"").append(escapeJson((String) value)).append("\"}");
        } else if (value instanceof Long || value instanceof Integer) {
            sb.append("{\"integerValue\":\"").append(value).append("\"}");
        } else if (value instanceof Double || value instanceof Float) {
            sb.append("{\"doubleValue\":").append(value).append("}");
        } else if (value instanceof Boolean) {
            sb.append("{\"booleanValue\":").append(value).append("}");
        } else {
            sb.append("{\"stringValue\":\"").append(escapeJson(value.toString())).append("\"}");
        }
    }

    private static void appendJsonValue(StringBuilder sb, Object value) {
        if (value instanceof String) {
            sb.append("\"").append(escapeJson((String) value)).append("\"");
        } else {
            sb.append(value);
        }
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
