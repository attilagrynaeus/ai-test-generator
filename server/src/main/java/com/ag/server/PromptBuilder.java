package com.ag.server;

/**
 * Centralised prompt‑builder for GPT‑4.
 */
public final class PromptBuilder {

   private PromptBuilder() {
   }

   public static String build(ApiType apiType, String spec) {
      String kind = (apiType == ApiType.REST) ? "REST" : "GraphQL";
      return """
            You are a senior Java developer and QA engineer.
            Generate a COMPLETE %s JUnit 5 test class.
            REST = MockMvc, GraphQL = WebTestClient.
            Include authentication mocks (Bearer / Basic) when securitySchemes are present.
            Cover happy‑path, edge‑cases and error responses.
            
            ### API specification:
            %s
            
            Provide valid Java code only (no markdown fences).
            """.formatted(kind, spec);
   }
}
