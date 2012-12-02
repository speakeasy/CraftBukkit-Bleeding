package org.bukkit.craftbukkit.inventory;

import java.util.Map;

import net.minecraft.server.NBTTagCompound;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftItemMeta.SerializableMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.google.common.collect.ImmutableMap.Builder;

@DelegateDeserialization(SerializableMeta.class)
final class CraftLeatherArmorMeta extends CraftItemMeta implements LeatherArmorMeta {
    // TODO: ItemMetaKey color
    private static final int defaultColor = 10511680;
    private static int maxRGB = 16581375;
    private int color = -1;

    CraftLeatherArmorMeta(CraftItemMeta meta) {
        super(meta);
        if (!(meta instanceof CraftLeatherArmorMeta)) {
            return;
        }
        CraftLeatherArmorMeta armorMeta = (CraftLeatherArmorMeta) meta;
        this.color = armorMeta.color;
    }

    CraftLeatherArmorMeta(NBTTagCompound tag) {
        super(tag);
        if (tag.hasKey("display")) {
            NBTTagCompound display = tag.getCompound("display");
            if (display.hasKey("color")) {
                color = display.getInt("color");
            }
        }
    }

    CraftLeatherArmorMeta(Map<String, Object> map) {
        super(map);
        // TODO Auto-generated constructor stub
    }

    void applyToItem(NBTTagCompound itemTag) {
        super.applyToItem(itemTag);

        if (hasColor()) {
            getDisplay(itemTag).setInt("color", color);
        } else {
            // remove color
        }
    }

    @Override
    boolean hasUncommon() {
        return hasColor();
    }

    boolean applicableTo(Material type) {
        switch(type) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return true;
            default:
                return false;
        }
    }

    @Override
    public CraftLeatherArmorMeta clone() {
        return (CraftLeatherArmorMeta) super.clone();
    }

    // TODO: How to do colors...
    public void getColor() {
    }

    public void setColor() {
    }

    public boolean hasColor() {
        return color > -1; // || color != defaultColor;
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        // TODO Auto-generated method stub
        return super.serialize(builder);
    }

    @Override
    SerializableMeta.Deserializers deserializer() {
        return SerializableMeta.Deserializers.LEATHER_ARMOR;
    }

    @Override
    boolean equalsCommon(CraftItemMeta meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftLeatherArmorMeta) {
            CraftLeatherArmorMeta that = (CraftLeatherArmorMeta) meta;

            return (hasColor() ? that.hasColor() && this.color == that.color : !that.hasColor());
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftItemMeta meta) {
        return meta instanceof CraftPotionMeta || super.notUncommon(meta);
    }
}
