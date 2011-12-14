package org.bukkit.craftbukkit.types.item;

import net.minecraft.server.ItemSword;

public class CraftItemSword extends CraftItemType implements org.bukkit.types.item.ItemSword {
    public CraftItemSword(ItemSword item) {
        super(item);
    }

    @Override
    public ItemSword getHandle() {
        return (ItemSword)super.getHandle();
    }

    @Override
    public int getDamage() {
        return getHandle().a;
    }

    @Override
    public void setDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be less than 0");
        }
        getHandle().a = damage;
    }
}
