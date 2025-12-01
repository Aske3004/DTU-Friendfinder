Feature: Friends list management

  Background:
    Given I am logged in

  Scenario: View friends list
    When I view my friends list
    Then I see all the students I am connected with

  Scenario: Add a new friend
    When I add a new friend "s654321@student.dtu.dk"
    Then they appear in my friends list

  Scenario: Remove a friend
    When I remove a friend "s654321@student.dtu.dk"
    Then they no longer appear in my friends list