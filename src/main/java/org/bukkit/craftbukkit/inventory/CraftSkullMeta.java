package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.NBTTagCompound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

class CraftSkullMeta extends CraftItemMeta implements SkullMeta {
    private String player;

    CraftSkullMeta() {}

    CraftSkullMeta(CraftItemStack itemstack) {
        super(itemstack);
        NBTTagCompound tag = itemstack.getHandle().getTag();
        if (tag != null) {
            if (tag.hasKey("SkullOwner")) {
                player = tag.getString("SkullOwner");
            }
        }
    }

    boolean isEmpty() {
        return player == null && super.isEmpty();
    }

    boolean applicableTo(ItemStack itemstack) {
        switch(itemstack.getType()) {
            case SKULL_ITEM: return true;
            default: return false;
        }
    }

    void applyToItem(CraftItemStack item) {
        super.applyToItem(item);

        NBTTagCompound tag = item.getHandle().getTag();
        if (player == null) {
            tag.remove("SkullOwner");
        } else {
            tag.setString("SkullOwner", player);
        }
    }

    @Override
    public CraftSkullMeta clone() {
        return (CraftSkullMeta) super.clone();
    }

    public String getOwner() {
        return player;
    }

    public boolean setOwner(String name) {
        if (name != null && name.length() > 16) {
            return false;
        }
        player = name;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 73 * hash + (player != null ? player.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CraftSkullMeta)) {
            return false;
        }
        CraftSkullMeta other = (CraftSkullMeta) object;

        if (!player.equals(other.player)) {
            return false;
        }

        return super.equals(object);
    }
}
