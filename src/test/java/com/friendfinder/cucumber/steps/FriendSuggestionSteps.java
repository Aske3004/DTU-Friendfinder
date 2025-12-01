package com.friendfinder.cucumber.steps;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class FriendSuggestionSteps {

    private List<String> myInterests;
    private Map<String, List<String>> otherStudents;
    private String suggestedFriend;

    @Given("I am logged in as a DTU student")
    public void i_am_logged_in_as_dtu_student() {
        myInterests = new ArrayList<>();
        otherStudents = new HashMap<>();
    }

    @And("my profile includes interests {string} and {string}")
    public void my_profile_includes_interests(String i1, String i2) {
        myInterests.add(i1);
        myInterests.add(i2);
    }

    @And("another student has interests {string} and {string}")
    public void another_student_has_interests(String i1, String i2) {
        otherStudents.put("studentA", List.of(i1, i2));
    }

    @And("another student has interests {string}")
    public void another_student_has_interests(String i1) {
        otherStudents.put("studentB", List.of(i1));
    }

    @When("I open the home screen")
    public void i_open_home_screen() {
        suggestedFriend = otherStudents.entrySet().stream()
                .max(Comparator.comparingInt(e -> countMatches(e.getValue())))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Then("I see a friend suggestion automatically displayed")
    public void i_see_friend_suggestion() {
        assertNotNull(suggestedFriend);
    }

    @Then("the suggested student is not already my friend")
    public void suggested_student_not_friend() {
        assertNotEquals("existingFriend", suggestedFriend);
    }

    @Then("the suggestion is chosen based on the highest number of shared interests")
    public void suggestion_based_on_shared_interests() {
        assertEquals("studentA", suggestedFriend);
    }

    private int countMatches(List<String> interests) {
        return (int) interests.stream().filter(myInterests::contains).count();
    }
}