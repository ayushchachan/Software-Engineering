package org.minispring.bootstrap;

import org.minispring.minispring.BeanFactory;
import org.minispring.minispring.ClassScanner;
import org.minispring.minispring.DispatcherServlet;
import org.minispring.minispring.SimpleHandlerMapping;
import org.minispring.server.MiniHttpServer;

import java.util.Map;
import java.util.Set;

/**
 * ============================================================
 * CLASS: Bootstrap
 * ============================================================
 *
 * This is the ENTRY POINT of the entire application.
 * The main() method here is what you run to start the server.
 * It plays the same role as "SpringApplication.run(...)" in real Spring Boot.
 *
 * WHAT HAPPENS STEP BY STEP WHEN YOU RUN main():
 *
 *  STEP 1 — SCAN
 *    A ClassScanner looks at all .class files inside the "org.minispring.app"
 *    package and collects every class it finds.
 *
 *  STEP 2 — CREATE BEANS (IoC Container)
 *    A BeanFactory takes those classes and:
 *      a) creates one object (instance) for every @Controller, @Service, etc.
 *      b) injects dependencies — if a class has an @Autowired field, the
 *         factory automatically fills it with the right object.
 *
 *  STEP 3 — BUILD THE WEB LAYER
 *    SimpleHandlerMapping reads all @RequestMapping annotations and builds
 *    a lookup table:  URL path → controller method
 *
 *  STEP 4 — START THE SERVER
 *    MiniHttpServer opens a TCP socket on port 8080 and waits for requests.
 *    Every incoming request is handed to the DispatcherServlet which uses
 *    the handler mapping to find and call the right controller method.
 *
 * ANALOGY:
 *   Imagine you're opening a restaurant.
 *   - ClassScanner finds all your staff (classes) in the building.
 *   - BeanFactory hires them, trains them, and assigns them to the right tables.
 *   - SimpleHandlerMapping is the menu (maps customer orders to kitchen tasks).
 *   - DispatcherServlet is the head waiter who takes orders and routes them.
 *   - MiniHttpServer is the front door — it opens the restaurant for customers.
 * ============================================================
 */
public class Bootstrap {

    public static void main(String[] args) throws Exception {

        // ============================================================
        // STEP 1: SCAN — find all classes in the application package
        // ============================================================
        ClassScanner scanner = new ClassScanner();

        // We tell the scanner to look inside "org.minispring.app".
        // It will discover ProductController, ProductService, Product, etc.
        Set<Class<?>> allClasses = scanner.scan("org.minispring.app");

        // Print what we found — good for debugging during development
        System.out.println("--- Scanned Classes ---");
        for (Class<?> clazz : allClasses) {
            System.out.println(clazz.getName());
        }
        System.out.println("-----------------------");

        // ============================================================
        // STEP 2: CREATE BEANS — build and wire all objects (IoC)
        // ============================================================
        BeanFactory beanFactory = new BeanFactory();

        // This is where the "magic" happens:
        //   - creates one instance of each @Controller and @Service class
        //   - fills @Autowired fields with the right objects
        beanFactory.createBeans(allClasses);

        // Get the finished map of  Class → Object  (e.g. ProductController.class → <instance>)
        Map<Class<?>, Object> beans = beanFactory.getBeans();

        // ============================================================
        // STEP 3: BUILD THE WEB LAYER
        // ============================================================

        // SimpleHandlerMapping reads @RequestMapping annotations and builds
        // a map of  "/product-list" → getAllProducts() method
        SimpleHandlerMapping handlerMapping = new SimpleHandlerMapping(beans);

        // DispatcherServlet is the "front controller" — it receives every
        // HTTP request and forwards it to the correct controller method
        DispatcherServlet dispatcher = new DispatcherServlet(handlerMapping);

        // ============================================================
        // STEP 4: START THE HTTP SERVER
        // ============================================================

        // MiniHttpServer opens port 8080 and starts listening for connections.
        // It knows NOTHING about Spring or controllers — it just hands every
        // incoming TCP socket to the DispatcherServlet.
        MiniHttpServer server = new MiniHttpServer(8080, dispatcher);
        server.start(); // blocks forever (runs the accept loop)
    }
}
