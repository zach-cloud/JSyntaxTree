Feature: Test analysis service

  # Scope report tests

  Scenario: Test variable scope report for unused
    Given input file: "war3map1"
    When variable scope is found for "udg_myUnit"
    Then scope result should be unused

  Scenario: Test variable scope report for single use
    Given input file: "war3map1"
    When variable scope is found for "gg_trg_trig1"
    Then scope result should be single use

  Scenario: Test variable scope report for global
    Given input file: "war3map1"
    When variable scope is found for "udg_myVar"
    Then scope result should be global

  # Isolate tests (negative)

  Scenario: Test variable isolation for non-existent variable
    Given input file: "war3map1"
    Given aggressive expansion style
    Then isolate variable should throw exception for "qwerty"

  Scenario: Test variable isolation for non-existent function
    Given input file: "war3map1"
    Given aggressive expansion style
    Then isolate function should throw exception for "qwerty"

  # Isolate tests
  # This test case works with either expansion method

  Scenario: Test variable isolation with depth 1
    Given input file: "war3map1"
    Given aggressive expansion style
    When input tree is isolated for variable usage with depth 1 and name "udg_myVar"
    Then result should have 2 function usages and 1 variable usages

  # This test case to only work with AGGRESSIVE EXPANSION

  Scenario: Test function isolation with depth 1 selects calling function
    Given input file: "war3map1"
    Given aggressive expansion style
    When input tree is isolated for function usage with depth 1 and name "InitGlobals"
    Then result should have 2 function usages and 1 variable usages

  Scenario: Test function isolation with depth 999 selects fully scoped
    Given input file: "war3map1"
    Given aggressive expansion style
    When input tree is isolated for function usage with depth 999 and name "InitGlobals"
    Then result should have 9 function usages and 4 variable usages

  # These test cases to only work with NON-AGGRESSIVE EXPANSION

  Scenario: Test function isolation with depth 1
    Given input file: "war3map1"
    Given passive expansion style
    When input tree is isolated for function usage with depth 1 and name "Trig_trig3_Actions"
    Then result should have 1 function usages and 2 variable usages

  Scenario: Test variable isolation with depth 2
    Given input file: "war3map1"
    Given passive expansion style
    When input tree is isolated for variable usage with depth 2 and name "udg_myVar"
    Then result should have 2 function usages and 2 variable usages

  Scenario: Test function isolation with depth 2
    Given input file: "war3map1"
    Given passive expansion style
    When input tree is isolated for function usage with depth 2 and name "Trig_trig3_Actions"
    Then result should have 3 function usages and 2 variable usages

  Scenario: Test function isolation with depth 3
    Given input file: "war3map1"
    Given passive expansion style
    When input tree is isolated for function usage with depth 3 and name "Trig_trig3_Actions"
    Then result should have 4 function usages and 2 variable usages

  Scenario: Test function isolation with infinite depth
    Given input file: "war3map1"
    Given passive expansion style
    When input tree is isolated for function usage with depth -1 and name "Trig_trig3_Actions"
    Then result should have 4 function usages and 2 variable usages

  Scenario: Test variable isolation with infinite depth
    Given input file: "war3map1"
    Given passive expansion style
    When input tree is isolated for variable usage with depth -1 and name "udg_myVar"
    Then result should have 4 function usages and 2 variable usages