package com.ag.server.config;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Centralised Caffeine cache configuration.
 */
@Configuration
public class CacheConfig {

   @Bean
   public CacheManager cacheManager() {
      CaffeineCacheManager mgr = new CaffeineCacheManager("testGenerationCache");
      mgr.setCaffeine(Caffeine.newBuilder().maximumSize(1_000).expireAfterWrite(Duration.ofHours(12)));
      return mgr;
   }
}
