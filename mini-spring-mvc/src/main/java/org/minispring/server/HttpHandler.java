package org.minispring.server;

import java.net.Socket;

/**
 * ============================================================
 * INTERFACE: HttpHandler
 * ============================================================
 *
 * An "interface" in Java defines a CONTRACT — a promise that says:
 * "Any class that implements me MUST provide a handle() method."
 *
 * This interface is the BRIDGE between the low-level HTTP server
 * (MiniHttpServer) and the high-level web framework (DispatcherServlet).
 *
 * WHY DO WE NEED THIS SEPARATION?
 * MiniHttpServer only understands TCP sockets — it has no idea about
 * Spring, controllers, annotations, or JSON.
 * DispatcherServlet understands all of that framework magic.
 *
 * By using HttpHandler as the bridge:
 *   - MiniHttpServer just calls  handler.handle(socket)  for each client.
 *   - It doesn't need to know WHO is handling it or HOW.
 *   - DispatcherServlet implements this interface, so it receives the socket
 *     and does all the Spring-style request processing.
 *
 * This is called the "Separation of Concerns" principle:
 *   networking code ≠ framework code — they talk through a shared interface.
 *
 * REAL WORLD ANALOGY:
 *   This is exactly how Tomcat (a real Java web server) works with Spring.
 *   Tomcat knows how to accept HTTP connections; Spring's DispatcherServlet
 *   handles the logic. Tomcat doesn't know about Spring — it just calls
 *   a standardized method (in that case, HttpServlet.service()).
 * ============================================================
 */
public interface HttpHandler {

    /**
     * Called by MiniHttpServer once for every incoming HTTP connection.
     * The implementor (DispatcherServlet) is responsible for:
     *   - Reading the HTTP request from the socket
     *   - Processing it (finding the right controller, calling it, etc.)
     *   - Writing the HTTP response back through the socket
     *   - Closing the socket when done
     *
     * @param clientSocket the TCP socket connected to the HTTP client
     */
    void handle(Socket clientSocket);
}
