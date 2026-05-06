package org.minispring.minispring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * ============================================================
 * CLASS: JsonUtils
 * ============================================================
 *
 * This is a UTILITY class that provides helper methods for converting
 * between Java objects and JSON strings (and vice versa).
 *
 * JSON (JavaScript Object Notation) is the standard format for sending
 * data over HTTP. For example:
 *   Java: new Product("iPhone 15", 85000)
 *   JSON: {"name":"iPhone 15","price":85000.0}
 *
 * We use the Jackson library (ObjectMapper) to handle the conversion.
 * Jackson is the same library that real Spring Boot uses internally.
 *
 * TWO KEY OPERATIONS:
 *   toJson()   — Java Object → JSON String   (used when SENDING responses)
 *   fromJson() — JSON String → Java Object   (used when READING request bodies)
 *
 * WHY ONE SHARED ObjectMapper?
 * Creating an ObjectMapper is expensive. By making it a single
 * static instance (shared across all calls), we avoid that cost
 * on every request. This is a standard performance best practice.
 *
 * NOTE: The large commented-out block at the bottom is an old manual
 * JSON serializer we wrote before switching to Jackson. It is kept
 * for reference to show how JSON conversion works under the hood.
 * ============================================================
 */
public class JsonUtils {

    // One shared ObjectMapper instance for the entire application.
    // "static final" means it is created once and never replaced.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts any Java object into a JSON string.
     *
     * Examples:
     *   toJson(new Product("iPhone", 85000))
     *     → '{"name":"iPhone","price":85000.0}'
     *
     *   toJson(List.of(p1, p2))
     *     → '[{"name":"iPhone",...},{"name":"Samsung",...}]'
     *
     * @param object the Java object to convert (can be anything)
     * @return a JSON string representation of that object
     * @throws RuntimeException if the object cannot be serialized
     */
    public static String toJson(Object object) {
        try {
            // objectMapper.writeValueAsString() does all the heavy lifting
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    /**
     * Converts a JSON string into a Java object of a specific type.
     *
     * Example:
     *   String json = '{"name":"MacBook","price":200000.0}';
     *   Product p = fromJson(json, Product.class);
     *   // p.getName() → "MacBook"
     *
     * The generic type <T> means this method works with ANY class,
     * not just Product — you tell it which type you want via the clazz parameter.
     *
     * @param json  the JSON string to parse
     * @param clazz the Java class to convert into (e.g. Product.class)
     * @param <T>   the type of the returned object (inferred from clazz)
     * @return a new Java object of type T with fields filled from the JSON
     * @throws RuntimeException if the JSON cannot be parsed or doesn't match the class
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            // objectMapper.readValue() parses the JSON and builds the object
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON string", e);
        }
    }

    // ================================================================
    // BELOW: Old manual JSON serializer (kept for educational reference)
    // This shows what Jackson does internally — field-by-field conversion.
    // We replaced it with Jackson because it handles edge cases better.
    // ================================================================

    // public static String toJson(Object object) {
    //
    //     if (object == null) return "null";
    //
    //     // If it's a Number or String, just return it directly
    //     if (object instanceof Number) {
    //         return object.toString();
    //     }
    //
    //     if (object instanceof String) {
    //         return "\"" + object + "\"";
    //     }
    //
    //     // If it's a List, wrap each element in [ ] separated by commas
    //     if (object instanceof List<?>) {
    //         List<?> list = (List<?>) object;
    //         StringBuilder json = new StringBuilder("[");
    //         for (int i = 0; i < list.size(); i++) {
    //             json.append(toJson(list.get(i)));
    //             if (i < list.size() - 1) {
    //                 json.append(", ");
    //             }
    //         }
    //         json.append("]");
    //         return json.toString();
    //     }
    //
    //     // For any other object, wrap its fields in { }
    //     StringBuilder json = new StringBuilder("{");
    //     Class<?> clazz = object.getClass();
    //     Field[] fields = clazz.getDeclaredFields();
    //
    //     try {
    //         for (int i = 0; i < fields.length; i++) {
    //             Field field = fields[i];
    //             field.setAccessible(true); // unlock private fields
    //             json.append("\"" + field.getName() + "\":");
    //             json.append(toJson(field.get(object)));
    //             if (i < fields.length - 1) {
    //                 json.append(",");
    //             }
    //         }
    //     } catch (IllegalAccessException e) {
    //         throw new RuntimeException(e);
    //     }
    //     json.append("}");
    //     return json.toString();
    // }
}
