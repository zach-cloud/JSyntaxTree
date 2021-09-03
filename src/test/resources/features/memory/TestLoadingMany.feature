Feature: Test loading tons of maps to put memory strain on the syntax tree

  Scenario: Load war3map2 many times
    When The file war3map2 is loaded 100 times
    Then the syntax tree should not have crashed