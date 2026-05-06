package org.minispring.minispring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ============================================================
 * ANNOTATION: @Service
 * ============================================================
 *
 * This annotation marks a class as a SERVICE — a component that
 * contains the BUSINESS LOGIC of your application.
 *
 * A service is the "worker" layer of an application:
 *   - Controller: receives the HTTP request, delegates to a service
 *   - Service:    does the real work (data processing, calculations,
 *                 database queries, etc.)
 *   - Model:      holds the data (like the Product class)
 *
 * When you put @Service on a class, you tell the framework:
 *   "Create one instance of this class, put it in the bean registry,
 *   and make it available for injection via @Autowired."
 *
 * EXAMPLE:
 *   @Service
 *   public class OrderService {
 *       public void placeOrder(Order order) { ... }
 *   }
 *   // Then in a controller:
 *   @Autowired
 *   OrderService orderService; // automatically injected!
 *
 * NOTE: In this project, ProductService does NOT use @Service —
 * instead it is manually registered via @Bean in AppConfig.
 * Both approaches achieve the same result; @Bean just gives more control.
 *
 * META-ANNOTATIONS EXPLAINED:
 *   @Retention(RUNTIME)   — visible at runtime so BeanFactory can detect it
 *   @Target(TYPE)         — can only be placed on a CLASS definition
 *
 * In real Spring, @Service is a specialization of @Component with the
 * same behavior but with clearer intent (it documents that the class
 * holds business logic).
 * ============================================================
 */
@Retention(RetentionPolicy.RUNTIME) // must be readable at runtime by BeanFactory
@Target(ElementType.TYPE)           // applies to class definitions only
public @interface Service {
    // No attributes needed — just mark the class and the framework does the rest
}
