package org.bukkit.craftbukkit.types;

import net.minecraft.server.Block;
import org.bukkit.types.block.BlockType;

public class CraftBlockType extends BlockType {
    private final Block block;

    public CraftBlockType(Block block) {
        super(block.id);
        this.block = block;
    }

    public Block getHandle() {
        return block;
    }

    @Override
    public String getName() {
        return block.m();
    }

    public static BlockType fromNative(Block block) {
        return new CraftBlockType(block);
    }
}
