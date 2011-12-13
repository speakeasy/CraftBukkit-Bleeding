package org.bukkit.craftbukkit.types.item;

import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;
import org.bukkit.types.ItemType;

public class CraftItemType extends ItemType {
    private final Item item;

    public CraftItemType(Item item) {
        super(item.id);
        this.item = item;
    }

    @Override
    public String getName() {
        return item.k();
    }

    public static ItemType fromNative(Item item) {
        if (item instanceof ItemBlock) {
            return new CraftBlockItem((ItemBlock)item);
        } else {
            return new CraftItemType(item);
        }
    }
}
