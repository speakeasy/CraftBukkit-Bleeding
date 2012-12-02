package org.bukkit.craftbukkit.inventory;

import java.util.Map;

import net.minecraft.server.NBTTagCompound;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftItemMeta.SerializableMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap.Builder;

@DelegateDeserialization(SerializableMeta.class)
class CraftSkullMeta extends CraftItemMeta implements SkullMeta {
    static final ItemMetaKey SKULL_OWNER = new ItemMetaKey("SkullOwner", "skull-owner");
    static final int MAX_OWNER_LENGTH = 16;

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

        if (tag.hasKey(SKULL_OWNER.NBT)) {
            player = tag.getString(SKULL_OWNER.NBT);
        }
    }

    CraftSkullMeta(Map<String, Object> map) {
        super(map);
        setOwner(SerializableMeta.getString(map, SKULL_OWNER.BUKKIT, true));
    }

    @Override
    void applyToItem(NBTTagCompound tag) {
        super.applyToItem(tag);
        if (hasOwner()) {
            tag.setString(SKULL_OWNER.NBT, player);
        } else {
            tag.o(SKULL_OWNER.NBT);
        }
    }

    @Override
    boolean hasUncommon() {
        return hasOwner();
    }

    @Override
    boolean applicableTo(Material type) {
        switch(type) {
            case SKULL_ITEM:
                return true;
            default:
                return false;
        }
    }

    @Override
    public CraftSkullMeta clone() {
        return (CraftSkullMeta) super.clone();
    }

    public boolean hasOwner() {
        return !Strings.isNullOrEmpty(player);
    }

    public String getOwner() {
        return player;
    }

    public boolean setOwner(String name) {
        if (name != null && name.length() > MAX_OWNER_LENGTH) {
            return false;
        }
        player = name;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (player != null) {
            hash = 61 * hash + (player.hashCode() ^ CraftSkullMeta.class.hashCode());
        }
        return hash;
    }

    @Override
    boolean equalsCommon(CraftItemMeta meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftSkullMeta) {
            CraftSkullMeta that = (CraftSkullMeta) meta;
            return this.player == that.player || (this.player != null && this.player.equals(that.player));
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftItemMeta meta) {
        return meta instanceof CraftSkullMeta || super.notUncommon(meta);
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.player != null) {
            return builder.put(SKULL_OWNER.BUKKIT, this.player);
        }
        return builder;
    }

    @Override
    SerializableMeta.Deserializers deserializer() {
        return SerializableMeta.Deserializers.SKULL;
    }
}
