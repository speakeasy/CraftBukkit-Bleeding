package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/*
 * Children must include the following:
 * - Constructor(CraftItemMeta meta)
 * - Constructor(NBTTagCompound tag)
 * - Constructor(Map<String, Object> map)
 * - void applyToItem(NBTTagCompound tag)
 * - boolean applicableTo(Material type)
 * - boolean hasUncommon()
 * - boolean equalsCommon(CraftItemMeta meta
 * - boolean notUncommon(CraftItemMeta meta)
 * - int applyHash(final int original)
 * - public Class clone()
 * - Builder<String, Object> serialize(Builder<String, Object> builder)
 * - SerializableMeta.Deserializers deserializer()
 *
 * And of course, their own api
 */
@DelegateDeserialization(CraftItemMeta.SerializableMeta.class)
class CraftItemMeta implements ItemMeta {
    static class ItemMetaKey {
        final String BUKKIT;
        final String NBT;

        ItemMetaKey(final String both) {
            this(both, both);
        }

        ItemMetaKey(final String nbt, final String bukkit) {
            this.NBT = nbt;
            this.BUKKIT = bukkit;
        }
    }

    @SerializableAs("ItemMeta")
    public static class SerializableMeta implements ConfigurationSerializable {
        static final String TYPE_FIELD = "meta-type";

        enum Deserializers {
            BOOK {
                @Override
                CraftBookMeta deserialize(Map<String, Object> map) {
                    return new CraftBookMeta(map);
                }
            },
            SKULL {
                @Override
                CraftSkullMeta deserialize(Map<String, Object> map) {
                    return new CraftSkullMeta(map);
                }
            },
            LEATHER_ARMOR {
                @Override
                CraftLeatherArmorMeta deserialize(Map<String, Object> map) {
                    return new CraftLeatherArmorMeta(map);
                }
            },
            MAP {
                @Override
                ItemMeta deserialize(Map<String, Object> map) {
                    // TODO
                    throw new UnsupportedOperationException(this.name());
                }
            },
            POTION {
                @Override
                ItemMeta deserialize(Map<String, Object> map) {
                    return new CraftPotionMeta(map);
                }
            },
            UNSPECIFIC {
                @Override
                CraftItemMeta deserialize(Map<String, Object> map) {
                    return new CraftItemMeta(map);
                }
            };

            abstract ItemMeta deserialize(Map<String, Object> map);
        }

        private SerializableMeta() {
        }

        public static ItemMeta deserialize(Map<String, Object> map) {
            Validate.notNull(map, "Cannot deserialize null map");

            String type = getString(map, TYPE_FIELD, false);
            Deserializers deserializer = Deserializers.valueOf(type);

            if (deserializer == null) {
                throw new IllegalArgumentException(type + " is not a valid " + TYPE_FIELD);
            }

            return deserializer.deserialize(map);
        }

        public Map<String, Object> serialize() {
            throw new AssertionError();
        }

        static String getString(Map<String, Object> map, String field, boolean nullable) {
            return getObject(String.class, map, field, nullable);
        }

        static <T> T getObject(Class<T> clazz, Map<String, Object> map, String field, boolean nullable) {
            final Object object = map.get(field);

            if (clazz.isInstance(object)) {
                return clazz.cast(object);
            }
            if (object == null) {
                if (!nullable) {
                    throw new IllegalArgumentException(field + " cannot be null");
                }
                return null;
            }
            throw new IllegalArgumentException(field + '(' + object + ") is not a valid " + clazz);
        }
    }

    static final ItemMetaKey NAME = new ItemMetaKey("Name", "display-name");
    static final ItemMetaKey DISPLAY = new ItemMetaKey("display");
    static final ItemMetaKey LORE = new ItemMetaKey("Lore", "lore");
    static final ItemMetaKey ENCHANTMENTS = new ItemMetaKey("ench", "enchants");
    static final ItemMetaKey ENCHANTMENTS_ID = new ItemMetaKey("id");
    static final ItemMetaKey ENCHANTMENTS_LVL = new ItemMetaKey("lvl");
    static final ItemMetaKey REPAIR = new ItemMetaKey("RepairCost", "repair-cost");

    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;
    private int repairCost;

    CraftItemMeta(CraftItemMeta meta) {
        if (meta == null) {
            return;
        }

        this.displayName = meta.displayName;

        if (meta.hasLore()) {
            this.lore = new ArrayList<String>(meta.lore);
        }

        if (meta.hasEnchants()) {
            this.enchantments = new HashMap<Enchantment, Integer>(meta.enchantments);
        }

        this.repairCost = meta.repairCost;
    }

