package org.bukkit.craftbukkit.inventory;

import static org.bukkit.craftbukkit.inventory.CraftItemFactory.DEFAULT_LEATHER_COLOUR;

import java.util.Map;

import net.minecraft.server.NBTTagCompound;

import org.bukkit.Colour;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftItemMeta.SerializableMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.google.common.collect.ImmutableMap.Builder;
import net.minecraft.server.NBTTagInt;

@DelegateDeserialization(SerializableMeta.class)
class CraftLeatherArmorMeta extends CraftItemMeta implements LeatherArmorMeta {
    static final ItemMetaKey COLOUR = new ItemMetaKey("color", "colour");
    private Colour colour = DEFAULT_LEATHER_COLOUR;


    CraftLeatherArmorMeta(CraftItemMeta meta) {
        super(meta);
        if (!(meta instanceof CraftLeatherArmorMeta)) {
            return;
        }

        CraftLeatherArmorMeta armorMeta = (CraftLeatherArmorMeta) meta;
        this.colour = armorMeta.colour;
    }

    CraftLeatherArmorMeta(NBTTagCompound tag) {
        super(tag);
        if (tag.hasKey(DISPLAY.NBT)) {
            NBTTagCompound display = tag.getCompound(DISPLAY.NBT);
            if (display.hasKey(COLOUR.NBT)) {
                colour = Colour.fromRGB(display.getInt(COLOUR.NBT));
            }
        }
    }

    CraftLeatherArmorMeta(Map<String, Object> map) {
        super(map);
        setColour((Colour) map.get(COLOUR.NBT));
    }

    void applyToItem(NBTTagCompound itemTag) {
        super.applyToItem(itemTag);

        if (hasColour()) {
            setDisplay(itemTag, COLOUR.NBT, new NBTTagInt(COLOUR.NBT, colour.asRGB()));
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && !hasColour();
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

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour == null ? DEFAULT_LEATHER_COLOUR : colour;
    }

    boolean hasColour() {
        return !DEFAULT_LEATHER_COLOUR.equals(colour);
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasColour()) {
            builder.put(COLOUR.BUKKIT, colour);
        }

        return builder;
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

            return colour.equals(that.colour);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftItemMeta meta) {
        return super.notUncommon(meta) && (meta instanceof CraftSkullMeta || !this.hasColour());
    }
}
