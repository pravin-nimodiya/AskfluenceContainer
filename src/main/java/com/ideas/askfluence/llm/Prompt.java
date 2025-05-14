package com.ideas.askfluence.llm;

import com.ideas.askfluence.config.Sanitizer;

public class Prompt {
    String context;
    String userQuery;
    final String preText = "Answer the following question using only the provided context. \n" +
            "Respond in a single, concise paragraph (max 5 sentences). \n" +
            "Avoid speculation, repetition, or unnecessary detail. \n" +
            "Use Markdown-style reference links if applicable. \n" +
            "End the response cleanly.\n";
    static final String summarizeText = "Summarize the following content within max 5000 tokens so as to be able to " +
            "generate embeddings, including relevant reference " +
            "links if available. ";


    public Prompt(String context, String userQuery) {
        this.context = context;
        this.userQuery = userQuery;
    }

    public String getPrompt() {
       return Sanitizer.sanitize(
                preText + "\n\n"
                        + "### Context:\n" + context + "\n\n"
                        + "### Question:\n" + userQuery + "\n\n"
                        + "### Answer:"
        );
    }

    public String getSummaryPrompt() {
        return Sanitizer.sanitize(summarizeText+" Content: "  + context);
    }
}
