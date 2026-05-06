package org.minispring.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ============================================================
 * CLASS: MiniHttpServer
 * ============================================================
 *
 * This is our custom HTTP web server — the "Tomcat" of this project.
 * It handles the pure NETWORKING part: opening a port, accepting TCP
 * connections, and handing each connection to the framework.
 *
 * IMPORTANT: This class knows NOTHING about Spring, controllers, JSON,
 * or annotations. It has ONE job: accept connections and pass them on.
 * This is the Single Responsibility Principle in action.
 *
 * HOW IT WORKS:
 *
 *   1. A ServerSocket is opened on a given port (e.g. 8080).
 *      Now the OS is listening for anyone connecting to localhost:8080.
 *
 *   2. A thread pool is created (10 threads ready to work).
 *      Thread pools are more efficient than creating a new thread for
 *      every request, because thread creation is expensive.
 *
 *   3. The server enters an infinite loop (while(true)):
 *       a. server.accept() BLOCKS (waits) until a client connects.
 *       b. Once a client connects, accept() returns a Socket.
 *       c. We wrap it in a ClientHandlerTask and submit it to the pool.
 *       d. A free thread picks up the task and handles the client.
 *       e. The main loop immediately goes back to step (a) to wait
 *          for the NEXT client — this is how multiple clients are served.
 *
 * ANALOGY:
 *   MiniHttpServer is like a hotel reception desk.
 *   - The ServerSocket is the front door (always open).
 *   - server.accept() is the receptionist waiting for a guest to arrive.
 *   - The thread pool is the staff (10 workers ready to help).
 *   - Each ClientHandlerTask is an assignment card: "Room 3, handle this guest."
 *   - The receptionist never leaves the desk — they always wait for the next guest.
 * ============================================================
 */
public class MiniHttpServer {

    // The HttpHandler is the framework entry point (DispatcherServlet).
    // The server calls handler.handle(socket) for each new client connection.
    private HttpHandler handler;

    // The TCP port number this server listens on (e.g. 8080).
    // Clients connect to http://localhost:8080/...
    private int port;

    /**
     * Constructor — stores the port and handler for use when start() is called.
     *
     * @param port    the TCP port to listen on, e.g. 8080
     * @param handler the HttpHandler (DispatcherServlet) that will process requests
     */
    public MiniHttpServer(int port, HttpHandler handler) {
        this.handler = handler;
        this.port = port;
    }

    /**
     * Opens the server socket and starts the infinite accept loop.
     *
     * This method BLOCKS forever — it never returns under normal circumstances.
     * The only way to stop it is to kill the JVM process (e.g. Ctrl+C).
     */
    public void start() {
        try {
            // Open a ServerSocket on our port.
            // The OS now knows: "if anyone connects to port 8080, wake up this app."
            ServerSocket server = new ServerSocket(port);
            System.out.println("MiniHttpServer started on port " + port);

            // Create a thread pool with 10 worker threads.
            // Each thread can handle one client at a time.
            // Up to 10 clients can be served simultaneously.
            ExecutorService pool = Executors.newFixedThreadPool(10);

            // Infinite loop — the server runs forever
            while (true) {
                // BLOCKING CALL: pause here until a client connects.
                // When a connection arrives, accept() returns a Socket.
                Socket client = server.accept();

                // Wrap the client socket + handler into a task and give it
                // to the thread pool. A free thread will call task.run()
                // which in turn calls handler.handle(client).
                pool.execute(new ClientHandlerTask(handler, client));
            }

        } catch (Exception ex) {
            // If something goes wrong (e.g. port already in use), print the error
            ex.printStackTrace();
        }
    }
}
