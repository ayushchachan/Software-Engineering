package org.minispring.minispring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ============================================================
 * ANNOTATION: @Configuration
 * ============================================================
 *
 * This annotation marks a class as a SOURCE OF BEAN DEFINITIONS.
 * Classes marked with @Configuration contain @Bean methods that the
 * framework calls at startup to create and register objects (beans).
 *
 * Think of a @Configuration class as a "recipe book":
 *   - Each @Bean method is one recipe.
 *   - BeanFactory is the chef — it reads the recipes and cooks the beans.
 *
 * EXAMPLE:
 *   @Configuration         ← "This class has bean recipes"
 *   public class AppConfig {
 *       @Bean              ← "This method is one recipe"
 *       public ProductService productService() {
 *           return new ProductService();
 *       }
 *   }
 *
 * HOW THE FRAMEWORK USES IT:
 *   BeanFactory scans all classes, finds those marked @Configuration,
 *   and then looks inside them for @Bean methods to call.
 *
 * META-ANNOTATIONS EXPLAINED:
 *   @Retention(RUNTIME)   — annotation is available at runtime via Reflection
 *   @Target(TYPE)         — can only be placed on a CLASS (type), not on
 *                           methods or fields
 *
 * This mirrors Spring's @Configuration annotation exactly.
 * ============================================================
 */
@Retention(RetentionPolicy.RUNTIME) // visible at runtime so BeanFactory can detect it
@Target(ElementType.TYPE)           // can only be placed on a class definition
public @interface Configuration {
    // No attributes needed
}
