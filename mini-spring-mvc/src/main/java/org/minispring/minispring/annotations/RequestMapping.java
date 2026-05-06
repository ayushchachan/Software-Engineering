package org.minispring.minispring.annotations;

import org.minispring.minispring.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ============================================================
 * ANNOTATION: @RequestMapping
 * ============================================================
 *
 * This annotation maps (connects) a specific URL path and HTTP method
 * to a controller method.
 *
 * In simple words: "When someone visits THIS URL using THIS HTTP method,
 * call THIS Java method."
 *
 * EXAMPLE:
 *   @RequestMapping(path = "/product-list", method = RequestMethod.GET)
 *   public List<Product> getAllProducts() { ... }
 *
 *   Meaning: "When a GET request arrives for /product-list,
 *             call getAllProducts()."
 *
 * THE TWO ATTRIBUTES:
 *   path()    — the URL path to map (e.g. "/product-list")
 *               default is "" (empty) if not specified
 *
 *   method()  — the HTTP method (GET, POST, etc.)
 *               default is GET if not specified
 *
 * HOW THE FRAMEWORK USES IT:
 *   SimpleHandlerMapping scans all methods inside @Controller classes.
 *   For each method that has @RequestMapping, it reads the 'path' and
 *   stores the mapping:  path → HandlerMethod (controller + method)
 *   Later, DispatcherServlet looks up the path for each request.
 *
 * META-ANNOTATIONS EXPLAINED:
 *   @Retention(RUNTIME)   — annotation survives to runtime for Reflection
 *   @Target(METHOD)       — can only be placed on METHODS, not classes
 *
 * In real Spring, @RequestMapping also supports path variables (/{id}),
 * multiple paths, headers, produces/consumes media types, and more.
 * ============================================================
 */
@Retention(RetentionPolicy.RUNTIME) // must be readable at runtime by SimpleHandlerMapping
@Target(ElementType.METHOD)         // applies to individual methods inside a controller
public @interface RequestMapping {

    /**
     * The URL path this method handles, e.g. "/product-list" or "/api/products/add".
     * Defaults to empty string if not set.
     */
    String path() default "";

    /**
     * The HTTP method this mapping responds to (GET or POST).
     * Defaults to GET if not specified.
     */
    RequestMethod method() default RequestMethod.GET;
}
