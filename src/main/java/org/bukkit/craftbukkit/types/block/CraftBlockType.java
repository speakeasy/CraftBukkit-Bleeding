package org.bukkit.craftbukkit.types.block;

import net.minecraft.server.Block;
import org.bukkit.types.block.BaseBlockType;
import org.bukkit.types.block.BlockType;

public class CraftBlockType extends BaseBlockType {
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

    @Override
    public void setLightEmission(int amount) {
        if (amount < 0 || amount > 15) {
            throw new IllegalArgumentException("Light emission must be between 0 and 15");
        }
        Block.s[block.id] = amount;
    }

    @Override
    public int getLightEmission() {
        int result = Block.s[block.id];
        if (result > 15) result = 15;
        if (result < 0) result = 0;
        return result;
    }

    @Override
    public void setLightBlock(int amount) {
        if (amount < 0 || amount > 15) {
            throw new IllegalArgumentException("Light emission must be between 0 and 15");
        }
        if (amount == 15) amount = 255; // Sanitize for MC
        Block.q[block.id] = amount;
    }

    @Override
    public int getLightBlock() {
        int result = Block.q[block.id];
        if (result > 15) result = 15;
        if (result < 0) result = 0;
        return result;
    }

    public static BlockType fromNative(Block block) {
        return new CraftBlockType(block);
    }
}
