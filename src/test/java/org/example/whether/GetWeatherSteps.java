package org.example.whether;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.extern.log4j.Log4j2;
import org.example.whether.api.WeatherstackApi;
import org.example.whether.entity.Location;
import org.example.whether.entity.Weather;
import org.example.whether.exception.ApiException;

@Log4j2
public class GetWeatherSteps {

  private static final String ACCESS_KEY = "dab052a65ca4d1d1eab17a30a8155c57";
  private final Map<String, Weather> expectedWeatherMap = new HashMap<>();
  private Map<String, String> params;
  private Weather weather;
  private ApiException exception;

  @Before
  public void setExpectedValues() {
    Weather newYorkWeather = Weather.builder()
        .temperature(12)
        .windSpeed(4)
        .pressure(1025)
        .feelsLike(11)
        .location(Location.builder()
            .name("New York")
            .country("United States of America")
            .lat("40.714")
            .lon("74.006")
            .build())
        .build();
    Weather moscowWeather = Weather.builder()
        .temperature(-3)
        .windSpeed(11)
        .pressure(1018)
        .feelsLike(-5)
        .location(Location.builder()
            .name("Moscow")
            .country("Russia")
            .lat("55.752")
            .lon("37.616")
            .build())
        .build();
    Weather oymyakonWeather = Weather.builder()
        .temperature(-30)
        .windSpeed(5)
        .pressure(1027)
        .feelsLike(-35)
        .location(Location.builder()
            .name("Oymyakon")
            .country("Russia")
            .lat("63.467")
            .lon("142.125")
            .build())
        .build();
    Weather novosibirskWeather = Weather.builder()
        .temperature(8)
        .windSpeed(30)
        .pressure(1007)
        .feelsLike(5)
        .location(Location.builder()
            .name("Novosibirsk")
            .country("USA")
            .lat("55.041")
            .lon("82.934")
            .build())
        .build();
    expectedWeatherMap.put("New York", newYorkWeather);
    expectedWeatherMap.put("Moscow", moscowWeather);
    expectedWeatherMap.put("Oymyakon", oymyakonWeather);
    expectedWeatherMap.put("Novosibirsk", novosibirskWeather);

    params = new HashMap<>();
    weather = null;
    exception = null;
  }

  @Step
  @Given("a valid access_key")
  public void givenValidAccessKey() {
    params.put("access_key", ACCESS_KEY);
  }

  @Step
  @Given("{string} parameter is {string}")
  public void givenRequestParams(String key, String value) {
    params.put(key, value);
  }

  @Step
  @When("I check the weather in {string}")
  public void whenCheckTheWeatherIn(String city) throws IOException, InterruptedException {
    params.put("query", city);
    try {
      weather = WeatherstackApi.getWeather(params);
    } catch (ApiException e) {
      this.exception = e;
    }
  }

  @Step
  @Then("Weatherstack returns the weather in {string}")
  public void thenWeatherstackReturnsTheWeatherIn(String city) {
    Weather expected = expectedWeatherMap.get(city);
    logDiffs(city, expectedWeatherMap.get(city), weather);
    assertWeather(expected, weather);
  }

  @Step
  @Then("Weatherstack returns an error")
  public void thenWeatherstackReturnsAnError() {
    assertNull(weather);
    assertNotNull(exception);
  }

  @Step
  @Then("the error code is {int}")
  public void thenTheErrorCodeIs(int errorCode) {
    assertEquals(errorCode, exception.getCode());
  }

  @Step
  @Then("the error type is {string}")
  public void thenTheErrorTypeIs(String errorType) {
    assertEquals(errorType, exception.getType());
  }

  @Step
  @Then("the error info is {string}")
  public void thenTheErrorMessageIs(String errorInfo) {
    assertTrue(exception.getInfo().contains(errorInfo));
  }

  private void assertWeather(Weather expected, Weather actual) {
    assertNotNull(expected);
    assertNotNull(actual);
    assertEquals(expected.getTemperature(), actual.getTemperature());
    assertEquals(expected.getWindSpeed(), actual.getWindSpeed());
    assertEquals(expected.getPressure(), actual.getPressure());
    assertEquals(expected.getFeelsLike(), actual.getFeelsLike());
    assertLocation(expected.getLocation(), actual.getLocation());
  }

  private void assertLocation(Location expected, Location actual) {
    assertNotNull(expected);
    assertNotNull(actual);
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getCountry(), actual.getCountry());
    assertEquals(expected.getLat(), actual.getLat());
    assertEquals(expected.getLon(), actual.getLon());
  }

  @Attachment(value = "Test difference", type = "text/plain")
  private String logDiffs(String forQuery, Weather expected, Weather actual) {
    StringBuilder sb = new StringBuilder();
    String template = "%s %s: expected=%s, actual=%s";
    BiConsumer<String, Function<Weather, Object>> compareAndLog = (fieldName, extractor) -> {
      Object exp = extractor.apply(expected);
      Object act = extractor.apply(actual);
      if (!Objects.equals(exp, act)) {
        String difference = String.format(template, forQuery, fieldName, exp, act);
        sb.append(difference).append("\n");
        log.warn(difference);
      }
    };
    compareAndLog.accept("temperature", Weather::getTemperature);
    compareAndLog.accept("wind speed", Weather::getWindSpeed);
    compareAndLog.accept("pressure", Weather::getPressure);
    compareAndLog.accept("feels like", Weather::getFeelsLike);
    Location eLocation = expected.getLocation();
    Location aLocation = actual.getLocation();
    if (eLocation == null && aLocation == null) {
      return sb.toString();
    } else if (eLocation == null || aLocation == null) {
      String difference = String.format(template, forQuery, "location", eLocation, aLocation);
      sb.append(difference).append("\n");
      log.warn(difference);
      return sb.toString();
    }
    compareAndLog.accept("location name", w -> w.getLocation().getName());
    compareAndLog.accept("country", w -> w.getLocation().getCountry());
    compareAndLog.accept("lat", w -> w.getLocation().getLat());
    compareAndLog.accept("lon", w -> w.getLocation().getLon());
    return sb.toString();
  }
}