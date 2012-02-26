package org.bukkit.craftbukkit.help;

import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;

import java.util.*;

public class SimpleHelpMap implements HelpMap {
    
    private HelpTopic defaultTopic;
    private Map<String, HelpTopic> helpTopics;

    public SimpleHelpMap() {
        helpTopics = new TreeMap<String, HelpTopic>(); // Using a TreeMap for its explicit sorting on key
        defaultTopic = new DefaultHelpTopic(helpTopics.values());
    }
    
    public synchronized HelpTopic getHelpTopic(String topicName) {
        if (topicName.equals("")) {
            return defaultTopic;
        }

        if (helpTopics.containsKey(topicName)) {
            return helpTopics.get(topicName);
        }

        return null;
    }

    public synchronized void addTopic(HelpTopic topic) {
        helpTopics.put(topic.getName(), topic);
    }

    public synchronized void clear() {
        helpTopics.clear();
    }
}
