package com.fooddelivery.restaurant;

import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {
    private final RestaurantOrderRepository repo;
    
    public TestRunner(RestaurantOrderRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("DEBUG-START: ALL ORDERS");
        repo.findAll().forEach(o -> System.out.println("ORDER " + o.getOrderId() + " status=" + o.getStatus() + " prep=" + o.getAdditionalPrepTime()));
        System.out.println("DEBUG-END");
    }
}
