package com.fooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication(
    scanBasePackages = {"com.fooddelivery", "com.fooddelivery.common"}
)
@org.springframework.boot.autoconfigure.domain.EntityScan(basePackages = {"com.fooddelivery", "com.fooddelivery.common"})
@org.springframework.data.jpa.repository.config.EnableJpaRepositories(basePackages = {"com.fooddelivery", "com.fooddelivery.common"})
@EnableScheduling
@EnableFeignClients
@EnableCaching
@org.springframework.scheduling.annotation.EnableAsync
public class RestaurantApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
    }
}
