package com.fooddelivery.restaurant.config;

import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.time.LocalTime;

@Configuration
public class OpenApiConfig {

    static {
        SpringDocUtils.getConfig().replaceWithSchema(LocalTime.class, new StringSchema().example("09:00:00"));
    }
}
