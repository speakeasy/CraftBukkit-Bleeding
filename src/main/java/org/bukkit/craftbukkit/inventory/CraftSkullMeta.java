package org.bukkit.craftbukkit.inventory;

import java.util.Map;

import net.minecraft.server.NBTTagCompound;

import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.collect.ImmutableMap.Builder;

class CraftSkullMeta extends CraftItemMeta implements SkullMeta {
    private String player;

    CraftSkullMeta(CraftItemMeta meta) {
        super(meta);
        if (!(meta instanceof CraftSkullMeta)) {
            return;
        }
        CraftSkullMeta skullMeta = (CraftSkullMeta) meta;
        this.player = skullMeta.player;
    }

    CraftSkullMeta(NBTTagCompound tag) {
        super(tag);

        if (tag.hasKey("SkullOwner")) {
            player = tag.getString("SkullOwner");
        }
    }

    CraftSkullMeta(Map<String, Object> map) {
        super(map);
        // TODO Auto-generated constructor stub
    }

    void applyToItem(NBTTagCompound tag) {
        if (hasOwner()) {
            tag.setString("SkullOwner", player);
        } else {
            tag.remove("SkullOwner");
        }
    }

    boolean isEmpty() {
        return !hasOwner() && super.isEmpty();
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

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        // TODO Auto-generated method stub
        return super.serialize(builder);
    }

    @Override
    CraftItemFactory.SerializableMeta.Deserializers deserializer() {
        return CraftItemFactory.SerializableMeta.Deserializers.SKULL;
    }
}
