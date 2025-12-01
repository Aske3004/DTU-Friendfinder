package com.friendfinder.cucumber.steps;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

public class CreateUserSteps {

    private String email;
    private boolean registrationSuccess;
    private String errorMessage;

    @Given("I am a new student")
    public void i_am_a_new_student() {
    }

    @When("I register with email {string}")
    public void i_register_with_email(String email) {
        this.email = email;
        if (email.matches("^s\\d{6}@student\\.dtu\\.dk$")) {
            registrationSuccess = true;
        } else {
            registrationSuccess = false;
            errorMessage = "Please use your DTU email";
        }
    }


    @Then("my account is created successfully")
    public void my_account_is_created_successfully() {
        assertTrue(registrationSuccess);
    }

    @Then("my account creation is rejected")
    public void my_account_creation_is_rejected() {
        assertFalse(registrationSuccess);
    }

    @Then("I see an error message {string}")
    public void i_see_an_error_message(String expectedMessage) {
        assertEquals(expectedMessage, errorMessage);
    }
}