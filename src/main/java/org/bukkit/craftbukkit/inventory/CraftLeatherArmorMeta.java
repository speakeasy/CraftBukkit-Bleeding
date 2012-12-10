package org.bukkit.craftbukkit.inventory;

import static org.bukkit.craftbukkit.inventory.CraftItemFactory.DEFAULT_LEATHER_COLOR;

import java.util.Map;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagInt;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftItemMeta.SerializableMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.google.common.collect.ImmutableMap.Builder;

@DelegateDeserialization(SerializableMeta.class)
class CraftLeatherArmorMeta extends CraftItemMeta implements LeatherArmorMeta {
    static final ItemMetaKey COLOR = new ItemMetaKey("color");

    private Color color = DEFAULT_LEATHER_COLOR;

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
        if (tag.hasKey(DISPLAY.NBT)) {
            NBTTagCompound display = tag.getCompound(DISPLAY.NBT);
            if (display.hasKey(COLOR.NBT)) {
                color = Color.fromRGB(display.getInt(COLOR.NBT));
            }
        }
    }

    CraftLeatherArmorMeta(Map<String, Object> map) {
        super(map);
        setColor((Color) map.get(COLOR.NBT));
    }

    void applyToItem(NBTTagCompound itemTag) {
        super.applyToItem(itemTag);

        if (hasColor()) {
            setDisplay(itemTag, COLOR.NBT, new NBTTagInt(COLOR.NBT, color.asRGB()));
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isLeatherArmorEmpty();
    }

    boolean isLeatherArmorEmpty() {
        return !(hasColor());
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color == null ? DEFAULT_LEATHER_COLOR : color;
    }

    public void dyeColor(Color... colors) {
        // TODO: Justification to not be in Color
        // TODO: wtf does this even do? Specifically; there are no javadocs
        Validate.noNullElements(colors, "Color cannot be null");

        Color currentColor = getColor();

        int totalRed = currentColor.getRed();
        int totalGreen = currentColor.getGreen();
        int totalBlue = currentColor.getBlue();
        int totalMax = Math.max(Math.max(currentColor.getRed(), currentColor.getGreen()), currentColor.getBlue());
        for (Color color : colors) {
            totalRed += color.getRed();
            totalGreen += color.getGreen();
            totalBlue += color.getBlue();
            totalMax += Math.max(Math.max(color.getRed(), color.getGreen()), color.getBlue());
        }

        int averageRed = totalRed / (colors.length + 1);
        int averageGreen = totalGreen / (colors.length + 1);
        int averageBlue = totalBlue / (colors.length + 1);
        int averageMax = totalMax / (colors.length + 1);

        int maximumOfAverages = Math.max(Math.max(averageRed, averageGreen), averageBlue);
        int gainFactor = averageMax / maximumOfAverages;

        setColor(Color.fromRGB((averageRed * gainFactor), (averageGreen * gainFactor), (averageBlue * gainFactor)));
    }

    boolean hasColor() {
        return !DEFAULT_LEATHER_COLOR.equals(color);
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasColor()) {
            builder.put(COLOR.BUKKIT, color);
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

            return color.equals(that.color);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftItemMeta meta) {
        return super.notUncommon(meta) && (meta instanceof CraftSkullMeta || isLeatherArmorEmpty());
    }

    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (hasColor()) {
            // TODO
        }
        return original != hash ? CraftSkullMeta.class.hashCode() ^ hash : hash;
    }
}
