package com.smartsociety.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final SecretKey signingKey;

    private static final List<String> WHITELISTED_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/oauth2",
            "/login/oauth2",
            "/actuator",
            "/eureka",
            "/ws",
            "/v3/api-docs"
    );

    public JwtAuthenticationFilter(@Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Check if the current path is whitelisted
        boolean isWhitelisted = WHITELISTED_PATHS.stream().anyMatch(path::startsWith);
        if (isWhitelisted) {
            return chain.filter(exchange);
        }

        // Check Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header", path);
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.get("userId") != null ? String.valueOf(claims.get("userId")) : "";
            String role = claims.get("role") != null ? String.valueOf(claims.get("role")) : "";
            String societyId = claims.get("societyId") != null ? String.valueOf(claims.get("societyId")) : "";
            String email = claims.getSubject() != null ? claims.getSubject() : "";

            // Inject downstream headers
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .header("X-User-Society", societyId)
                    .header("X-User-Email", email)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (ExpiredJwtException ex) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "JWT token has expired", path);
        } catch (JwtException | IllegalArgumentException ex) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid JWT token: " + ex.getMessage(), path);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String detail, String path) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        // RFC 7807 Problem Details representation
        String rfc7807Body = String.format("""
                {
                  "type": "about:blank",
                  "title": "%s",
                  "status": %d,
                  "detail": "%s",
                  "instance": "%s",
                  "timestamp": "%s"
                }
                """, status.getReasonPhrase(), status.value(), detail, path, Instant.now().toString());

        byte[] bytes = rfc7807Body.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100; // High precedence before routing
    }
}
