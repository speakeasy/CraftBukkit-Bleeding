package org.bukkit.craftbukkit.command;

import net.minecraft.server.TileEntityCommand;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;

/**
 * Represents input from a command block
 */
public class CraftBlockCommandSender extends ServerCommandSender implements BlockCommandSender {
    private final TileEntityCommand commandBlock;
    public MutableBoolean state = null;

    public CraftBlockCommandSender(TileEntityCommand commandBlock) {
        super();
        this.commandBlock = commandBlock;
    }

    @Override
    public Block getBlock() {
        return commandBlock.getWorld().getWorld().getBlockAt(commandBlock.x, commandBlock.y, commandBlock.z);
    }

    @Override
    public void sendMessage(String message) {
    }

    @Override
    public void sendMessage(String[] messages) {
    }

    @Override
    public String getName() {
        return commandBlock.getName();
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of a block");
    }

    @Override
    public void setNextOutputState(boolean state) {
        MutableBoolean stateVariable = this.state;
        if (stateVariable == null) {
            throw new IllegalStateException("Unprepared to handle output");
        }
        stateVariable.setValue(state);
    }
}
