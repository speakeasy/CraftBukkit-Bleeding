package org.bukkit.craftbukkit.inventory;

import java.util.Map;

import net.minecraft.server.NBTTagCompound;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap.Builder;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaSkull extends CraftMetaItem implements SkullMeta {
    static final ItemMetaKey SKULL_OWNER = new ItemMetaKey("SkullOwner", "skull-owner");
    static final ItemMetaKey SKULL_TYPE = new ItemMetaKey("Damage", "skull-type");
    static final int MAX_OWNER_LENGTH = 16;

    private String player;
    private SkullType skullType;

    CraftMetaSkull(CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaSkull)) {
            return;
        }
        CraftMetaSkull skullMeta = (CraftMetaSkull) meta;
        this.player = skullMeta.player;
        this.skullType = skullMeta.skullType;
    }

    CraftMetaSkull(NBTTagCompound tag) {
        super(tag);

        if (tag.hasKey(SKULL_OWNER.NBT)) {
            player = tag.getString(SKULL_OWNER.NBT);
        }

        if (tag.hasKey(SKULL_TYPE.NBT)) {
            switch (tag.getShort(SKULL_TYPE.NBT)) {
                case 0:
                default:
                    skullType = SkullType.SKELETON;
                    break;

                case 1:
                    skullType = SkullType.WITHER;
                    break;

                case 2:
                    skullType = SkullType.ZOMBIE;
                    break;

                case 3:
                    skullType = SkullType.PLAYER;
                    break;

                case 4:
                    skullType = SkullType.CREEPER;
                    break;
            }
        }
    }

    CraftMetaSkull(Map<String, Object> map) {
        super(map);
        setOwner(SerializableMeta.getString(map, SKULL_OWNER.BUKKIT, true));
        setType(SerializableMeta.getObject(SkullType.class, map, SKULL_TYPE.BUKKIT, false));
    }

    @Override
    void applyToItem(NBTTagCompound tag) {
        super.applyToItem(tag);

        if (hasOwner()) {
            tag.setString(SKULL_OWNER.NBT, player);
        }

        tag.setShort(SKULL_TYPE.NBT, getData(skullType));
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isSkullEmpty();
    }

    boolean isSkullEmpty() {
        return !(hasOwner());
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
    public CraftMetaSkull clone() {
        return (CraftMetaSkull) super.clone();
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
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (hasOwner()) {
            hash = 61 * hash + player.hashCode();
        }
        return original != hash ? CraftMetaSkull.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaSkull) {
            CraftMetaSkull that = (CraftMetaSkull) meta;

            return (this.hasOwner() ? that.hasOwner() && this.player.equals(that.player) : !that.hasOwner());
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaSkull || isSkullEmpty());
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);
        if (hasOwner()) {
            return builder.put(SKULL_OWNER.BUKKIT, this.player);
        }
        return builder;
    }

    @Override
    public SkullType getType() {
        return skullType;
    }

    @Override
    public boolean setType(SkullType type) {
        skullType = type;
        return true;
    }

    private short getData(SkullType type) {
        switch (type) {
            case SKELETON:
            default:
                return 0;

            case WITHER:
                return 1;

            case ZOMBIE:
                return 2;

            case PLAYER:
                return 3;

            case CREEPER:
                return 4;
        }
    }
}
