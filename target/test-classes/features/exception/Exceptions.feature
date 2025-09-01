Feature: Test creating/displaying exceptions

  Scenario: Test MergeFailureException
    Given MergeFailureException created with an exception with message "test1"
    Then Exception should have message "java.lang.RuntimeException: test1"
    Given MergeFailureException created with message "test2"
    Then Exception should have message "test2"
    Given MergeFailureException created with no message
    Then Exception should have message ""

  Scenario: Test ParsingException
    Given ParsingException created with an exception with message "test1"
    Then Exception should have message "java.lang.RuntimeException: test1"
    Given ParsingException created with message "test2"
    Then Exception should have message "test2"
    Given ParsingException created with no message
    Then Exception should have message ""

  Scenario: Test RenameFailureException
    Given RenameFailureException created with an exception with message "test1"
    Then Exception should have message "java.lang.RuntimeException: test1"
    Given RenameFailureException created with message "test2"
    Then Exception should have message "test2"
    Given RenameFailureException created with no message
    Then Exception should have message ""

  Scenario: Test WritingException
    Given WritingException created with an exception with message "test1"
    Then Exception should have message "java.lang.RuntimeException: test1"
    Given WritingException created with message "test2"
    Then Exception should have message "test2"
    Given WritingException created with no message
    Then Exception should have message ""