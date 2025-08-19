package ptsd14.find.doctor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
         String uploadPath = System.getProperty("user.dir") + "/uploads/profile-photos/";

        registry.addResourceHandler("/uploads/profile-photos/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
