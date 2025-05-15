package com.ideas.askfluence.index;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SpaceDetails {


    public SpaceDetails(JsonNode node){
        this.spaceId = node.get("id").asLong(); // Parent node ID
        JsonNode spaceNode = node.get("space");
        if (spaceNode != null) {
            this.spaceName = spaceNode.get("name").asText();
            this.spaceKey = spaceNode.get("key").asText();
        } else {
            throw new IllegalArgumentException("Missing 'space' object in JSON");
        }
    }

    Long spaceId;
    String spaceName;
    String spaceKey;
}
