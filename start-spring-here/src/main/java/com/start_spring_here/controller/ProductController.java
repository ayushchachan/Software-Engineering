package com.start_spring_here.controller;

import com.start_spring_here.model.Product;
import com.start_spring_here.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String viewProducts(Model model) {
        var products = productService.findAll();
        model.addAttribute("products", products);
        return "products";
    }

    @PostMapping(path = "/products")
    public String addProduct(@RequestParam String name, @RequestParam double price, Model model) {
        var newProduct = new Product();
        newProduct.setName(name);
        newProduct.setPrice(price);
        productService.addProduct(newProduct);

        var products = productService.findAll();
        model.addAttribute("products", products);
        return "products";

    }
}
