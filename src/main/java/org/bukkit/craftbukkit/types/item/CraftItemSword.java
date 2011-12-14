package org.bukkit.craftbukkit.types.item;

import net.minecraft.server.ItemSword;

public class CraftItemSword extends org.bukkit.types.item.ItemSword {
    private final ItemSword item;

    public CraftItemSword(ItemSword item) {
        super(item.id);
        this.item = item;
    }

    @Override
    public String getName() {
        return item.k();
    }

    @Override
    public int getDamage() {
        return item.a;
    }

    @Override
    public void setDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be less than 0");
        }
        item.a = damage;
    }

    @Override
    public int getMaxUses() {
        return item.getMaxDurability();
    }

    @Override
    public void setMaxUses(int uses) {
        if (uses < 0) {
            throw new IllegalArgumentException("Max uses cannot be below zero");
        }
        item.f(uses);
    }
}
