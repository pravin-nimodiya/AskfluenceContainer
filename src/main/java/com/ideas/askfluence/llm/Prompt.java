package com.ideas.askfluence.llm;

import com.ideas.askfluence.config.Sanitizer;

public class Prompt {
    String context;
    String userQuery;
    static final String summarizeText = "Summarize the following content within max 5000 tokens so as to be able to " +
            "generate embeddings, including relevant reference " +
            "links if available. ";


    public Prompt(String context, String userQuery) {
        this.context = context;
        this.userQuery = userQuery;
    }

    public String getPrompt() {
       return Sanitizer.filterPrompt(
         "You are a helpful assistant. Only answer the final question enclosed within <QUESTION> tags. " +
                "Do not answer or summarize other questions in the context. Do not repeat any questions. " +
                "Format any links in Markdown if needed.Include relevant reference and \" +\n" +
                 "links if available\n" +
                "<CONTEXT>\n" +
                context + "\n" +
                "</CONTEXT>\n\n" +
                "<QUESTION>\n" +
                userQuery + "\n" +
                "</QUESTION>");
    }

    public String getSummaryPrompt() {
        return Sanitizer.sanitize(summarizeText+" Content: "  + context);
    }
}
