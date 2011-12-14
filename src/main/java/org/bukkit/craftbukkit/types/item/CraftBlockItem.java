package org.bukkit.craftbukkit.types.item;

import net.minecraft.server.ItemBlock;
import org.bukkit.types.block.BlockType;
import org.bukkit.types.block.Blocks;
import org.bukkit.types.item.BlockItem;

public class CraftBlockItem extends CraftItemType implements BlockItem {
    public CraftBlockItem(ItemBlock item) {
        super(item);
    }

    @Override
    public ItemBlock getHandle() {
        return (ItemBlock)super.getHandle();
    }

    public BlockType getBlockType() {
        return Blocks.get(getId());
    }
}
