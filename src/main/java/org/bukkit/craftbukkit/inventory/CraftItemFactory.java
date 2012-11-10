package org.bukkit.craftbukkit.inventory;

import java.util.Map;

import org.apache.commons.lang.Validate;
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

    private CraftItemFactory() {}

    public boolean isValidMeta(ItemMeta meta, ItemStack itemstack) {
        if (itemstack == null || !(meta instanceof CraftItemMeta)) {
            return false;
        }

        return ((CraftItemMeta) meta).applicableTo(itemstack.getType());
    }

    private CraftItemMeta makeItemMeta(Material material) {
        switch (material) {
            case WRITTEN_BOOK:
            case BOOK_AND_QUILL:
                return new CraftBookMeta();
            case SKULL_ITEM:
                return new CraftSkullMeta();
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return new CraftLeatherArmorMeta();
            default:
                return new CraftItemMeta();
        }
    }

    public ItemMeta getItemMeta(ItemStack itemStack) {
        Validate.notNull(itemStack, "Stack cannot be null");
        Material material = itemStack.getType();
        return makeItemMeta(material == null ? Material.AIR : material);
    }

    public ItemMeta getItemMeta(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return makeItemMeta(material);
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

        return meta1.equals(meta2);
    }

    public static CraftItemFactory instance() {
        return instance;
    }
}
