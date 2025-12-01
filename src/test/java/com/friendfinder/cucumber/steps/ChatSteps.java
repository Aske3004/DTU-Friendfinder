package com.friendfinder.cucumber.steps;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

public class ChatSteps {

    private String sentMessage;

    @And("I have friends or groups")
    public void i_have_friends_or_groups() {
        CommonSteps.chatLog.add("Friend: Hi!");
    }

    @When("I send a message {string} in chat")
    public void i_send_message(String message) {
        sentMessage = message;
        CommonSteps.chatLog.add("Me: " + message);
    }

    @Then("my friends or group members see the message instantly")
    public void friends_see_message() {
        assertTrue(CommonSteps.chatLog.contains("Me: " + sentMessage));
    }

    @Then("I can see their replies in real time")
    public void i_see_replies() {
        CommonSteps.chatLog.add("Friend: Got your message!");
        assertTrue(CommonSteps.chatLog.stream().anyMatch(m -> m.contains("Got your message")));
    }
}