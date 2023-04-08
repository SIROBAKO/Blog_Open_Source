package com.hako.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
public class servletConfig implements WebMvcConfigurer {

	@Bean
	public ViewResolver viewResolver(){
		// VIEW 지정
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		// 접두사(ex./WEB-INF/views/main.jsp)
		resolver.setPrefix("/WEB-INF/View/");
		// 접미사
		resolver.setSuffix(".jsp");
		
		return resolver;
	}

	private String connectPath = "/upload_image/**";
//  private String resourcePath = "file:///C:/Users/edwsq/Documents/workspace-spring-tool-suite-4-4.18.0.RELEASE/BootWebPorject/src/main/upload_image/";
	private String resourcePath = "file:///tomcat/webapps/upload_image/";

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler(connectPath).addResourceLocations(resourcePath);
		registry.addResourceHandler("resources/**").addResourceLocations("classpath:/static/");
	}

}
