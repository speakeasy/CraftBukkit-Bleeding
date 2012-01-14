package org.bukkit.craftbukkit.types.item;

import net.minecraft.server.ItemSword;

public class CraftItemSword extends CraftItemType implements org.bukkit.types.item.ItemSword {
    public CraftItemSword(ItemSword item) {
        super(item);
    }

    @Override
    public ItemSword getItem() {
        return (ItemSword)super.getItem();
    }

    @Override
    public int getDamage() {
        return getItem().a;
    }

    @Override
    public void setDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be less than 0");
        }
        getItem().a = damage;
    }
}
