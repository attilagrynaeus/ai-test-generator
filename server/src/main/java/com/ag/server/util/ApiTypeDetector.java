package com.ag.server.util;

import java.util.Locale;

import com.ag.server.core.ApiType;

/**
 * Very fast heuristic API‑type detector.
 */
public final class ApiTypeDetector {

   private ApiTypeDetector() {
   }

   public static ApiType detect(String spec) {
      String lower = spec.toLowerCase(Locale.ROOT);
      if (lower.contains("openapi") || lower.contains("\"openapi\"")) {
         return ApiType.REST;
      }
      if (lower.contains("type query") || lower.contains("schema {") || lower.contains("type mutation")) {
         return ApiType.GRAPHQL;
      }
      return null;
   }
}
