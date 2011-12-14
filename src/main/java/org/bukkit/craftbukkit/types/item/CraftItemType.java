package org.bukkit.craftbukkit.types.item;

import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemSword;
import org.bukkit.types.item.BaseItemType;
import org.bukkit.types.item.ItemType;

public class CraftItemType extends BaseItemType {
    private final Item item;

    public CraftItemType(Item item) {
        super(item.id);
        this.item = item;
    }

    public Item getHandle() {
        return item;
    }

    public String getName() {
        return item.b();
    }

    public static ItemType fromNative(Item item) {
        if (item instanceof ItemBlock) {
            return new CraftBlockItem((ItemBlock)item);
        } else if (item instanceof ItemSword) {
            return new CraftItemSword((ItemSword)item);
        } else {
            return new CraftItemType(item);
        }
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
}
