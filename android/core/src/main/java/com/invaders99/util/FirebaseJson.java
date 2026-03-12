package com.invaders99.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * Serializes plain Java objects into JSON for Firebase REST APIs.
 * Supports: String, long, int, double, float, boolean, Collection, Map.
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
        if (obj == null) return "null";
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
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            sb.append("\"").append(escapeJson((String) value)).append("\"");
        } else if (value instanceof Boolean || value instanceof Number) {
            sb.append(value);
        } else if (value instanceof Collection) {
            sb.append("[");
            boolean first = true;
            for (Object item : (Collection<?>) value) {
                if (!first) sb.append(",");
                first = false;
                appendJsonValue(sb, item);
            }
            sb.append("]");
        } else if (value instanceof Map) {
            sb.append("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(escapeJson(String.valueOf(entry.getKey()))).append("\":");
                appendJsonValue(sb, entry.getValue());
            }
            sb.append("}");
        } else {
            // Treat as object
            sb.append("{");
            appendFields(sb, value, false);
            sb.append("}");
        }
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
