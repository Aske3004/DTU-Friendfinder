package com.friendfinder.utils;

import java.util.List;

public record Table(String heading, List<String> columnNames, List<List<String>> rows, String editURL, Button button) {

}