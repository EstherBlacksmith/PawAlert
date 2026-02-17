package itacademy.pawalert.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static files from frontend directory
        registry.addResourceHandler("/frontend/**")
                .addResourceLocations("file:frontend/")
                .setCachePeriod(0);
    }
}
