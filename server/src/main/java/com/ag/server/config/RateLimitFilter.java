package com.ag.server.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Egyszerű, IP‑alapú rate‑limiter Bucket4j‑jel.
 * Jelenleg 100 kérés / óra / IP.
 */
@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

   private static final int LIMIT = 100;
   private static final Duration WINDOW = Duration.ofHours(1);

   private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
   private final ObjectMapper mapper = new ObjectMapper();

   @Override
   protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {

      Bucket bucket = buckets.computeIfAbsent(
            resolveClientIp(req),
            ip -> Bucket.builder().addLimit(Bandwidth.classic(LIMIT, Refill.greedy(LIMIT, WINDOW))).build());

      if (bucket.tryConsume(1)) {
         chain.doFilter(req, res);
      } else {
         log.debug("Rate limit exceeded");
         sendLimitExceeded(res);
      }
   }

   private String resolveClientIp(HttpServletRequest req) {
      String xff = req.getHeader("X-Forwarded-For");
      return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : req.getRemoteAddr();
   }

   private void sendLimitExceeded(HttpServletResponse res) throws IOException {
      res.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
      res.setContentType(MediaType.APPLICATION_JSON_VALUE);
      mapper.writeValue(
            res.getWriter(),
            Map.of("timestamp", Instant.now().toString(), "status", 429, "error", "Too Many Requests", "message", "Hourly request quota exceeded."));
   }
}
