package com.kumarsusant.config;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestEncryptionInterceptor implements HandlerInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(RequestEncryptionInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		ContentCachingRequestWrapper servletRequest = new ContentCachingRequestWrapper(request);
		String decryptedRequestBody = IOUtils.toString(servletRequest.getInputStream(), StandardCharsets.UTF_8);
		logger.debug("Inside RequestEncryptionInterceptor  -----------");
		logger.debug("RequestEncryptionInterceptor >> Decrypted_RequestBody  >>>> " + decryptedRequestBody);
		String type = request.getHeader("Content-Type");
		logger.debug("RequestEncryptionInterceptor >> Content-Type >>>> " + type);
		boolean isPost = "POST".equals(request.getMethod());
		if (isPost) {
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}
}
