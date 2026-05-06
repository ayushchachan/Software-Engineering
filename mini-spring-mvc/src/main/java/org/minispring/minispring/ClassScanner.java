package org.minispring.minispring;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * ============================================================
 * CLASS: ClassScanner
 * ============================================================
 *
 * This class is responsible for FINDING all Java classes inside a
 * given package at runtime. It works like a search engine that looks
 * at the compiled .class files on your disk.
 *
 * WHY DO WE NEED THIS?
 * The framework needs to know which classes have @Controller, @Service,
 * etc. so it can automatically create and manage them.
 * Rather than you manually listing every class, the scanner finds them all.
 *
 * HOW IT WORKS:
 *   1. Takes a package name like "org.minispring.app".
 *   2. Converts the dots to slashes → "org/minispring/app".
 *   3. Asks the JVM's ClassLoader to find the matching folder on disk.
 *   4. Walks through that folder (and sub-folders recursively).
 *   5. For every ".class" file found, loads the Class object and adds it
 *      to the result set.
 *
 * ANALOGY:
 *   Imagine you have a filing cabinet (the package folder).
 *   ClassScanner opens every drawer (sub-package), reads every file,
 *   and brings back a list of what it found.
 *
 * This is similar to what Spring's component-scan does internally.
 * ============================================================
 */
public class ClassScanner {

    /**
     * Scans a Java package and returns every class found inside it
     * (including classes in sub-packages).
     *
     * @param packageName the dot-separated package to scan, e.g. "org.minispring.app"
     * @return a Set containing all Class objects found in that package
     */
    public Set<Class<?>> scan(String packageName) {

        // We use a Set so duplicate classes are automatically ignored
        Set<Class<?>> classes = new HashSet<>();

        try {
            // Step 1: Convert "org.minispring.app" → "org/minispring/app"
            // The file system uses slashes; Java packages use dots.
            String path = packageName.replace('.', '/');

            // Step 2: Ask the ClassLoader (the JVM's file manager) where
            // this package folder lives on disk.
            // getContextClassLoader() gives us the classloader that loaded our app.
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            // getResources() returns all locations that match this path
            // (there can be more than one, e.g. if jars are on the classpath)
            Enumeration<URL> resources = classLoader.getResources(path);

            // Step 3: Go through each location we found
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                // We only handle "file://" URLs (real folders on disk).
                // We skip JAR files to keep this implementation simple.
                if (resource.getProtocol().equals("file")) {
                    File directory = new File(resource.getFile());

                    // Recursively scan this folder for .class files
                    scanDirectory(directory, packageName, classes);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            // Print the stack trace so we can debug if something goes wrong
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * Recursively walks through a directory and loads every .class file
     * it finds as a Java Class object.
     *
     * "Recursive" means: if we find a sub-folder, we call this same method
     * again on that sub-folder. This way we get ALL classes, even those
     * in nested packages.
     *
     * @param directory   the folder to scan
     * @param packageName the package name that corresponds to this folder
     * @param classes     the Set we add found Class objects into
     * @throws ClassNotFoundException if a .class file cannot be loaded
     */
    private void scanDirectory(File directory, String packageName, Set<Class<?>> classes)
            throws ClassNotFoundException {

        // If the folder doesn't exist on disk, nothing to do
        if (!directory.exists()) {
            return;
        }

        // Get a list of everything inside this folder
        File[] files = directory.listFiles();
        if (files == null) return; // empty folder — nothing to process

        for (File file : files) {

            if (file.isDirectory()) {
                // It's a sub-folder → dive deeper (recursion!)
                // The new package name adds the folder name with a dot separator.
                // e.g. "org.minispring.app" + "." + "annotations" → "org.minispring.app.annotations"
                scanDirectory(file, packageName + "." + file.getName(), classes);

            } else if (file.getName().endsWith(".class")) {
                // It's a compiled Java class file → load it!

                // Remove the ".class" extension to get just the simple class name.
                // e.g. "ProductController.class" → "ProductController"
                String className = file.getName().substring(0, file.getName().length() - 6);

                // Build the fully-qualified class name, e.g. "org.minispring.app.ProductController"
                // Then use Class.forName() to load it into the JVM.
                Class<?> clazz = Class.forName(packageName + "." + className);
                classes.add(clazz);
            }
        }
    }
}