package org.bukkit.craftbukkit.help;

import org.bukkit.command.Command;
import org.bukkit.command.MultipleCommandAlias;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicFactory;

import java.util.*;

public class SimpleHelpMap implements HelpMap {
    
    private HelpTopic defaultTopic;
    private Map<String, HelpTopic> helpTopics;
    private Map<Class, HelpTopicFactory> topicFactoryMap;

    public SimpleHelpMap() {
        helpTopics = new TreeMap<String, HelpTopic>(new HelpTopicComparator()); // Using a TreeMap for its explicit sorting on key
        defaultTopic = new DefaultHelpTopic(helpTopics.values());
        topicFactoryMap = new HashMap<Class, HelpTopicFactory>();

        registerHelpTopicFactory(MultipleCommandAlias.class, new MultipleCommandAliasHelpTopicFactory());
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
        // Existing topics take priority
        if (!helpTopics.containsKey(topic.getName())) {
            helpTopics.put(topic.getName(), topic);
        }
    }

    public synchronized void clear() {
        helpTopics.clear();
    }

    // ** Load topics from highest to lowest priority order **
    public synchronized void initializeHelpYaml(CraftServer server) {
        HelpYamlReader reader = new HelpYamlReader(server);

        // Initialize general help topics from the help.yml file
        for (HelpTopic topic : reader.getGeneralTopics()) {
            addTopic(topic);
        }

        // Initialize command topic overrides from the help.yml file
    }

    public synchronized void initializeCommands(CraftServer server) {
        // Initialize help topics from the server's command map
        for (Command command : server.getCommandMap().getCommands()) {
            if (topicFactoryMap.containsKey(command.getClass())) {
                addTopic(topicFactoryMap.get(command.getClass()).createTopic(command));
            } else {
                addTopic(new GenericCommandHelpTopic(command));
            }
        }

        // Initialize help topics from the server's fallback commands
        for (VanillaCommand command : server.getCommandMap().getFallbackCommands()) {
            addTopic(new GenericCommandHelpTopic(command));
        }
    }

    public void registerHelpTopicFactory(Class commandClass, HelpTopicFactory factory) {
        if (!Command.class.isAssignableFrom(commandClass)) {
            throw new IllegalArgumentException("commandClass must implement Command");
        }
        topicFactoryMap.put(commandClass, factory);
    }
}
