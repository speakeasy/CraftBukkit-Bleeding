package org.bukkit.craftbukkit.help;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.help.HelpTopic;

public class CustomHelpTopic implements HelpTopic {
    
    private String permissionNode;
    private String name;
    private String shortText;
    private String fullText;
    
    public CustomHelpTopic(String name, String shortText, String fullText, String permissionNode) {
        this.permissionNode = permissionNode;
        this.name = name;
        this.shortText = shortText;
        this.fullText = fullText;
    }
    
    public boolean canSee(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }

        if (!permissionNode.equals("")) {
            return sender.hasPermission(permissionNode);
        } else {
            return true;
        }
    }

    public String getName() {
        return name;
    }

    public String getShortText() {
        return shortText;
    }

    public String getFullText(CommandSender who) {
        return shortText + "\n" + fullText;
    }
}
