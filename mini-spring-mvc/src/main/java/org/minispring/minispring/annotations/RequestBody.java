package org.minispring.minispring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ============================================================
 * ANNOTATION: @RequestBody
 * ============================================================
 *
 * This annotation is placed on a METHOD PARAMETER to tell the framework:
 * "Read the body of the HTTP request, parse it as JSON, and convert it
 * into a Java object of this parameter's type."
 *
 * It is used for POST (and PUT) requests where the client sends data
 * in the request body (not in the URL).
 *
 * EXAMPLE:
 *   @RequestMapping(path = "/api/products/add", method = RequestMethod.POST)
 *   public String addProduct(@RequestBody Product newProduct) {
 *       // newProduct is already a fully-filled Product object
 *       // The framework read the JSON body and converted it for us
 *   }
 *
 *   Client sends:
 *     POST /api/products/add
 *     Content-Type: application/json
 *
 *     {"name": "MacBook Pro", "price": 200000}
 *
 *   Framework does:
 *     1. Reads the JSON string from the request body
 *     2. Sees @RequestBody on the 'newProduct' parameter
 *     3. Calls JsonUtils.fromJson(jsonString, Product.class)
 *     4. Passes the resulting Product object to your method
 *
 * META-ANNOTATIONS EXPLAINED:
 *   @Retention(RUNTIME)    — readable at runtime via Reflection
 *   @Target(PARAMETER)     — can only be placed on METHOD PARAMETERS
 *                            (unlike @Controller which goes on a class)
 *
 * In real Spring, @RequestBody works the same way.
 * ============================================================
 */
@Retention(RetentionPolicy.RUNTIME) // must be visible at runtime so DispatcherServlet can read it
@Target(ElementType.PARAMETER)      // goes on method parameters only, not classes or fields
public @interface RequestBody {
    // No attributes needed
}
