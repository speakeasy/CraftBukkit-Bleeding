package org.bukkit.craftbukkit.help;

/**
 */
public class HelpTopicAmendment {
    private String topicName;
    private String shortText;
    private String fullText;

    public HelpTopicAmendment(String topicName, String shortText, String fullText) {
        this.fullText = fullText;
        this.shortText = shortText;
        this.topicName = topicName;
    }

    public String getFullText() {
        return fullText;
    }

    public String getShortText() {
        return shortText;
    }

    public String getTopicName() {
        return topicName;
    }
}
