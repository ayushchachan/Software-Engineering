package org.minispring.app;

import org.minispring.app.Product;
import org.minispring.minispring.annotations.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * CLASS: ProductService
 * ============================================================
 *
 * This is the SERVICE layer of the application.
 * Think of a service as the "back-room worker" who does all the real
 * business logic, while the Controller just takes orders from the client
 * and passes them here.
 *
 * In a real application this class would talk to a database.
 * Here we use a simple ArrayList as our in-memory "database" so that
 * we can focus on learning the framework concepts.
 *
 * RESPONSIBILITIES:
 *   - Keep a list of all products (acting as a fake database)
 *   - Return all products when asked
 *   - Add a new product to the list
 *   - Find a product by its ID
 *
 * HOW IT IS USED:
 *   ProductController has a @Autowired ProductService field.
 *   The BeanFactory creates ONE instance of this class and injects
 *   that same instance into every class that needs it (Singleton pattern).
 * ============================================================
 */
public class ProductService {

    // Our in-memory "database" — a simple list of Product objects.
    // In a real app this would be replaced by a database query.
    List<Product> products = new ArrayList<>();

    // ----------------------------------------------------------------
    // CONSTRUCTOR
    // When the framework creates a ProductService object, this constructor
    // runs and pre-loads three sample products so we have something to show.
    // ----------------------------------------------------------------
    public ProductService() {
        products.add(new Product("iPhone 15", 8500000));
        products.add(new Product("Samsung S24 Ultra", 670000));
        products.add(new Product("OnePlus Nord CE 2", 25000));
    }

    // ----------------------------------------------------------------
    // METHOD: getAllProducts
    // Returns every product in our list.
    // The controller calls this when a client hits GET /product-list.
    // ----------------------------------------------------------------
    /**
     * Returns all products stored in memory.
     * Simulates what "SELECT * FROM products" would do in a real database.
     *
     * @return the full list of Product objects
     */
    public List<Product> getAllProducts() {
        return this.products;
    }

    // ----------------------------------------------------------------
    // METHOD: addProduct
    // Adds a brand-new product to our in-memory list.
    // Called when the client sends a POST request with a product in JSON.
    // ----------------------------------------------------------------
    /**
     * Adds a new product to the list.
     *
     * @param newProduct the Product object received from the HTTP request body
     */
    public void addProduct(Product newProduct) {
        this.products.add(newProduct);
    }

    // ----------------------------------------------------------------
    // METHOD: getProductById
    // Finds a product by its ID.
    // Currently just returns a fake string — in a real app you would
    // query the database and return the actual matching product.
    // ----------------------------------------------------------------
    /**
     * Looks up a product by its ID string.
     * (This is a stub — it returns a fake message for now.)
     *
     * @param id the product ID passed in the URL query parameter
     * @return a string describing the "found" product
     */
    public String getProductById(String id) {
        return "Product ID: " + id + " (Fetched from DataBase)";
    }
}
