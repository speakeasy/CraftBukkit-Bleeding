package org.bukkit.craftbukkit.help;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

import java.util.Collection;

public class DefaultHelpTopic implements HelpTopic {

    private Collection<HelpTopic> allTopics;

    public DefaultHelpTopic(Collection<HelpTopic> allTopics) {
        this.allTopics = allTopics;
    }

    public boolean canSee(CommandSender player) {
        return true;
    }

    public String getName() {
        return "Overall";
    }

    public String getShortText() {
        return "";
    }

    public String getFullText() {
        StringBuilder sb = new StringBuilder();
        for (HelpTopic topic : allTopics) {
            sb.append(ChatColor.GOLD);
            sb.append(topic.getName().startsWith("/") ? topic.getName() : "/" + topic.getName());
            sb.append(": ");
            sb.append(ChatColor.WHITE);
            sb.append(topic.getShortText());
            sb.append("\n");
        }
        return sb.toString();
    }
}
