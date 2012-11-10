package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagInt;
import org.bukkit.Material;

class CraftLeatherArmorMeta extends CraftItemMeta {
    private static final int defaultColor = 10511680;
    private int color;

    CraftLeatherArmorMeta() {}

    CraftLeatherArmorMeta(NBTTagCompound tag) {
        super(tag);
        if (tag.hasKey("display")) {
            NBTTagCompound display = tag.getCompound("display");
            if (display.hasKey("color")) {
                color = display.getInt("color");
            }
        }
    }

    void applyToItem(CraftItemStack item) {
        super.applyToItem(item);

        // TODO when is color empty?
        if (color > 0) {
            NBTTagCompound itemTag = item.getHandle().getTag();

            setDisplay(itemTag, new NBTTagInt("color", color));
        }
    }

    // TODO
    boolean isEmpty() {
        return super.isEmpty();
    }

    boolean applicableTo(Material type) {
        switch(type) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS: return true;
            default: return false;
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
