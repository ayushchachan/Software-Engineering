package org.minispring.minispring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ============================================================
 * ANNOTATION: @RequestParam
 * ============================================================
 *
 * This annotation is placed on a METHOD PARAMETER to tell the framework:
 * "Extract the value of this named query parameter from the URL and
 * pass it as the argument to this method."
 *
 * WHAT IS A QUERY PARAMETER?
 * A query parameter is the key=value data you put after "?" in a URL.
 *   URL:  /find-product?id=42&color=blue
 *   Query parameters:  id=42  and  color=blue
 *
 * EXAMPLE:
 *   @RequestMapping(path = "/find-product/{id}", method = RequestMethod.GET)
 *   public String findProduct(@RequestParam("id") String productId, Response res) {
 *       // productId will automatically be "42" if URL is /find-product?id=42
 *   }
 *
 * HOW THE FRAMEWORK USES IT:
 *   DispatcherServlet's argument resolver checks each method parameter.
 *   If a parameter has @RequestParam("id"), it calls:
 *     request.getParameter("id")
 *   to fetch the value from the URL's query string and passes it in.
 *
 * THE ATTRIBUTE:
 *   value() — the name of the query parameter to look for in the URL.
 *             e.g. @RequestParam("id") means "find ?id=... in the URL"
 *
 * META-ANNOTATIONS EXPLAINED:
 *   @Target(PARAMETER)    — can only be placed on METHOD PARAMETERS
 *   @Retention(RUNTIME)   — visible at runtime so DispatcherServlet can read it
 *
 * In real Spring, @RequestParam also supports:
 *   required = false  (make the parameter optional)
 *   defaultValue = "..."  (use a fallback if not provided)
 * ============================================================
 */
@Target(ElementType.PARAMETER)      // applies to method parameters only
@Retention(RetentionPolicy.RUNTIME) // must be readable at runtime by DispatcherServlet
public @interface RequestParam {

    /**
     * The name of the query parameter to extract from the URL.
     * For example, @RequestParam("id") extracts the value of ?id=...
     */
    String value(); // required — you must always specify the parameter name
}