package org.minispring.minispring;

import org.minispring.minispring.annotations.Controller;
import org.minispring.minispring.annotations.RequestMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================
 * CLASS: SimpleHandlerMapping
 * ============================================================
 *
 * This class is the URL ROUTER of our Mini-Spring framework.
 * It builds and maintains a lookup table that maps URL paths to the
 * specific controller method that should handle each path.
 *
 * ANALOGY — think of it as a restaurant menu:
 *   "/product-list"      → getAllProducts()  in ProductController
 *   "/find-product/{id}" → findProduct()     in ProductController
 *   "/api/products/add"  → addProduct()      in ProductController
 *
 * HOW IT WORKS:
 *
 *   AT STARTUP (constructor):
 *     - Receives the map of all fully-wired beans from BeanFactory.
 *     - Loops through every bean looking for classes marked @Controller.
 *     - For each @Controller class, scans its methods for @RequestMapping.
 *     - Stores each path → HandlerMethod pair in the 'mapping' map.
 *
 *   AT REQUEST TIME (getHandler):
 *     - DispatcherServlet calls getHandler("/product-list").
 *     - We look up that path in our map and return the HandlerMethod.
 *     - DispatcherServlet then uses that HandlerMethod to invoke the method.
 *
 * In real Spring this is done by RequestMappingHandlerMapping which also
 * supports path variables, wildcards, and many other advanced features.
 * ============================================================
 */
public class SimpleHandlerMapping {

    // The core data structure: URL path → HandlerMethod (controller + method)
    // Example entry:  "/product-list" → HandlerMethod(productControllerInstance, getAllProducts())
    private Map<String, HandlerMethod> mapping = new HashMap<>();

    /**
     * Constructor — builds the URL-to-method mapping at startup.
     *
     * We receive a map of already-created, already-wired beans from BeanFactory.
     * This is important: we use the EXISTING instances (not creating new ones),
     * so when we later call a method, dependency injection is already in place.
     *
     * @param beans the map of  Class → Object  from BeanFactory.getBeans()
     */
    public SimpleHandlerMapping(Map<Class<?>, Object> beans) {

        // Go through every bean in the registry
        for (Object bean : beans.values()) {

            Class<?> beanType = bean.getClass();

            // Only process beans that are marked @Controller
            // (Services, configs, etc. don't have URL mappings)
            if (beanType.isAnnotationPresent(Controller.class)) {
                // Register all @RequestMapping methods from this controller
                registerController(beanType, bean);
            }
        }
    }

    /**
     * Reads all methods in a controller class, finds those with @RequestMapping,
     * and stores them in the mapping table.
     *
     * @param clazz              the Class of the controller (for reading annotations)
     * @param controllerInstance the actual live object (for calling methods on)
     */
    private void registerController(Class<?> clazz, Object controllerInstance) {
        try {
            // Get all methods declared in this controller class
            Method[] methods = clazz.getDeclaredMethods();

            for (Method method : methods) {

                // Check if this method has @RequestMapping (i.e. it handles a URL)
                if (method.isAnnotationPresent(RequestMapping.class)) {

                    // Read the annotation to get the path string (e.g. "/product-list")
                    RequestMapping rm = method.getAnnotation(RequestMapping.class);
                    String path = rm.path();

                    // Wrap the controller instance + method together into a HandlerMethod
                    HandlerMethod hm = new HandlerMethod(controllerInstance, method);

                    // Store it in our map:  "/product-list" → hm
                    mapping.put(path, hm);
                    System.out.println("Mapped URL: " + path + " --> " + method.getName());
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Looks up the handler for a given URL path.
     * Called by DispatcherServlet for every incoming request.
     *
     * @param path the URL path from the incoming HTTP request, e.g. "/product-list"
     * @return the HandlerMethod that handles this path, or null if none is registered
     */
    public HandlerMethod getHandler(String path) {
        // Simple map lookup — O(1) time
        return this.mapping.get(path);
    }
}
