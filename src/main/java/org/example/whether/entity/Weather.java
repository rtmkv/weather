package org.example.whether.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather {

  private int temperature;
  private int windSpeed;
  private int pressure;
  private int feelsLike;
  private Location location;

  @SuppressWarnings("unchecked")
  @JsonProperty("current")
  public void unpackCurrent(Map<String, Object> current) {
    this.temperature = (int) current.get("temperature");
    this.windSpeed = (int) current.get("wind_speed");
    this.pressure = (int) current.get("pressure");
    this.feelsLike = (int) current.get("feelslike");
  }
}
