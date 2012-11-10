package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;

class CraftSkullMeta extends CraftItemMeta implements SkullMeta {
    private String player;

    CraftSkullMeta() {}

    CraftSkullMeta(NBTTagCompound tag) {
        super(tag);
        if (tag.hasKey("SkullOwner")) {
            player = tag.getString("SkullOwner");
        }
    }

    boolean isEmpty() {
        return !hasOwner() && super.isEmpty();
    }

    void applyToItem(NBTTagCompound tag) {
        if (player == null) {
            tag.remove("SkullOwner");
        } else {
            tag.setString("SkullOwner", player);
        }
    }

    boolean applicableTo(Material type) {
        switch(type) {
            case SKULL_ITEM: return true;
            default: return false;
        }
    }

    @Override
    public CraftSkullMeta clone() {
        return (CraftSkullMeta) super.clone();
    }

    public boolean hasOwner() {
        return player != null;
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

        if (this.player == null ? other.player != null : !this.player.equals(other.player)) {
            return false;
        }

        return super.equals(object);
    }
}
