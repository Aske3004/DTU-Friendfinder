Feature: Friend suggestion on home screen

  Scenario: Friend suggestion appears automatically
    Given I am logged in as a DTU student
    And my profile includes interests "Music" and "Gaming"
    And another student has interests "Music" and "Gaming"
    And another student has interests "Sports"
    When I open the home screen
    Then I see a friend suggestion automatically displayed
    And the suggested student is not already my friend
    And the suggestion is chosen based on the highest number of shared interests