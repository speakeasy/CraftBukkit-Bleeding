package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagInt;
import org.bukkit.inventory.ItemStack;

class CraftLeatherArmorMeta extends CraftItemMeta {
    private static int defaultColor = 10511680;
    private int color;

    CraftLeatherArmorMeta() {}

    CraftLeatherArmorMeta(CraftItemStack itemstack) {
        super(itemstack);
        // TODO: Set data
    }

    // nbt: display.color

    // TODO: isEmpty()
    boolean isEmpty() {
        return super.isEmpty();
    }

    boolean applicableTo(ItemStack itemstack) {
        switch(itemstack.getType()) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS: return true;
            default: return false;
        }
    }

    void applyToItem(CraftItemStack item) {
        super.applyToItem(item);

        if (color > 0) { // && color < (the max color...)
            NBTTagCompound itemTag = item.getHandle().getTag();

            setDisplay(itemTag, new NBTTagInt("color", color));
        }
    }

    @Override
    public CraftLeatherArmorMeta clone() {
        return (CraftLeatherArmorMeta) super.clone();
    }

    public void getColor() {
    }

    public void setColor() {
    }
}
