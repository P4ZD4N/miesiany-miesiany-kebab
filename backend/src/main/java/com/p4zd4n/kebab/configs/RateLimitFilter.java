package com.p4zd4n.kebab.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.exceptions.invalid.InvalidAcceptLanguageHeaderValue;
import com.p4zd4n.kebab.responses.exceptions.ExceptionResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RateLimitFilter implements Filter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RateLimitFilter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain
    ) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && isManagerOrEmployee(authentication)) {
            filterChain.doFilter(request, servletResponse);
            return;
        }

        String ip = request.getRemoteAddr();
        Bucket bucket = cache.computeIfAbsent(ip, this::newBucket);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, servletResponse);
        } else {
            Locale locale = getLocaleFromRequest(request);
            String messageKey = getMessageKeyForEndpoint(request.getRequestURI());
            String message = messageSource.getMessage(messageKey, null, locale);

            log.info("Received request to {} from IP {} with rate limit exceeded", request.getRequestURI(), ip);

            response.setContentType("application/json");
            response.setStatus(429);
            response.getWriter().write(objectMapper.writeValueAsString(
                    ExceptionResponse.builder()
                            .statusCode(429)
                            .message(message)
                            .build()
            ));
        }
    }

    private Bucket newBucket(String key) {
        return Bucket.builder()
                .addLimit(
                    Bandwidth.builder()
                        .capacity(1)
                        .refillGreedy(1, Duration.ofMinutes(5))
                        .build()
                )
                .build();
    }

    private boolean isManagerOrEmployee(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_MANAGER") || role.equals("ROLE_EMPLOYEE"));
    }

    private Locale getLocaleFromRequest(HttpServletRequest request) {
        String langHeader = request.getHeader("Accept-Language");
        Locale locale = Locale.forLanguageTag(langHeader);
        String language = locale.getLanguage();

        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(langHeader);
        }

        return locale;
    }

    private String getMessageKeyForEndpoint(String uri) {
        return switch (uri) {
            case "/api/v1/orders/add-order" -> "addOrder.rateLimitExceeded";
            case "/api/v1/jobs/add-job-offer-application" -> "addJobOfferApplication.rateLimitExceeded";
            case "/api/v1/newsletter/subscribe" -> "subscribe.rateLimitExceeded";
            default -> "";
        };
    }
}
