package org.minispring.minispring;

import org.minispring.minispring.annotations.RequestBody;
import org.minispring.minispring.annotations.RequestParam;
import org.minispring.minispring.JsonUtils;
import org.minispring.server.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.Socket;

/**
 * ============================================================
 * CLASS: DispatcherServlet
 * ============================================================
 *
 * This is the FRONT CONTROLLER of the Mini-Spring framework.
 * Every single HTTP request that comes into the server first arrives here.
 *
 * Think of it as the HEAD WAITER in a restaurant:
 *   - A customer (the HTTP client) walks in with an order (HTTP request).
 *   - The head waiter reads the order (parses method + path).
 *   - Looks up which chef (controller method) should handle it.
 *   - Passes the order to the right chef.
 *   - Takes the chef's output and sends it back to the customer (HTTP response).
 *
 * WHAT IT DOES STEP BY STEP:
 *   1. Reads the raw bytes from the client socket and builds a Request object.
 *   2. Asks SimpleHandlerMapping: "which method handles this URL?"
 *   3. If no match → sends HTTP 404 back to the client.
 *   4. If match found → inspects the method's parameters and fills them:
 *       - Is the parameter of type Request?  → pass our Request object
 *       - Is the parameter of type Response? → pass our Response object
 *       - Does the parameter have @RequestParam? → extract from query string
 *       - Does the parameter have @RequestBody?  → parse JSON body → object
 *   5. Calls (invokes) the controller method with those arguments.
 *   6. If the method returns a String → send it as plain text.
 *      If it returns any other object → convert to JSON, then send.
 *
 * This class implements HttpHandler so the MiniHttpServer can call it
 * without knowing anything about controllers or Spring.
 *
 * In real Spring, DispatcherServlet does this exact job but with far more
 * features (interceptors, view resolvers, error handling, etc.).
 * ============================================================
 */
public class DispatcherServlet implements HttpHandler {

    // Reference to the handler mapping — used to look up which controller
    // method should handle a given URL path
    SimpleHandlerMapping handlerMapping;

    /**
     * Constructor — stores the handler mapping for use in handle().
     *
     * @param handlerMapping the mapping of URL paths to controller methods
     */
    public DispatcherServlet(SimpleHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * Called by MiniHttpServer for every incoming HTTP connection.
     * This is where the full request-response cycle happens.
     *
     * @param clientSocket the raw TCP socket connected to the HTTP client
     */
    @Override
    public void handle(Socket clientSocket) {
        try {
            // --------------------------------------------------------
            // STEP 1: Read the raw HTTP request from the socket
            // --------------------------------------------------------

            // Wrap the socket's input stream in a BufferedReader so we
            // can read it line by line (text mode)
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Wrap the socket's output stream in a PrintWriter so we
            // can write text lines back to the client easily
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());

            // The very first line of an HTTP request looks like:
            //   "GET /product-list HTTP/1.1"
            // We read it here.
            String requestLine = reader.readLine();
            System.out.println("Incoming Request: " + requestLine);

            // Safety check: if the line is empty/null, something went wrong — abort
            if (requestLine == null) return;

            // Split the request line by spaces to get [method, path, protocol]
            //   parts[0] = "GET"   or "POST"
            //   parts[1] = "/product-list"
            //   parts[2] = "HTTP/1.1"  (we don't need this)
            String[] parts = requestLine.split(" ");
            String methodType = parts[0]; // e.g. "GET"
            String rawPath    = parts[1]; // e.g. "/product-list?id=1"

            // Build a Request object — it parses the path, query params,
            // and request body for us.  We pass the reader so it can
            // continue reading the rest of the HTTP message (headers + body).
            Request request = new Request(methodType, rawPath, reader);

            // Build a Response object — wraps the writer so controller
            // methods can easily send data back to the client
            Response response = new Response(writer);

            // --------------------------------------------------------
            // STEP 2: Find the right controller method for this URL
            // --------------------------------------------------------
            // Ask the handler mapping: "who handles /product-list?"
            // Returns a HandlerMethod (controller object + method) or null
            HandlerMethod handlerMethod = this.handlerMapping.getHandler(request.getPath());

            if (handlerMethod == null) {
                // No controller method registered for this URL → 404
                writer.println("HTTP/1.1 404 Not Found");
                writer.println(); // blank line required by HTTP spec
                writer.println("404 - Not Found (MiniSpring)");
                writer.flush();
                clientSocket.close();
                return;
            }

            // --------------------------------------------------------
            // STEP 3: Build the argument list and invoke the method
            // --------------------------------------------------------
            try {
                // Get the controller object (e.g. the ProductController instance)
                Object controller = handlerMethod.getHandler();

                // Get the specific method we need to call (e.g. getAllProducts)
                Method method = handlerMethod.getMethod();

                // --- SMART ARGUMENT RESOLVER ---
                // The controller method might need different parameters:
                //   void handle(Request req)
                //   String findProduct(@RequestParam("id") String id, Response res)
                //   String addProduct(@RequestBody Product p)
                // We need to figure out what to pass for each parameter.

                // Get all declared parameters of this method
                Parameter[] parameters = method.getParameters();

                // Create an array to hold the actual argument values we'll pass
                Object[] args = new Object[parameters.length];

                // Loop through each parameter and decide what value to use
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    Class<?> parameterType = parameter.getType();

                    if (parameterType.equals(Request.class)) {
                        // The method wants the Request object → give it ours
                        args[i] = request;

                    } else if (parameterType.equals(Response.class)) {
                        // The method wants the Response object → give it ours
                        args[i] = response;

                    } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                        // The parameter has @RequestParam("id") → extract from query string
                        // e.g. URL: /find-product?id=42  →  paramValue = "42"
                        RequestParam rp = parameter.getAnnotation(RequestParam.class);
                        String paramName  = rp.value(); // e.g. "id"
                        String paramValue = request.getParameter(paramName); // e.g. "42"
                        args[i] = paramValue;

                    } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                        // The parameter has @RequestBody → read the JSON from the request body
                        // and convert it to the right Java type

                        // Get the raw JSON string from the request body (already read by Request)
                        String jsonBody = request.getBody();

                        // Convert the JSON string into a Java object of the correct type.
                        // e.g.  '{"name":"MacBook","price":200000}' → new Product("MacBook", 200000)
                        Object bodyObject = JsonUtils.fromJson(jsonBody, parameterType);

                        args[i] = bodyObject;
                    }
                }

                // --- INVOKE THE CONTROLLER METHOD ---
                // This is like calling:  controller.getAllProducts()
                // but done dynamically at runtime using Java Reflection.
                Object result = method.invoke(controller, args);

                // --------------------------------------------------------
                // STEP 4: Write the result back to the client
                // --------------------------------------------------------
                if (result instanceof String) {
                    // The method returned a plain String → send it as-is
                    String responseBody = (String) result;
                    response.write(responseBody);

                } else {
                    // The method returned an object (List, Product, etc.)
                    // → Convert it to a JSON string first, then send
                    String jsonOutput = JsonUtils.toJson(result);
                    response.write(jsonOutput);
                }

            } catch (Exception exception) {
                // Something went wrong inside the controller method
                exception.printStackTrace();
                writer.println("HTTP/1.1 500 Internal Error");
                writer.flush();
            }

            // Done — close the connection
            clientSocket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
