package org.example.landofbooks.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/images/**") // This maps the URL pattern to the file location
                .addResourceLocations("file:/C:/Users/pabod/IdeaProjects/land-of-books/uploads/images/");
    }
}
