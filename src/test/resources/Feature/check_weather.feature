Feature: Weatherstack
  As a user
  I want to use Weatherstack
  So that i can get the weather

  Scenario Outline: when a user makes a request with valid request parameters, Weatherstack returns the weather

    Given a valid access_key
    When I check the weather in '<city>'
    Then Weatherstack returns the weather in '<city>'

    Examples:
      | city        |
      | New York    |
      | Moscow      |
      | Oymyakon    |
      | Novosibirsk |

  Scenario:  when a user makes a request with language parameter, Weatherstack returns an error

    Given a valid access_key
    And 'language' parameter is 'ru'
    When I check the weather in 'Moscow'
    Then Weatherstack returns an error
    And the error code is 105
    And the error type is 'function_access_restricted'
    And the error info is 'Your current Subscription Plan does not support this API Function.'

  Scenario: when a user makes a request for an invalid city, Weatherstack returns an error

    Given a valid access_key
    When I check the weather in '*****'
    Then Weatherstack returns an error
    And the error code is 615
    And the error type is 'request_failed'
    And the error info is 'Your API request failed.'

  Scenario: when a user makes a request without an access_key, Weatherstack returns an error

    When I check the weather in 'Moscow'
    Then Weatherstack returns an error
    And the error code is 101
    And the error type is 'missing_access_key'
    And the error info is 'You have not supplied an API Access Key.'

  Scenario: when a user makes a request with an invalid unit parameter, Weatherstack returns an error

    Given a valid access_key
    And 'units' parameter is 'T'
    When I check the weather in 'Moscow'
    Then Weatherstack returns an error
    And the error code is 606
    And the error type is 'invalid_unit'
    And the error info is 'You have specified an invalid unit'