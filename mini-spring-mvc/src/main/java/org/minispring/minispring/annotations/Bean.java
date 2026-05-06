package org.minispring.minispring.annotations;

import java.lang.annotation.*;

/**
 * ============================================================
 * ANNOTATION: @Bean
 * ============================================================
 *
 * This annotation is placed on a METHOD inside a @Configuration class
 * to tell the framework: "Call this method at startup and put whatever
 * it returns into the bean registry (the IoC container)."
 *
 * WHEN TO USE @Bean:
 * Use @Bean when you need fine-grained control over how an object is
 * created — for example when you need to pass constructor arguments,
 * configure properties, or choose a specific implementation.
 *
 * EXAMPLE:
 *   @Configuration
 *   public class AppConfig {
 *       @Bean
 *       public ProductService productService() {
 *           return new ProductService();  // you control how it's built
 *       }
 *   }
 *
 * HOW THE FRAMEWORK PROCESSES IT:
 *   BeanFactory finds the @Configuration class → creates an instance of it
 *   → scans its methods for @Bean → calls each @Bean method → stores
 *   the returned object in the beans map under the method's return type.
 *
 * META-ANNOTATIONS EXPLAINED:
 *   @Target(METHOD)       — can only be placed on METHODS
 *   @Retention(RUNTIME)   — kept at runtime so BeanFactory can read it via Reflection
 *
 * This is identical in purpose to Spring's @Bean annotation.
 * ============================================================
 */
@Target(ElementType.METHOD)         // only allowed on methods
@Retention(RetentionPolicy.RUNTIME) // visible at runtime via Reflection
public @interface Bean {
    // No attributes — just mark the method and the framework handles the rest
}
