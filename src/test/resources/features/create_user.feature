Feature: User registration with DTU email

  Scenario: Successful registration with valid DTU email format
    Given I am a new student
    When I register with email "s123456@student.dtu.dk"
    Then my account is created successfully

  Scenario: Rejected registration with invalid DTU email format
    Given I am a new student
    When I register with email "student@gmail.com"
    Then my account creation is rejected
    And I see an error message "Please use your DTU email"

  Scenario: Rejected registration with malformed DTU email
    Given I am a new student
    When I register with email "alfred@student.dtu.dk"
    Then my account creation is rejected
    And I see an error message "Please use your DTU email"
