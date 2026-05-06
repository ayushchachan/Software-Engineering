package org.minispring.app;

/**
 * ============================================================
 * CLASS: Product
 * ============================================================
 *
 * This is a simple "data holder" class (also called a Model or POJO).
 * It represents a single product in our application, just like a row
 * in a products database table.
 *
 * A Product has two pieces of information:
 *   - name  : the product's name (e.g. "iPhone 15")
 *   - price : the product's price (e.g. 85000.0)
 *
 * WHY GETTERS AND SETTERS?
 * In Java it's a best practice to keep fields private and expose them
 * through public getter/setter methods. This lets us add validation
 * later without changing any other code.
 *
 * WHY TWO CONSTRUCTORS?
 * Jackson (the JSON library) requires a no-argument constructor so it
 * can create an empty Product object first and then fill in the values
 * using the setters. We also keep the convenience constructor so we can
 * create a fully-filled Product in one line (e.g. in ProductService).
 * ============================================================
 */
public class Product {

    // The product's name, e.g. "Samsung S24 Ultra"
    private String name;

    // The product's price as a decimal number, e.g. 670000.0
    private double price;

    // ----------------------------------------------------------------
    // NO-ARGUMENT CONSTRUCTOR
    // Jackson (our JSON library) NEEDS this to build a Product object
    // from incoming JSON. Without it, JSON → Java conversion will fail.
    // ----------------------------------------------------------------
    public Product() {
    }

    // ----------------------------------------------------------------
    // CONVENIENCE CONSTRUCTOR
    // Lets us create a fully-filled Product in one line:
    //   new Product("iPhone 15", 85000)
    // ----------------------------------------------------------------
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    // ----------------------------------------------------------------
    // GETTERS — read the private fields from outside this class
    // ----------------------------------------------------------------

    /** Returns the name of the product. */
    public String getName() {
        return name;
    }

    /** Returns the price of the product. */
    public double getPrice() {
        return price;
    }

    // ----------------------------------------------------------------
    // SETTERS — let Jackson (and our code) write values into the fields
    // Jackson calls setName("...") and setPrice(...) when it converts
    // JSON text into a Product object.
    // ----------------------------------------------------------------

    /** Sets / updates the product name. */
    public void setName(String name) {
        this.name = name;
    }

    /** Sets / updates the product price. */
    public void setPrice(double price) {
        this.price = price;
    }
}