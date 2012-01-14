package org.bukkit.craftbukkit.types.block;

import net.minecraft.server.Block;
import net.minecraft.server.Item;
import org.bukkit.craftbukkit.types.item.CraftItemType;
import org.bukkit.types.block.BlockType;

public class CraftBlockType extends CraftItemType implements BlockType {
    private final Block block;

    public CraftBlockType(Item item) {
        super(item);
        this.block = Block.byId[item.id];
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public String getName() {
        return block.m();
    }

    public void setLightEmission(int amount) {
        if (amount < 0 || amount > 15) {
            throw new IllegalArgumentException("Light emission must be between 0 and 15");
        }
        Block.s[block.id] = amount;
    }

    public int getLightEmission() {
        int result = Block.s[block.id];
        if (result > 15) result = 15;
        if (result < 0) result = 0;
        return result;
    }

    public void setLightBlock(int amount) {
        if (amount < 0 || amount > 15) {
            throw new IllegalArgumentException("Light emission must be between 0 and 15");
        }
        if (amount == 15) amount = 255; // Sanitize for MC
        Block.q[block.id] = amount;
    }

    public int getLightBlock() {
        int result = Block.q[block.id];
        if (result > 15) result = 15;
        if (result < 0) result = 0;
        return result;
    }

    public static BlockType blockFromNative(Item item) {
        return new CraftBlockType(item);
    }
}
