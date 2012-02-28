package org.bukkit.craftbukkit.help;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.util.ChatPaginator;

import java.util.Collection;

public class DefaultHelpTopic implements HelpTopic {

    private Collection<HelpTopic> allTopics;

    public DefaultHelpTopic(Collection<HelpTopic> allTopics) {
        this.allTopics = allTopics;
    }

    public boolean canSee(CommandSender sender) {
        return true;
    }

    public String getName() {
        return "Overall";
    }

    public String getShortText() {
        return "";
    }

    public String getFullText(CommandSender sender) {
        StringBuilder sb = new StringBuilder();
        for (HelpTopic topic : allTopics) {
            if (topic.canSee(sender)) {
                StringBuilder line = new StringBuilder();
                line.append(ChatColor.GOLD);
                line.append(topic.getName().startsWith("/") ? topic.getName() : "/" + topic.getName());
                line.append(": ");
                line.append(ChatColor.WHITE);
                line.append(topic.getShortText());

                String lineStr = line.toString().replace("\n", ". ");
                if (sender instanceof Player && lineStr.length() > ChatPaginator.DEFAULT_CHAT_WIDTH) {
                    sb.append(lineStr.substring(0, ChatPaginator.DEFAULT_CHAT_WIDTH - 3));
                    sb.append("...");
                } else {
                    sb.append(lineStr);
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
