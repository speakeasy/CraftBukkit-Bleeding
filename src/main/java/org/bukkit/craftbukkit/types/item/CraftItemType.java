package org.bukkit.craftbukkit.types.item;

import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemSword;
import org.bukkit.craftbukkit.types.block.CraftBlockType;
import org.bukkit.types.block.BlockType;
import org.bukkit.types.block.Blocks;
import org.bukkit.types.item.BaseItemType;
import org.bukkit.types.item.ItemType;
import org.bukkit.types.item.Items;

public class CraftItemType extends BaseItemType {
    private final Item item;

    public CraftItemType(Item item) {
        super(item.id);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public String getName() {
        return item.b();
    }

    public int getMaxUses() {
        return item.getMaxDurability();
    }

    public void setMaxUses(int uses) {
        if (uses < 0) {
            throw new IllegalArgumentException("Max uses cannot be below zero");
        }
        item.f(uses);
    }

    public static void registerNative(Item raw) {
        ItemType item;

        if (raw instanceof ItemBlock) {
            BlockType block = CraftBlockType.blockFromNative(raw);
            item = block;

            Blocks.registerBlock(block);
        } else {
            item = itemFromNative(raw);
        }

        Items.registerItem(item);
    }

    public static ItemType itemFromNative(Item item) {
        if (item instanceof ItemSword) {
            return new CraftItemSword((ItemSword)item);
        } else {
            return new CraftItemType(item);
        }
    }
}
