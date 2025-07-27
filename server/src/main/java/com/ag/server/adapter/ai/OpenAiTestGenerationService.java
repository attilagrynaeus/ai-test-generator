package com.ag.server.adapter.ai;

import com.ag.server.core.TestGenerationService;
import com.ag.server.exception.InvalidApiSpecException;
import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OpenAiTestGenerationService implements TestGenerationService {

   private final ChatClient chatClient;

   @Autowired
   public OpenAiTestGenerationService(ChatClient chatClient) {
      this.chatClient = chatClient;
   }

   @Override
   @Cacheable(value = "testGenerationCache", key = "#apiSpecContent.hashCode()", unless = "#result == null")
   public String generateTests(String apiSpecContent) {
      String apiType = detectApiType(apiSpecContent);
      if ("UNKNOWN".equals(apiType)) {
         throw new InvalidApiSpecException("Could not detect API type (OpenAPI or GraphQL).");
      }

      String prompt = String.format(
            """
                  You are a senior Java developer and test engineer.
                  Generate a COMPLETE %s JUnit5 test class.
                  REST = MockMvc, GraphQL = WebTestClient.
                  Include auth mocks (Bearer/Basic) if securitySchemes present.
                  Cover happy path, edge cases, error responses.
                  
                  ### API specification:
                  %s
                  
                  Provide valid Java code only.
                  """, apiType, apiSpecContent);

      return chatClient.prompt().user(prompt).call().content();
   }

   private String detectApiType(String spec) {
      String l = spec.toLowerCase();
       if (l.contains("openapi")) {return "REST";}
       if (l.contains("type query") || l.contains("type mutation")) {return "GraphQL";}
      return "UNKNOWN";
   }
}
