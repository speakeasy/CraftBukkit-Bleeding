package org.bukkit.craftbukkit.types.item;

import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;
import org.bukkit.types.BlockItem;

public class CraftBlockItem extends BlockItem {
    private final ItemBlock item;

    public CraftBlockItem(ItemBlock item) {
        super(((Item)item).id); // It manages to make id private. Wtf?
        this.item = item;
    }

    @Override
    public String getName() {
        return item.k();
    }
}