    CraftItemMeta(NBTTagCompound tag) {
        if (tag.hasKey(DISPLAY.NBT)) {
            NBTTagCompound display = tag.getCompound(DISPLAY.NBT);

            if (display.hasKey(NAME.NBT)) {
                displayName = display.getString(NAME.NBT);
            }

            if (display.hasKey(LORE.NBT)) {
                NBTTagList list = display.getList(LORE.NBT);
                lore = new ArrayList<String>(list.size());

                for (int index = 0; index < list.size(); index++) {
                    String line = ((NBTTagString) list.get(index)).data;
                    lore.add(line);
                }
            }
        }

        if (tag.hasKey(ENCHANTMENTS.NBT)) {
            NBTTagList ench = tag.getList(ENCHANTMENTS.NBT);
            enchantments = new HashMap<Enchantment, Integer>(ench.size());

            for (int i = 0; i < ench.size(); i++) {
                short id = ((NBTTagCompound) ench.get(i)).getShort(ENCHANTMENTS_ID.NBT);
                short level = ((NBTTagCompound) ench.get(i)).getShort(ENCHANTMENTS_LVL.NBT);

                addEnchant(Enchantment.getById(id), (int) level);
            }
        }

        if (tag.hasKey(REPAIR.NBT)) {
            repairCost = tag.getInt(REPAIR.NBT);
        }
    }

    CraftItemMeta(Map<String, Object> map) {
        setDisplayName(SerializableMeta.getString(map, NAME.BUKKIT, true));

        if (map.containsKey(LORE.BUKKIT)) {
            lore = (List<String>) map.get(LORE.BUKKIT);
        }

        Map<?, ?> ench = SerializableMeta.getObject(Map.class, map, ENCHANTMENTS.BUKKIT, true);
        if (ench != null) {
            enchantments = new HashMap<Enchantment, Integer>(ench.size());
            for (Map.Entry<?, ?> entry : ench.entrySet()) {
                Enchantment enchantment = Enchantment.getByName(entry.getKey().toString());

                if ((enchantment != null) && (entry.getValue() instanceof Integer)) {
                    addEnchant(enchantment, (Integer) entry.getValue());
                }
            }
        }

        if (map.containsKey(REPAIR.BUKKIT)) {
            repairCost = (Integer) map.get(REPAIR.BUKKIT);
        }
    }

    void applyToItem(NBTTagCompound itemTag) {
        if (hasDisplayName()) {
            setDisplay(itemTag, NAME.NBT, new NBTTagString(NAME.NBT, displayName));
        } else {
            setDisplay(itemTag, NAME.NBT, null);
        }

        if (hasLore()) {
            NBTTagList list = new NBTTagList(LORE.NBT);
            for (int i = 0; i < lore.size(); i++) {
                list.add(new NBTTagString(String.valueOf(i), lore.get(i)));
            }
            setDisplay(itemTag, LORE.NBT, list);
        } else {
            setDisplay(itemTag, LORE.NBT, null);
        }

        if (hasEnchants()) {
            NBTTagList list = new NBTTagList(ENCHANTMENTS.NBT);

            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                NBTTagCompound subtag = new NBTTagCompound();

                subtag.setShort(ENCHANTMENTS_ID.NBT, (short) entry.getKey().getId());
                subtag.setShort(ENCHANTMENTS_LVL.NBT, (short) (int) entry.getValue());

                list.add(subtag);
            }

            itemTag.set(ENCHANTMENTS.NBT, list);
        } else {
            itemTag.o(ENCHANTMENTS.NBT);
        }

