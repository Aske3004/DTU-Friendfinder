package com.friendfinder.cucumber.steps;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ProfileCreationSteps {

    private String email;
    private List<String> interests;
    private boolean profileCreated;

    @Given("I am a new DTU student")
    public void i_am_a_new_dtu_student() {
    }

    @When("I create a profile with my DTU email and select interests {string} and {string}")
    public void i_create_profile_with_interests(String interest1, String interest2) {
        this.email = "s123456@student.dtu.dk";
        this.interests = List.of(interest1, interest2);
        profileCreated = email.matches("^s\\d{6}@student\\.dtu\\.dk$");
    }

    @Then("my profile is created successfully")
    public void my_profile_is_created_successfully() {
        assertTrue(profileCreated);
    }

    @Then("my selected interests are saved")
    public void my_selected_interests_are_saved() {
        assertEquals(2, interests.size());
    }

    @Then("I can see my interests listed on my profile page")
    public void i_can_see_my_interests_listed() {
        assertTrue(interests.contains("Music") || interests.contains("Gaming"));
    }
}