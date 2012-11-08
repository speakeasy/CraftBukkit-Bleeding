package org.bukkit.craftbukkit.inventory;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CraftItemFactory implements ItemFactory {
    @SerializableAs("ItemMeta")
    class SerializableMeta implements ConfigurationSerializable {

        public Map<String, Object> serialize() {
            throw new UnsupportedOperationException();
        }

    }
    private static CraftItemFactory instance = new CraftItemFactory();

    // TODO: Add Enchantments
    private CraftItemFactory() {}

    public boolean isValidMeta(ItemMeta meta, ItemStack itemstack) {
        if (itemstack == null || !(meta instanceof CraftItemMeta)) {
            return false;
        }

        return ((CraftItemMeta) meta).applicableTo(itemstack);
    }

    private CraftItemMeta getItemMeta(Material material, ItemStack itemstack) {
        if (material == null) {
            material = itemstack.getType();

            if (material == null) {
                material = Material.AIR;
            }
        }

        switch (material) {
            case WRITTEN_BOOK:
            case BOOK_AND_QUILL:
                if (itemstack instanceof CraftItemStack) {
                    return new CraftBookMeta((CraftItemStack) itemstack);
                } else {
                    return new CraftBookMeta();
                }
            default:
                return new CraftItemMeta();
        }
    }

    public ItemMeta getItemMeta(ItemStack itemstack) {
        return getItemMeta(null, itemstack);
    }

    public ItemMeta getItemMeta(Material material) {
        return getItemMeta(material, null);
    }

    public boolean equals(ItemMeta meta1, ItemMeta meta2) {
        if (meta1 == meta2) {
            return true;
        }
        if (meta1 != null && !(meta1 instanceof CraftItemMeta)) {
            throw new IllegalArgumentException("First meta " + meta1 + " does not belong to " + this.getClass());
        }
        if (meta2 != null && !(meta2 instanceof CraftItemMeta)) {
            throw new IllegalArgumentException("Second meta " + meta2 + " does not belong to " + this.getClass());
        }
        if (meta1 == null) {
            return ((CraftItemMeta) meta2).isEmpty();
        }
        if (meta2 == null) {
            return ((CraftItemMeta) meta1).isEmpty();
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static CraftItemFactory instance() {
        return instance;
    }
}
