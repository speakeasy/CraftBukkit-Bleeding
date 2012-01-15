package org.bukkit.craftbukkit.types.item;

import net.minecraft.server.ItemFood;
import net.minecraft.server.ItemSword;
import org.bukkit.types.item.Food;

public class CraftFood extends CraftItemType implements Food {
    public CraftFood(ItemFood item) {
        super(item);
    }

    @Override
    public ItemFood getItem() {
        return (ItemFood) super.getItem();
    }

    public int getNourishment() {
        return getItem().getNutrition();
    }

    public void setNourishment(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage cannot be less than 0");
        }
        getItem().b = amount;
    }
}
