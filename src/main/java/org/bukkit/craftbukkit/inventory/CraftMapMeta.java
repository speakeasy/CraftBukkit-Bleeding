package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap.Builder;
import net.minecraft.server.NBTTagCompound;
import org.bukkit.Material;

import java.util.Map;

class CraftMapMeta extends CraftItemMeta {
    private boolean scaling;

    CraftMapMeta(CraftItemMeta meta) {
        super(meta);
        if (!(meta instanceof CraftMapMeta)) {
            return;
        }
        CraftMapMeta mapMeta = (CraftMapMeta) meta;
        this.scaling = mapMeta.scaling;
    }

    CraftMapMeta(NBTTagCompound tag) {
        super(tag);
        if (tag.hasKey("map_is_scaling")) {
            scaling = tag.getBoolean("map_is_scaling");
        }
    }

    CraftMapMeta(Map<String, Object> map) {
        super(map);
        // TODO Auto-generated constructor stub
    }

    void applyToItem(NBTTagCompound itemTag) {
        super.applyToItem(itemTag);

        if (scaling) {
            itemTag.setBoolean("map_is_scaling", true);
        } else {
            itemTag.remove("map_is_scaling");
        }
    }

    boolean isEmpty() {
        return !scaling && super.isEmpty();
    }

    boolean applicableTo(Material type) {
        switch(type) {
            case MAP:
                return true;
            default:
                return false;
        }
    }

    @Override
    public CraftMapMeta clone() {
        return (CraftMapMeta) super.clone();
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        // TODO Auto-generated method stub
        return super.serialize(builder);
    }

    @Override
    CraftItemFactory.SerializableMeta.Deserializers deserializer() {
        return CraftItemFactory.SerializableMeta.Deserializers.LEATHER_ARMOR;
    }
}
