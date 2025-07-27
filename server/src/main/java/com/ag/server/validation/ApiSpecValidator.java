package com.ag.server.validation;

import com.ag.server.core.ApiType;
import com.ag.server.exception.InvalidApiSpecException;
import graphql.language.Document;
import graphql.parser.Parser;
import io.swagger.v3.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.springframework.stereotype.Component;

/**
 * Best‑effort validation of API specs.
 * – OpenAPI: swagger‑parser
 * – GraphQL: graphql‑java parser
 */
@Component
public class ApiSpecValidator {

   public void validate(String specContent, ApiType type) {
      switch (type) {
         case REST -> validateOpenApi(specContent);
         case GRAPHQL -> validateGraphQl(specContent);
         default -> throw new InvalidApiSpecException("Unsupported API type: " + type);
      }
   }

   private void validateOpenApi(String content) {
      ParseOptions opts = new ParseOptions();
      opts.setResolve(false);
      var result = new OpenAPIParser().readContents(content, null, opts);
      if (!result.getMessages().isEmpty()) {
         throw new InvalidApiSpecException("OpenAPI validation errors: " + result.getMessages());
      }
   }

   private void validateGraphQl(String content) {
      try {
         Document doc = new Parser().parseDocument(content);
         if (doc.getDefinitions().isEmpty()) {
            throw new InvalidApiSpecException("GraphQL spec contains no definitions.");
         }
      } catch (Exception ex) {
         throw new InvalidApiSpecException("GraphQL validation error: " + ex.getMessage());
      }
   }
}
