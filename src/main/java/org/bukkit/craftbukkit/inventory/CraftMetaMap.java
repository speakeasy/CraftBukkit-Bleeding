package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.server.NBTTagCompound;
import org.bukkit.Material;

class CraftMetaMap extends CraftMetaItem {
    static final ItemMetaKey MAP_SCALING = new ItemMetaKey("map_is_scaling", "scaling");

    private boolean scaling;

    CraftMetaMap(CraftMetaItem meta) {
        super(meta);

        if (!(meta instanceof CraftMetaMap)) {
            return;
        }

        CraftMetaMap map = (CraftMetaMap) meta;
        this.scaling = map.scaling;
    }

    CraftMetaMap(NBTTagCompound tag) {
        super(tag);

        if (tag.hasKey(MAP_SCALING.NBT)) {
            this.scaling = tag.getBoolean(MAP_SCALING.NBT);
        }
    }

    CraftMetaMap(Map<String, Object> map) {
        super(map);

        this.scaling = SerializableMeta.getBoolean(map, MAP_SCALING.BUKKIT);
    }

    @Override
    void applyToItem(NBTTagCompound tag) {
        tag.setBoolean(MAP_SCALING.NBT, scaling);
    }

    @Override
    boolean applicableTo(Material type) {
        switch (type) {
            case MAP:
                return true;
            default:
                return false;
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isMapEmpty();
    }

    boolean isMapEmpty() {
        return false; // A boolean can never be empty
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        return true; // TODO
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaMap); // TODO
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        // TODO
        return original != hash ? CraftMetaBook.class.hashCode() ^ hash : hash;
    }

    public CraftMetaMap clone() {
        return (CraftMetaMap) super.clone();
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);

        builder.put(MAP_SCALING.BUKKIT, scaling);

        return builder;
    }

    @Override
    SerializableMeta.Deserializers deserializer() {
        return SerializableMeta.Deserializers.MAP;
    }
}
