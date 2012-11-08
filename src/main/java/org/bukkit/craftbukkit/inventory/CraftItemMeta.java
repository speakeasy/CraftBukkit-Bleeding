package org.bukkit.craftbukkit.inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

class CraftItemMeta implements ItemMeta {
    CraftItemMeta() {}

    public CraftItemMeta clone() {
        try {
            return (CraftItemMeta) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    abstract void applyToItem(CraftItemStack item);

    boolean applicableTo(ItemStack itemstack) {
        return true;
    }

    boolean isEmpty() {
        return true;
    }
}
