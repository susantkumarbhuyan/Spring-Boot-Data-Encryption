package com.kumarsusant.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
	private static final List<String> ENCRYPT_URI = Arrays.asList("/account/public/login2", "/account/public/login",
			"/account/public/getuser");
	@Autowired
	private RequestEncryptionInterceptor encryptionInterceptor;

	@Bean
	public FilterRegistrationBean<EncryptionFilter> decodingFilter() {
		FilterRegistrationBean<EncryptionFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(new EncryptionFilter());
		filterRegistrationBean.setUrlPatterns(ENCRYPT_URI);
		return filterRegistrationBean;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(encryptionInterceptor).addPathPatterns("/account/**");
	}
}
