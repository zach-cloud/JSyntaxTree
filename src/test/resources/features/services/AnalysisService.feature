Feature: Test analysis service

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

  Scenario: Test function isolation with depth 999
    Given input file: "war3map1"
    Given passive expansion style
    When input tree is isolated for function usage with depth 999 and name "Trig_trig3_Actions"
    Then result should have 4 function usages and 2 variable usages

  Scenario: Test variable isolation with depth 999
    Given input file: "war3map1"
    Given passive expansion style
    When input tree is isolated for variable usage with depth 999 and name "udg_myVar"
    Then result should have 4 function usages and 2 variable usages