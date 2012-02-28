package org.bukkit.craftbukkit.help;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.MultipleCommandAlias;
import org.bukkit.help.HelpTopic;

/**
 */
public class MultipleCommandAliasHelpTopic implements HelpTopic {

    private MultipleCommandAlias alias;

    public MultipleCommandAliasHelpTopic(MultipleCommandAlias alias) {
        this.alias = alias;
    }

    public boolean canSee(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }

        for (Command command : alias.getCommands()) {
            if (!command.testPermissionSilent(sender)) {
                return false;
            }
        }

        return true;
    }

    public String getName() {
        return "/" + alias.getLabel();
    }

    public String getShortText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < alias.getCommands().length; i++) {
            if (i != 0) {
                sb.append(ChatColor.GOLD + " > " + ChatColor.WHITE);
            }
            sb.append("/");
            sb.append(alias.getCommands()[i].getLabel());
        }
        return sb.toString();
    }

    public String getFullText(CommandSender sender) {
        return ChatColor.GOLD + "Alias for: " + ChatColor.WHITE + getShortText();
    }
}
