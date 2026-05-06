package org.minispring.app;

import org.minispring.app.Product;
import org.minispring.app.ProductService;
import org.minispring.minispring.annotations.*;
import org.minispring.minispring.Request;
import org.minispring.minispring.RequestMethod;
import org.minispring.minispring.Response;

import java.util.List;

/**
 * ============================================================
 * CLASS: ProductController
 * ============================================================
 *
 * This is a CONTROLLER class. Think of it as the "front desk" of a hotel.
 * When an HTTP request (like a browser or Postman) arrives at a URL,
 * the framework finds the matching method in this class and calls it.
 *
 * HOW IT FITS IN THE BIG PICTURE:
 *   Browser/Postman → HTTP Request → MiniHttpServer → DispatcherServlet
 *       → SimpleHandlerMapping → ProductController (this class) → ProductService
 *
 * ENDPOINTS EXPOSED:
 *   GET  /product-list          → returns all products as JSON
 *   GET  /find-product/{id}     → returns a single product by ID
 *   POST /api/products/add      → adds a new product (body is JSON)
 *
 * NOTE ON @Autowired:
 *   We don't create ProductService ourselves (no "new ProductService()").
 *   The framework's BeanFactory detects the @Autowired annotation and
 *   injects (puts in) the already-created ProductService object for us.
 *   This is called "Dependency Injection".
 * ============================================================
 */
@Controller  // tells the framework: "this class handles HTTP requests"
public class ProductController {

    // The framework will automatically fill this field with a ProductService
    // object because of the @Autowired annotation.
    // We never write "productService = new ProductService()" ourselves.
    @Autowired
    ProductService productService;

    // ----------------------------------------------------------------
    // ENDPOINT 1: GET /product-list
    // Returns the full list of products in JSON format.
    // ----------------------------------------------------------------
    /**
     * Handles HTTP GET requests to "/product-list".
     * Asks the ProductService for all products and returns the list.
     * The framework will automatically convert the List<Product> to JSON.
     */
    @RequestMapping(path = "/product-list", method = RequestMethod.GET)
    public List<Product> getAllProducts() {
        // Delegate to the service layer — it knows how to fetch the data
        return productService.getAllProducts();
    }

    // ----------------------------------------------------------------
    // ENDPOINT 2: GET /find-product/{id}  (e.g. /find-product?id=1)
    // Returns a single product by its ID.
    // @RequestParam("id") tells the framework to pull the "id" value
    // from the query string (the part after "?" in the URL).
    // ----------------------------------------------------------------
    /**
     * Handles HTTP GET requests to "/find-product/{id}".
     *
     * @param productId  the value of the "id" query parameter, e.g. "1"
     * @param res        the Response object (not used here, but available)
     * @return a String describing the found product
     */
    @RequestMapping(path = "/find-product/{id}", method = RequestMethod.GET)
    public String findProduct(@RequestParam("id") String productId, Response res) {
        // Pass the id to the service and return whatever it gives back
        return productService.getProductById(productId);
    }

    // ----------------------------------------------------------------
    // ENDPOINT 3: POST /api/products/add
    // Reads a Product object from the JSON body of the request and
    // adds it to our product list.
    // @RequestBody tells the framework: "read the JSON body and convert
    // it into a Product object before calling this method".
    // ----------------------------------------------------------------
    /**
     * Handles HTTP POST requests to "/api/products/add".
     * The request body must be a JSON object like:
     *   { "name": "MacBook Pro", "price": 200000 }
     *
     * @param newProduct  the Product object deserialized from the request body
     * @return a success message string
     */
    @RequestMapping(path = "/api/products/add", method = RequestMethod.POST)
    public String addProduct(@RequestBody Product newProduct) {
        // Hand off to the service to save the product
        this.productService.addProduct(newProduct);

        // Print a confirmation to the console (useful for debugging)
        System.out.println("Received Product: " + newProduct.getName() + " - " + newProduct.getPrice());

        // Return a plain-text success message back to the client
        return "Product " + newProduct.getName() + " added successfully!";
    }
}
