package com.friendfinder.cucumber.steps;

import io.cucumber.java.en.Given;
import java.util.*;

public class CommonSteps {


    public static Set<String> friends;
    public static List<String> chatLog;

    @Given("I am logged in")
    public void i_am_logged_in() {
        friends = new HashSet<>();
        chatLog = new ArrayList<>();
        friends.add("s123456@student.dtu.dk");
    }
}
