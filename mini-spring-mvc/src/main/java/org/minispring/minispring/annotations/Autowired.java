package org.minispring.minispring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ============================================================
 * ANNOTATION: @Autowired
 * ============================================================
 *
 * This annotation is a "sticker" you put on a class field to tell the
 * framework: "Please fill this field with the right object for me.
 * I don't want to create it myself."
 *
 * This is called DEPENDENCY INJECTION (DI) — one of the most important
 * concepts in Spring and modern Java development.
 *
 * HOW IT WORKS:
 *   1. You mark a field with @Autowired, e.g.:
 *        @Autowired
 *        ProductService productService;
 *
 *   2. BeanFactory reads this annotation using Java Reflection.
 *   3. It looks up a ProductService instance in its registry (the beans map).
 *   4. It injects (sets) that instance into the field automatically.
 *   5. Your class gets a working ProductService without calling "new" anywhere.
 *
 * META-ANNOTATIONS EXPLAINED:
 *   @Retention(RUNTIME)   — keep this annotation available at runtime
 *                           (not just at compile time) so Reflection can see it
 *   @Target(FIELD)        — this annotation can only be placed on a FIELD (variable),
 *                           not on a class or a method
 *
 * In real Spring, @Autowired does the same thing but with many more features
 * like handling interfaces, optional injection, constructor injection, etc.
 * ============================================================
 */
@Retention(RetentionPolicy.RUNTIME) // visible to our BeanFactory at runtime via Reflection
@Target(ElementType.FIELD)          // can only be placed on class fields (variables)
public @interface Autowired {
    // No attributes needed — just mark the field and the framework does the rest
}
