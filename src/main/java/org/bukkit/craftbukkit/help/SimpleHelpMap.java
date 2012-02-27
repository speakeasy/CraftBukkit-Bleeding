package org.bukkit.craftbukkit.help;

import org.bukkit.command.Command;
import org.bukkit.command.MultipleCommandAlias;
import org.bukkit.craftbukkit.CraftServer;
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
        if (topic.getName().startsWith("/")) {
            helpTopics.put(topic.getName(), topic);
        } else {
            helpTopics.put("/" + topic.getName(), topic);
        }
    }

    public synchronized void clear() {
        helpTopics.clear();
    }

    public synchronized void initialize(CraftServer server) {
        clear();

        // Initialize help topics from the server's command map
        for (Command command : server.getCommandMap().getCommands()) {
            if (command instanceof MultipleCommandAlias) {
                addTopic(new MultipleCommandAliasHelpTopic((MultipleCommandAlias)command));
            } else {
                addTopic(new GenericCommandHelpTopic(command));
            }
        }
    }
}
