package org.minispring.minispring;

import org.minispring.minispring.annotations.Autowired;
import org.minispring.minispring.annotations.Bean;
import org.minispring.minispring.annotations.Controller;
import org.minispring.minispring.annotations.Service;
import org.minispring.minispring.annotations.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ============================================================
 * CLASS: BeanFactory
 * ============================================================
 *
 * This is the HEART of the Mini-Spring framework — the IoC Container.
 *
 * "IoC" stands for Inversion of Control:
 *   Instead of your code creating its own dependencies
 *   (e.g. "ProductService ps = new ProductService()"),
 *   you let the FRAMEWORK create and connect everything for you.
 *
 * A "Bean" is just a Java object that the framework manages.
 *
 * WHAT THIS CLASS DOES (in order):
 *
 *   PHASE 0 — Process @Configuration classes
 *     Finds classes marked @Configuration, runs their @Bean methods,
 *     and registers the returned objects in the bean registry.
 *
 *   PHASE 1 — Instantiation
 *     For every class marked @Controller or @Service, creates one
 *     instance using its default (no-argument) constructor and stores
 *     it in the registry map.
 *
 *   PHASE 2 — Dependency Injection
 *     Goes through every created object, looks at its fields.
 *     If a field has @Autowired, finds the matching bean in the registry
 *     and injects (assigns) it into that field automatically.
 *
 * ANALOGY:
 *   Think of the BeanFactory as a smart factory manager.
 *   It reads a list of required parts (classes), builds each part,
 *   and then assembles them together by connecting the right pieces.
 * ============================================================
 */
public class BeanFactory {

    // THE REGISTRY — a map that stores all created objects.
    // Key:   the Class object (e.g. ProductService.class)
    // Value: the actual instance (e.g. the one ProductService object)
    // Using a Map means there is only ONE instance per type (Singleton).
    private Map<Class<?>, Object> beans = new HashMap<>();

    /**
     * The main startup method — runs all three phases in order.
     * Called once at application startup with the list of all scanned classes.
     *
     * @param classes all Class objects found by ClassScanner
     * @throws Exception if any class cannot be instantiated or wired
     */
    public void createBeans(Set<Class<?>> classes) throws Exception {

        // ============================================================
        // PHASE 0: PROCESS @Configuration CLASSES
        // These classes have @Bean methods that manually create beans.
        // ============================================================
        for (Class<?> clazz : classes) {

            // Check if this class has the @Configuration annotation
            if (clazz.isAnnotationPresent(Configuration.class)) {

                // Step 1: Create an instance of the @Configuration class itself
                //   (we need an object to call its methods on)
                Object configObject = clazz.getDeclaredConstructor().newInstance(null);

                // Step 2: Look at every method inside this @Configuration class
                for (Method method : clazz.getDeclaredMethods()) {

                    // Step 3: If the method has @Bean, call it
                    if (method.isAnnotationPresent(Bean.class)) {

                        // Invoke (call) the @Bean method on the config object
                        // This runs code like:  return new ProductService();
                        Object beanInstance = method.invoke(configObject);

                        // Step 4: Register the result using the method's return type as key
                        //   e.g.  ProductService.class → <the ProductService instance>
                        Class<?> returnType = method.getReturnType();
                        System.out.println("Registering @Bean: " + returnType.getName());
                        beans.put(returnType, beanInstance);
                    }
                }
            }
        }

        // ============================================================
        // PHASE 1: INSTANTIATION — create objects for @Controller / @Service
        // ============================================================
        for (Class<?> clazz : classes) {

            // We only care about classes marked @Controller or @Service
            if (clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class)) {

                // Skip if this bean was already created via @Bean in Phase 0
                // (we don't want two instances of the same type)
                if (!beans.containsKey(clazz)) {

                    System.out.println("Creating bean: " + clazz.getName());

                    // Use Java Reflection to create a new instance of this class
                    // using its default no-argument constructor.
                    // This is equivalent to:  Object instance = new ProductController();
                    Object instance = clazz.getDeclaredConstructor().newInstance();

                    // Store it in the registry so it can be found later
                    beans.put(clazz, instance);
                }
            }
        }

        // ============================================================
        // PHASE 2: DEPENDENCY INJECTION — connect the objects together
        // ============================================================
        // Loop through every bean we just created
        for (Object bean : beans.values()) {

            // Get all fields (variables) declared in this class.
            // For ProductController, this finds: "ProductService productService;"
            Field[] fields = bean.getClass().getDeclaredFields();

            for (Field field : fields) {

                // Check if this field is annotated with @Autowired
                // meaning: "please inject the right object here"
                if (field.isAnnotationPresent(Autowired.class)) {

                    // Find out WHAT TYPE of object this field needs.
                    // e.g. for "ProductService productService;" the type is ProductService.class
                    Class<?> dependencyType = field.getType();

                    // Look up that type in our registry to find the existing instance
                    Object dependency = beans.get(dependencyType);

                    // If we can't find a matching bean, throw a clear error
                    if (dependency == null) {
                        throw new RuntimeException("Could not find a bean for: " + dependencyType.getName());
                    }

                    // Private fields normally can't be accessed from outside their class.
                    // field.setAccessible(true) bypasses Java's access control so we CAN set it.
                    field.setAccessible(true);

                    // INJECT! — set the field's value to the found dependency object.
                    // This is like doing:  controller.productService = productServiceInstance;
                    System.out.println(
                            "Injecting " + dependencyType.getSimpleName() + " into " + bean.getClass().getSimpleName());
                    field.set(bean, dependency);
                }
            }
        }
    }

    /**
     * Registers a bean in the registry under its primary type.
     * (Helper method — currently used internally.)
     *
     * In real Spring, a bean can also be looked up by its interfaces
     * or superclasses. Here we keep it simple: one key per bean.
     *
     * @param bean        the object instance to register
     * @param primaryType the type (class) to register it under
     */
    private void registerBean(Object bean, Class<?> primaryType) {
        // Store the bean in the map using the declared type as the key
        beans.put(primaryType, bean);
    }

    /**
     * Returns the entire bean registry.
     * Other parts of the framework (like SimpleHandlerMapping) call this
     * to get the fully initialized, wired-together objects.
     *
     * @return map of  Class → Object  (all beans)
     */
    public Map<Class<?>, Object> getBeans() {
        return beans;
    }
}
