package com.ideas.askfluence.api;

import java.util.List;

public record AskRequest(String question, List<Long> spaces) {
}


