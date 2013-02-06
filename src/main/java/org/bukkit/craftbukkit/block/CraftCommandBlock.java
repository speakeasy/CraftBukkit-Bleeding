package org.bukkit.craftbukkit.block;

import net.minecraft.server.TileEntityCommand;

import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.craftbukkit.CraftWorld;

public class CraftCommandBlock extends CraftBlockState implements CommandBlock {
    private final CraftWorld world;
    private final TileEntityCommand note;

    public CraftCommandBlock(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        note = (TileEntityCommand) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public String getCommand() {
        return note.a;
    }

    public void setCommand(String command) {
        note.b(command);
    }

    public void execute() {
        note.a(world.getHandle());
    }

}
