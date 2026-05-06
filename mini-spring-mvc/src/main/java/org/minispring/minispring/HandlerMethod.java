package org.minispring.minispring;

import java.lang.reflect.Method;

/**
 * ============================================================
 * CLASS: HandlerMethod
 * ============================================================
 *
 * This is a simple WRAPPER (or "holder") class that bundles together
 * two related pieces of information:
 *
 *   1. handler — the CONTROLLER OBJECT (the instance), e.g. the
 *                one ProductController that was created by BeanFactory.
 *
 *   2. method  — the specific METHOD inside that controller that should
 *                handle the request, e.g. getAllProducts().
 *
 * WHY DO WE NEED THIS?
 * When SimpleHandlerMapping receives an HTTP request for "/product-list",
 * it needs to answer two questions:
 *   Q1: Which object should I call the method on?  → the handler
 *   Q2: Which method exactly should I call?        → the method
 *
 * By wrapping both in a HandlerMethod object, we can store them together
 * in the URL → HandlerMethod map, and the DispatcherServlet can easily
 * retrieve both at once.
 *
 * ANALOGY:
 *   If the URL is a customer's food order, then HandlerMethod is a
 *   sticky note that says:
 *     "Table 3 wants pasta — go to Chef Maria (handler) and ask her
 *     to make the pasta dish (method)."
 * ============================================================
 */
public class HandlerMethod {

    // The controller object (e.g. a ProductController instance).
    // We call the method ON this object.
    private Object handler;

    // The Java Method object representing the specific method to call
    // (e.g. the getAllProducts() method inside ProductController).
    private Method method;

    /**
     * Creates a HandlerMethod pairing a controller object with one of its methods.
     *
     * @param obj    the controller instance (created by BeanFactory)
     * @param method the method to invoke when the matching URL is requested
     */
    public HandlerMethod(Object obj, Method method) {
        this.handler = obj;
        this.method = method;
    }

    /**
     * Returns the controller object.
     * DispatcherServlet uses this to know WHICH OBJECT to call the method on.
     *
     * @return the controller instance
     */
    public Object getHandler() {
        return handler;
    }

    /**
     * Returns the method that should be called.
     * DispatcherServlet uses this with Java Reflection to invoke it at runtime.
     *
     * @return the Method object (a Java reflection handle to the function)
     */
    public Method getMethod() {
        return method;
    }
}
