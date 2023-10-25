package org.example.whether.api;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.stream.Collectors;
import org.example.whether.entity.Weather;
import org.example.whether.exception.ApiException;

public class WeatherstackApi {

  private static final String api = "http://api.weatherstack.com/current";
  private static final HttpClient client = HttpClient.newBuilder().build();
  private static final ObjectMapper om = new ObjectMapper();

  public static Weather getWeather(Map<String, String> params) throws ApiException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(buildURI(params))
        .build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    JsonNode node = om.readTree(response.body());
    if (node.has("error")) {
      throw om.treeToValue(node.get("error"), ApiException.class);
    }
    return om.treeToValue(node, Weather.class);
  }

  private static URI buildURI(Map<String, String> params) {
    return (params == null || params.isEmpty())
        ? URI.create(api)
        : URI.create(api + "?" + params.keySet().stream()
            .map(key -> encode(key, UTF_8) + "=" + encode(params.get(key), UTF_8))
            .collect(Collectors.joining("&")));
  }
}
