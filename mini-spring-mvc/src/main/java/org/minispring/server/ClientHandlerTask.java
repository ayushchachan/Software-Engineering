package org.minispring.server;

import java.net.Socket;

/**
 * ============================================================
 * CLASS: ClientHandlerTask
 * ============================================================
 *
 * This class is a small TASK wrapper used to handle one HTTP client
 * connection on its own thread.
 *
 * BACKGROUND — WHY DO WE NEED THIS?
 * A web server must handle many clients at the same time.
 * If we processed each request one after another (single-threaded),
 * a slow request would block everyone else from getting a response.
 *
 * Instead, MiniHttpServer uses a thread pool (a group of pre-created threads).
 * For each new client connection, it submits a task to the pool.
 * A free thread picks up the task and handles that client independently.
 *
 * WHAT IS "Runnable"?
 * Runnable is a Java interface that has one method: run().
 * When a thread picks up this task, it calls run() — that's where
 * the actual work happens.
 *
 * WHAT THIS CLASS DOES:
 *   1. Stores references to the HttpHandler (DispatcherServlet) and
 *      the client Socket (the connection to the browser/Postman).
 *   2. When run() is called by a thread, it delegates to handler.handle(client).
 *      This triggers the full Spring-style request-response cycle.
 *
 * ANALOGY:
 *   Imagine a restaurant where the manager (MiniHttpServer) hands each
 *   new customer to a waiter (a thread from the pool).
 *   ClientHandlerTask is like the customer's order ticket — it tells the
 *   waiter exactly which customer to serve (client socket) and where to
 *   take their order (handler.handle).
 * ============================================================
 */
public class ClientHandlerTask implements Runnable {

    // The framework's request handler — this is the DispatcherServlet
    private final HttpHandler handler;

    // The TCP socket connected to the HTTP client (browser, Postman, etc.)
    private final Socket client;

    /**
     * Constructor — stores the handler and client socket for use in run().
     *
     * @param handler the HttpHandler (DispatcherServlet) that will process the request
     * @param client  the Socket representing the connected HTTP client
     */
    public ClientHandlerTask(HttpHandler handler, Socket client) {
        this.handler = handler;
        this.client = client;
    }

    /**
     * Called automatically by a thread from the thread pool.
     * Delegates to handler.handle() which does all the actual work:
     * parsing the HTTP request, calling the controller, sending the response.
     */
    @Override
    public void run() {
        // Pass the client socket to the DispatcherServlet (via the HttpHandler interface)
        // Everything from here on happens inside DispatcherServlet.handle()
        handler.handle(client);
    }
}
