package com.Basics.onlineCoursePlatform.config;


import com.Basics.onlineCoursePlatform.DTO.CourseDTO;

import com.Basics.onlineCoursePlatform.DTO.ReviewDTO;
import com.Basics.onlineCoursePlatform.entity.Course;

import com.Basics.onlineCoursePlatform.entity.Review;
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
        modelMapper.createTypeMap(Review.class, ReviewDTO.class)
                .addMappings(mapper -> mapper.map(src -> src.getId(), ReviewDTO::setId))
                .addMappings(mapper -> mapper.map(src -> src.getCourse().getId(), ReviewDTO::setCourseId))
                .addMappings(mapper -> mapper.map(src -> src.getUser().getId(), ReviewDTO::setUserId))
                .addMappings(mapper -> mapper.map(src -> src.getUser().getName(), ReviewDTO::setUserName));
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

