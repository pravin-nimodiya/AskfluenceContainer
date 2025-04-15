package com.ideas.askfluence.llm;

import com.ideas.askfluence.config.Sanitizer;

public class Prompt {
    String context;
    String userQuery;
    final String preText ="Answer the following question in a well-formatted and manner, including relevant reference " +
            "links if available. Ensure the links are in Markdown format: [text](URL). Question: ";


    public Prompt(String context, String userQuery) {
        this.context = context;
        this.userQuery = userQuery;
    }

    public String getPrompt() {
        return Sanitizer.sanitize(preText + " Context: " + context + "\n User Question: " + userQuery +"\n Answer: ");
    }
}
