package org.minispring.minispring;

/**
 * ============================================================
 * ENUM: RequestMethod
 * ============================================================
 *
 * An "enum" is a special Java type that defines a fixed set of constants.
 * Here we use it to represent the two HTTP methods our framework supports.
 *
 * WHAT IS AN HTTP METHOD?
 * When a browser or Postman sends a request, it tells the server WHAT it
 * wants to do by using an HTTP method:
 *
 *   GET  — "Give me some data" (e.g. fetch a list of products)
 *   POST — "Here is some data, save it" (e.g. add a new product)
 *
 * There are more methods in real HTTP (PUT, DELETE, PATCH, etc.), but
 * we only implement GET and POST for simplicity.
 *
 * HOW IT IS USED:
 *   In @RequestMapping we specify which method a URL should respond to:
 *
 *     @RequestMapping(path = "/product-list", method = RequestMethod.GET)
 *     public List<Product> getAllProducts() { ... }
 *
 *   Using an enum instead of a plain String prevents typos like "GTE"
 *   because the compiler will catch any invalid value at compile time.
 *
 * This is the same concept as Spring's org.springframework.web.bind.annotation.RequestMethod.
 * ============================================================
 */
public enum RequestMethod {
    GET,  // Used for read-only requests (fetching data)
    POST  // Used for write requests (creating/adding data)
}
