package com.ag.cli;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ai-test-generator", mixinStandardHelpOptions = true, version = "1.0", description = "CLI for AI Test Generator SaaS")
public class AiTestGeneratorCli implements Callable<Integer> {

   @Option(names = {"generate"}, description = "generate tests", required = false)
   boolean generate;

   @Option(names = {"--spec"}, description = "Path to API spec file (.yaml/.graphql)", required = true)
   String specPath;

   @Option(names = {"--output"}, description = "Output directory", required = true)
   String outputDir;

   @Option(names = {"--endpoint"}, description = "API endpoint", defaultValue = "http://localhost:8080/api/generate-tests")
   String endpoint;

   public static void main(String[] args) {
      int exitCode = new CommandLine(new AiTestGeneratorCli()).execute(args);
      System.exit(exitCode);
   }

   @Override
   public Integer call() throws Exception {
      String specContent = Files.readString(Path.of(specPath));
      // Escape quotation marks for JSON
      String json = "{\"content\":\"" + specContent.replace("\"", "\\\"") + "\"}";
      String result = callApi(json);
      Files.createDirectories(Path.of(outputDir));
      Path file = Path.of(outputDir, "GeneratedTests.java");
      Files.writeString(file, result);
      System.out.println("Test code written to " + file.toAbsolutePath());
      return 0;
   }

   private String callApi(String body) throws IOException, InterruptedException {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest req = HttpRequest
            .newBuilder()
            .uri(URI.create(endpoint))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
      HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
      if (resp.statusCode() != 200) {
         throw new RuntimeException("API error: " + resp.body());
      }
      return new ObjectMapper().readTree(resp.body()).get("testCode").asText();
   }
}
