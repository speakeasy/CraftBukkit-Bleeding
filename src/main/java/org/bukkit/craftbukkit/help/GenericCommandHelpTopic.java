package org.bukkit.craftbukkit.help;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.help.HelpTopic;

public class GenericCommandHelpTopic implements HelpTopic {

    private Command command;

    public GenericCommandHelpTopic(Command command) {
        this.command = command;
    }

    public boolean canSee(CommandSender sender) {
        if (!command.isRegistered()) {
            // Unregistered commands should not show up in the help
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

    public String getFullText() {
        StringBuffer sb = new StringBuffer();
        sb.append(command.getUsage());
        sb.append("\n");
        sb.append(command.getDescription());
        sb.append("\n");
        if (command.getAliases().size() > 0) {
            sb.append(command.getDescription() + "Aliases: " + StringUtils.join(command.getAliases(), ", "));
        }
        return sb.toString();
    }
}
