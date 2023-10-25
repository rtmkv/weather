package org.example.whether.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApiException extends Exception {

  private int code;
  private String type;
  private String info;

  @Override
  public String getMessage() {
    return toString();
  }
}
