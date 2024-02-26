package com.hako.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
public class servletConfig implements WebMvcConfigurer {

	final String UPLOAD_HANDLER = "/upload/**";
	final String STATIC_HANDLER = "resources/**";
	final String STATIC_PATH = "classpath:/static/";
	
	@Value("${blog.uploadPath}")
	private String uploadPath;

	@Bean
	public ViewResolver viewResolver() {
		// VIEW 지정
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		// 접두사(ex./WEB-INF/views/main.jsp)
		resolver.setPrefix("/WEB-INF/View/");
		// 접미사

		resolver.setSuffix(".jsp");

		return resolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler(UPLOAD_HANDLER).addResourceLocations(uploadPath);
		registry.addResourceHandler(STATIC_HANDLER).addResourceLocations(STATIC_PATH);
	}

}
