package com.friendfinder.cucumber.steps;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

public class FriendsListSteps {

    @When("I view my friends list")
    public void i_view_my_friends_list() {
    }

    @Then("I see all the students I am connected with")
    public void i_see_all_students() {
        assertFalse(CommonSteps.friends.isEmpty());
    }

    @When("I add a new friend {string}")
    public void i_add_new_friend(String friendEmail) {
        CommonSteps.friends.add(friendEmail);
    }

    @Then("they appear in my friends list")
    public void they_appear_in_my_friends_list() {
        assertTrue(CommonSteps.friends.contains("s654321@student.dtu.dk"));
    }

    @When("I remove a friend {string}")
    public void i_remove_friend(String friendEmail) {
        CommonSteps.friends.remove(friendEmail);
    }

    @Then("they no longer appear in my friends list")
    public void they_no_longer_appear() {
        assertFalse(CommonSteps.friends.contains("s654321@student.dtu.dk"));
    }
}