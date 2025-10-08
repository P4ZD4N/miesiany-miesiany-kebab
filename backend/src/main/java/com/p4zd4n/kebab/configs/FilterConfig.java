package com.p4zd4n.kebab.configs;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

  @Bean
  public FilterRegistrationBean<RateLimitFilter> rateLimitFilter(MessageSource messageSource) {
    FilterRegistrationBean<RateLimitFilter> bean = new FilterRegistrationBean<>();

    bean.setFilter(new RateLimitFilter(messageSource));
    bean.addUrlPatterns(
        "/api/v1/orders/add-order",
        "/api/v1/jobs/add-job-offer-application",
        "/api/v1/newsletter/subscribe");

    return bean;
  }
}
