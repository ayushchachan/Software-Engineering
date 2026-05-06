package org.minispring.minispring.annotations;

import java.lang.annotation.*;

/**
 * ============================================================
 * ANNOTATION: @Controller
 * ============================================================
 *
 * This annotation marks a class as an HTTP CONTROLLER.
 * A controller is a class whose methods handle incoming HTTP requests.
 *
 * When you put @Controller on a class, you are telling the framework:
 *   "This class contains methods that should respond to HTTP requests.
 *   Please create an instance of it, inject its dependencies, and
 *   register its @RequestMapping methods in the URL router."
 *
 * EXAMPLE:
 *   @Controller
 *   public class ProductController {
 *       @RequestMapping(path = "/products", method = RequestMethod.GET)
 *       public List<Product> getAllProducts() { ... }
 *   }
 *
 * HOW THE FRAMEWORK USES IT:
 *   1. BeanFactory scans all classes, finds those marked @Controller,
 *      and creates one instance of each.
 *   2. SimpleHandlerMapping scans @Controller beans and registers their
 *      @RequestMapping methods in the URL routing table.
 *
 * META-ANNOTATIONS EXPLAINED:
 *   @Retention(RUNTIME)   — annotation survives until runtime so Reflection can read it
 *   @Target(TYPE)         — can only be placed on a CLASS definition
 *
 * In real Spring, @Controller also marks the class as a Spring component
 * (like @Component) and enables additional MVC-specific features.
 * ============================================================
 */
@Retention(RetentionPolicy.RUNTIME) // must be readable at runtime
@Target(ElementType.TYPE)           // applies to classes only
public @interface Controller {
    // No attributes needed — just marking the class is enough
}
