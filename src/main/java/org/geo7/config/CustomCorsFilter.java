package org.geo7.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.io.IOException;

@Configuration
public class CustomCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        System.out.println("=== CUSTOM CORS FILTER ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Request Method: " + request.getMethod());
        System.out.println("Origin: " + request.getHeader("Origin"));

        // Configurar headers CORS
        response.setHeader("Access-Control-Allow-Origin", "https://18-228-94-6.sslip.io");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Max-Age", "3600");

        // Se for uma requisição OPTIONS (preflight), responder imediatamente
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("OPTIONS request - responding with 200");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        System.out.println("Continuing filter chain");
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("CustomCorsFilter initialized");
    }

    @Override
    public void destroy() {
        System.out.println("CustomCorsFilter destroyed");
    }

    @Bean
    public FilterRegistrationBean<CustomCorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<CustomCorsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CustomCorsFilter());
        registration.addUrlPatterns("/*");
        registration.setName("customCorsFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        System.out.println("CustomCorsFilter registered with highest precedence");
        return registration;
    }
}