        if (hasRepairCost()) {
            itemTag.setInt(REPAIR.NBT, repairCost);
        } else {
            itemTag.o(REPAIR.NBT);
        }
    }

    void setDisplay(NBTTagCompound tag, String key, NBTBase value) {
        NBTTagCompound display = tag.getCompound(DISPLAY.NBT);

        if (value == null) {
            display.o(key);
        } else {
            display.set(key, value);
        }

        if (!tag.hasKey(DISPLAY.NBT) && !display.d()) {
            tag.setCompound(DISPLAY.NBT, display);
        } else if (tag.hasKey(DISPLAY.NBT) && display.d()) {
            tag.o(DISPLAY.NBT);
        }
    }

    boolean applicableTo(Material type) {
        return type != Material.AIR;
    }

    final boolean isEmpty() {
        return !hasCommon() && !hasUncommon();
    }

    final boolean hasCommon() {
        return hasDisplayName() || hasEnchants() || hasLore();
    }

    boolean hasUncommon() {
        return false;
    }

    public String getDisplayName() {
        return displayName;
    }

    public final void setDisplayName(String name) {
        this.displayName = name;
    }

    public boolean hasDisplayName() {
        return !Strings.isNullOrEmpty(displayName);
    }

    public boolean hasLore() {
        return this.lore != null && !this.lore.isEmpty();
    }

    public boolean hasRepairCost() {
        return repairCost > 0;
    }

    public boolean hasEnchant(Enchantment ench) {
        return hasEnchants() ? enchantments.containsKey(ench) : false;
    }

    public int getEnchantLevel(Enchantment ench) {
        Integer level = hasEnchants() ? enchantments.get(ench) : null;
        if (level == null) {
            return 0;
        }
        return level;
    }

    public Map<Enchantment, Integer> getEnchants() {
        return hasEnchants() ? ImmutableMap.copyOf(enchantments) : ImmutableMap.<Enchantment, Integer>of();
    }

    public boolean addEnchant(Enchantment ench, int level, boolean ignoreRestrictions) {
        if (enchantments == null) {
            enchantments = new HashMap<Enchantment, Integer>(4);
        }

        // TODO  && ench.getItemTarget().includes( ... ? ... )
        if (ignoreRestrictions || level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
            Integer old = enchantments.put(ench, level);
            return old == null || old != level;
        }
        return false;
    }

    private void addEnchant(Enchantment ench, int level) {
        addEnchant(ench, level, true);
    }

    public boolean removeEnchant(Enchantment ench) {
        return hasEnchants() ? enchantments.remove(ench) != null : false;
    }

    public boolean hasEnchants() {
        return !(enchantments == null || enchantments.isEmpty());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (!(object instanceof CraftItemMeta)) {
            return false;
        }
        return CraftItemFactory.instance().equals(this, (ItemMeta) object);
    }

    /**
     * This method is almost as weird as notUncommon.
     * Only return false if your common internals are unequal.
     * Checking your own internals is redundant if you are not common, as notUncommon is meant for checking those 'not common' variables.
     */
    boolean equalsCommon(CraftItemMeta that) {
        return ((this.hasDisplayName() ? that.hasDisplayName() && this.displayName.equals(that.displayName) : !that.hasDisplayName()))
                && (this.hasEnchants() ? that.hasEnchants() && this.enchantments.equals(that.enchantments) : !that.hasEnchants())
                && (this.hasLore() ? that.hasLore() && this.lore.equals(that.lore) : !that.hasLore())
                && (this.hasRepairCost() ? that.hasRepairCost() && this.repairCost == that.repairCost : !that.hasRepairCost());
    }

    /**
     * This method is a bit weird...
     * Return true if you are a common class OR your uncommon parts are empty.
     * Empty uncommon parts implies the NBT data would be equivalent if both were applied to an item
     */
    boolean notUncommon(CraftItemMeta meta) {
        return !hasUncommon();
    }

    @Override
    public final int hashCode() {
        int hash = 3;
        hash = 61 * hash + (hasDisplayName() ? this.displayName.hashCode() : 0);
        hash = 61 * hash + (hasLore() ? this.lore.hashCode() : 0);
        hash = 61 * hash + (hasEnchants() ? this.enchantments.hashCode() : 0);
        hash = 61 * hash + (hasRepairCost() ? this.repairCost : 0);
        return applyHash(hash);
    }

    int applyHash(final int original) {
        return original;
    }

    public CraftItemMeta clone() {
        try {
            return (CraftItemMeta) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public Map<String, Object> serialize() {
        ImmutableMap.Builder<String, Object> map = ImmutableMap.builder();
        map.put(SerializableMeta.TYPE_FIELD, deserializer().name());
        serialize(map);
        return map.build();
    }

    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        if (hasDisplayName()) {
            builder.put(NAME.BUKKIT, displayName);
        }

        if (hasLore()) {
            builder.put(LORE.BUKKIT, lore);
        }

        if (hasEnchants()) {
            builder.put(ENCHANTMENTS.BUKKIT, enchantments);
        }

        if (hasRepairCost()) {
            builder.put(REPAIR.BUKKIT, repairCost);
        }

        return builder;
    }

    SerializableMeta.Deserializers deserializer() {
        return SerializableMeta.Deserializers.UNSPECIFIC;
    }
}
