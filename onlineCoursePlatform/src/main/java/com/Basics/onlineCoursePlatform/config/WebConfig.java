package com.Basics.onlineCoursePlatform.config;


import com.Basics.onlineCoursePlatform.DTO.CourseDTO;
import com.Basics.onlineCoursePlatform.DTO.CourseRatingDTO;
import com.Basics.onlineCoursePlatform.DTO.UserDTO;
import com.Basics.onlineCoursePlatform.entity.Course;
import com.Basics.onlineCoursePlatform.entity.CourseRating;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(CourseDTO.class, Course.class)
                .addMappings(mapper -> mapper.skip(Course::setId));
        modelMapper.createTypeMap(CourseRating.class, CourseRatingDTO.class)
                .addMappings(mapper -> mapper.map(src -> src.getId(), CourseRatingDTO::setId))
                .addMappings(mapper -> mapper.map(src -> src.getCourse().getId(), CourseRatingDTO::setCourseId))
                .addMappings(mapper -> mapper.map(src -> src.getUser().getId(), CourseRatingDTO::setUserId))
                .addMappings(mapper -> mapper.map(src -> src.getUser().getName(), CourseRatingDTO::setUserName));

        return modelMapper;

    }



    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file://" + uploadDir + "/");
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/static/");

    }
}

