package org.minispring.minispring;

import org.minispring.minispring.annotations.Bean;
import org.minispring.minispring.annotations.Configuration;
import org.minispring.app.ProductService;

/**
 * ============================================================
 * CLASS: AppConfig
 * ============================================================
 *
 * This is a CONFIGURATION class — it tells the framework how to create
 * certain objects (beans) manually, instead of letting the scanner
 * auto-detect them.
 *
 * WHY DO WE NEED THIS?
 * Sometimes you want full control over how a bean is created.
 * For example, you might want to pass constructor arguments, set up
 * connections, or choose which implementation to use.
 * In those cases you define a @Bean method here instead of putting
 * @Service directly on the class.
 *
 * HOW IT WORKS:
 *   1. The BeanFactory sees the @Configuration annotation on this class.
 *   2. It creates one instance of AppConfig.
 *   3. It then calls every method marked @Bean inside this class.
 *   4. Whatever those methods return is stored in the bean registry.
 *
 * This is exactly what Spring's @Configuration + @Bean pattern does.
 *
 * CURRENT SETUP:
 *   - productService() creates and registers a ProductService bean.
 *     ProductController will later get this exact instance injected
 *     via its @Autowired field.
 * ============================================================
 */
@Configuration  // marks this class as a source of bean definitions
public class AppConfig {

    /**
     * Creates the ProductService bean and registers it in the IoC container.
     *
     * The method name doesn't matter much — what matters is:
     *   - @Bean annotation  →  the framework calls this method at startup
     *   - return type       →  ProductService.class is used as the key in the registry
     *
     * @return a new, fully initialized ProductService instance
     */
    @Bean
    public ProductService ProductService() {
        return new ProductService();
    }
}
