package com.start_spring_here.model;

/**
 * A Product class to describe a product with a name and a price.
 */
public class Product {

    private String name;
    private double price;

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
