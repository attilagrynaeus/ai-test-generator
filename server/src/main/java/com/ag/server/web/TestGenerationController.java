package com.ag.server.web;

import com.ag.server.core.TestGenerationService;
import com.ag.server.web.dto.ApiSpecRequest;
import com.ag.server.web.dto.GeneratedTestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST‑endpoint.
 */
@Validated
@RestController
@RequestMapping("/api")
public class TestGenerationController {

   private final TestGenerationService service;

   public TestGenerationController(TestGenerationService service) {
      this.service = service;
   }

   @Operation(summary = "Generate JUnit5 tests for an API specification", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = ApiSpecRequest.class))), responses = {
         @ApiResponse(responseCode = "200", description = "Generated", content = @Content(schema = @Schema(implementation = GeneratedTestResponse.class))),
         @ApiResponse(responseCode = "400", description = "Invalid specification"),
         @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
         @ApiResponse(responseCode = "500", description = "Internal error")})
   @PostMapping(path = "/generate-tests", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<GeneratedTestResponse> generate(@Valid @RequestBody ApiSpecRequest req) {
      return ResponseEntity.ok(new GeneratedTestResponse(service.generateTests(req.content())));
   }
}
