package org.bukkit.craftbukkit.help;

import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.help.HelpTopic;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 */
public class HelpYamlReader {

    private YamlConfiguration helpYaml;

    public HelpYamlReader(Server server) {
        File helpYamlFile = new File("help.yml");
        
        helpYaml = YamlConfiguration.loadConfiguration(helpYamlFile);
        helpYaml.options().copyDefaults(true);
        helpYaml.setDefaults(YamlConfiguration.loadConfiguration(getClass().getClassLoader().getResourceAsStream("configurations/help.yml")));
        try {
            if (!helpYamlFile.exists()) {
                helpYaml.save(helpYamlFile);
            }
        } catch (IOException ex) {
            server.getLogger().log(Level.SEVERE, "Could not save " + helpYamlFile, ex);
        }
    }
    
    public List<HelpTopic> getGeneralTopics() {
        List<HelpTopic> topics = new LinkedList<HelpTopic>();
        ConfigurationSection generalTopics = helpYaml.getConfigurationSection("general-topics");
        for (String topicName : generalTopics.getKeys(false)) {
            ConfigurationSection section = generalTopics.getConfigurationSection(topicName);
            String shortText = section.getString("shortText");
            String fullText = section.getString("fullText");
            String permission = section.getString("permission");
            topics.add(new CustomHelpTopic(topicName, shortText, fullText, permission));
        }
        return topics;
    }
    
    public List<HelpTopic> getCommandTopics() {
        return null;
    }
}
