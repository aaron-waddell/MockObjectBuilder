package com.shaw.mock.builder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.shaw.mock.object.BeanObject;

@Configuration
@ComponentScan(basePackages = "com.shaw.mock.object")
public class AppConfig {
	
//	@Bean
//	public BeanObject beanObject()
//	{
//		return BeanObject.getInstance();
//	}
}
