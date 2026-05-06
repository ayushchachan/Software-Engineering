package org.minispring.minispring;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================
 * CLASS: Request
 * ============================================================
 *
 * This class represents ONE incoming HTTP request.
 * It reads the raw bytes from the network socket and gives you a clean,
 * easy-to-use object with sensible getter methods.
 *
 * Think of it as a "parsed version" of the raw HTTP message.
 *
 * WHAT AN HTTP REQUEST LOOKS LIKE (raw text over the network):
 * ┌────────────────────────────────────────────────────────────┐
 * │ POST /api/products/add HTTP/1.1                            │  ← request line
 * │ Host: localhost:8080                                       │  ← headers
 * │ Content-Type: application/json                            │
 * │                                                           │  ← blank line
 * │ {"name":"MacBook","price":200000}                         │  ← body (JSON)
 * └────────────────────────────────────────────────────────────┘
 *
 * This class parses all three parts:
 *   - method       : "POST" or "GET"
 *   - path         : "/api/products/add"  (without query string)
 *   - queryParams  : map of key=value pairs from "?id=1&name=iphone"
 *   - body         : the JSON string after the blank line
 *
 * It is similar to Spring's HttpServletRequest.
 * ============================================================
 */
public class Request {

    // The HTTP method, e.g. "GET" or "POST"
    private String method;

    // The URL path WITHOUT the query string, e.g. "/product-list"
    private String path;

    // A map of query parameters, e.g. for "?id=1&name=iphone" this holds:
    //   { "id" → "1",  "name" → "iphone" }
    private Map<String, String> queryParams;

    // The raw request body — for POST requests this contains the JSON string
    private String body;

    /**
     * Constructor — reads and parses the full HTTP request from the socket.
     *
     * @param method  the HTTP method string, e.g. "GET"
     * @param rawPath the raw URL which may include query params, e.g. "/find?id=1"
     * @param reader  the BufferedReader connected to the client socket so we
     *                can keep reading headers and the body
     * @throws IOException if reading from the socket fails
     */
    public Request(String method, String rawPath, BufferedReader reader) throws IOException {
        this.method = method;
        this.queryParams = new HashMap<>();

        // Split the rawPath into:  /path  and  key=value&key=value
        parsePathAndParam(rawPath);

        // Read past the HTTP headers and grab the body (if any)
        parseBody(reader);
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPER: parsePathAndParam
    // Separates the URL into the path and the query string.
    // e.g. "/find-product?id=1&name=iphone"  →  path="/find-product",
    //                                             queryParams={"id":"1","name":"iphone"}
    // ----------------------------------------------------------------
    private void parsePathAndParam(String rawPath) {

        if (rawPath.contains("?")) {
            // There is a query string — split at the "?" character
            String[] parts = rawPath.split("\\?");
            this.path = parts[0]; // everything before "?"

            // Parse the key=value pairs after "?"
            String queryString = parts[1]; // e.g. "id=1&name=iphone"
            parseQueryString(queryString);
        } else {
            // No "?" → the whole rawPath is just the path, no query params
            this.path = rawPath;
        }
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPER: parseQueryString
    // Splits "id=1&name=iphone" into individual key=value pairs and
    // stores them in the queryParams map.
    // ----------------------------------------------------------------
    private void parseQueryString(String queryString) {

        // Split by "&" to get each  key=value  piece
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            // Split each piece by "=" to separate key from value
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                // Decode URL-encoded characters (e.g. "%20" → " ", "+" → " ")
                String key   = decode(keyValue[0]);
                String value = decode(keyValue[1]);
                this.queryParams.put(key, value);
            }
        }
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPER: decode
    // URL-decodes a string (handles special chars like %20, +, etc.)
    // ----------------------------------------------------------------
    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPER: parseBody
    // Skips over the HTTP headers and reads the body of the request.
    //
    // HTTP spec says:  headers and body are separated by a BLANK LINE.
    // So we keep reading lines until we hit an empty one, then read
    // whatever comes after as the body.
    // ----------------------------------------------------------------
    private void parseBody(BufferedReader reader) throws IOException {

        // STEP 1: Skip all header lines until we find the blank separator line
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break; // Found the blank line — body starts on the next read
            }
        }

        // STEP 2: Read the body character by character until there's nothing left.
        // reader.ready() returns true while there is data waiting to be read.
        StringBuilder payload = new StringBuilder();
        while (reader.ready()) {
            payload.append((char) reader.read());
        }

        // Store the body as a string (e.g. '{"name":"iPhone","price":85000}')
        this.body = payload.toString();
    }

    // ----------------------------------------------------------------
    // PUBLIC GETTERS — used by DispatcherServlet and controller methods
    // ----------------------------------------------------------------

    /**
     * Returns the HTTP method ("GET", "POST", etc.).
     *
     * @return the request method string
     */
    public String getMethod() {
        return method;
    }

    /**
     * Returns the URL path without the query string.
     * e.g. for "/find-product?id=1" this returns "/find-product"
     *
     * @return the path portion of the URL
     */
    public String getPath() {
        return path;
    }

    /**
     * Looks up a query parameter value by name.
     * e.g. for URL "/find?id=42", getParameter("id") returns "42"
     *
     * @param paramName the name of the query parameter
     * @return the value, or null if the parameter is not present
     */
    public String getParameter(String paramName) {
        return this.queryParams.get(paramName);
    }

    /**
     * Returns the raw request body as a string.
     * For POST requests with JSON, this is the JSON text.
     * For GET requests, this is typically an empty string.
     *
     * @return the request body string
     */
    public String getBody() {
        return this.body;
    }
}
