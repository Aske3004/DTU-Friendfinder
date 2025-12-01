Feature: Chat with friends and groups

  Background:
    Given I am logged in

  Scenario: Send and receive messages in real time
    And I have friends or groups
    When I send a message "Hello everyone" in chat
    Then my friends or group members see the message instantly
    And I can see their replies in real time