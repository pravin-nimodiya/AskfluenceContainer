package com.ideas.askfluence.llm;

import com.ideas.askfluence.config.Sanitizer;

public class Prompt {
    String context;
    String userQuery;
    final String preText = "Answer the following question in a clear, single-paragraph summary (max 5 lines). "
            + "Do not repeat or speculate. Use Markdown-style reference links if available. "
            + "End the answer cleanly and do not continue past the answer.";
    static final String summarizeText = "Summarize the following content within max 5000 tokens so as to be able to " +
            "generate embeddings, including relevant reference " +
            "links if available. ";


    public Prompt(String context, String userQuery) {
        this.context = context;
        this.userQuery = userQuery;
    }

    public String getPrompt() {
        return Sanitizer.sanitize(preText + "\n\nContext:\n" + context + "\n\nUser Question:\n" + userQuery + "\n\nAnswer:");
    }

    public String getSummaryPrompt() {
        return Sanitizer.sanitize(summarizeText+" Content: "  + context);
    }
}
