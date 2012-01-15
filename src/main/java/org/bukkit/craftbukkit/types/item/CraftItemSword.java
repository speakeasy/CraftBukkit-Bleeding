package org.bukkit.craftbukkit.types.item;

import net.minecraft.server.ItemSword;
import org.bukkit.types.item.Weapon;

public class CraftItemSword extends CraftItemType implements Weapon {
    public CraftItemSword(ItemSword item) {
        super(item);
    }

    @Override
    public ItemSword getItem() {
        return (ItemSword) super.getItem();
    }

    public int getDamage() {
        return getItem().a;
    }

    public void setDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be less than 0");
        }
        getItem().a = damage;
    }
}
