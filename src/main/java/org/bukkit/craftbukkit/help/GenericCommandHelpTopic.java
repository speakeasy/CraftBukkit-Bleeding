package org.bukkit.craftbukkit.help;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.help.HelpTopic;

public class GenericCommandHelpTopic implements HelpTopic {

    private Command command;

    public GenericCommandHelpTopic(Command command) {
        this.command = command;
    }

    public boolean canSee(CommandSender sender) {
        if (!command.isRegistered() && !(command instanceof VanillaCommand)) {
            // Unregistered commands should not show up in the help (ignore VanillaCommands)
            return false;
        }

        if (sender instanceof ConsoleCommandSender) {
            return true;
        }

        return command.testPermissionSilent(sender);
    }

    public String getName() {
        return command.getLabel();
    }

    public String getShortText() {
        return command.getUsage();
    }

    public String getFullText(CommandSender sender) {
        StringBuffer sb = new StringBuffer();

        sb.append(ChatColor.GOLD);
        sb.append("Description: ");
        sb.append(ChatColor.WHITE);
        sb.append(command.getDescription());

        sb.append("\n");

        sb.append(ChatColor.GOLD);
        sb.append("Usage: ");
        sb.append(ChatColor.WHITE);
        sb.append(command.getUsage());

        if (command.getAliases().size() > 0) {
            sb.append("\n");
            sb.append(ChatColor.GOLD);
            sb.append("Aliases: ");
            sb.append(ChatColor.WHITE);
            sb.append(ChatColor.WHITE + StringUtils.join(command.getAliases(), ", "));
        }
        return sb.toString();
    }
}
