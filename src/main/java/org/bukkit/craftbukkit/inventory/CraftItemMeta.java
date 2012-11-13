package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftItemFactory.SerializableMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableMap;

@DelegateDeserialization(CraftItemFactory.SerializableMeta.class)
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

    static final ItemMetaKey NAME = new ItemMetaKey("Name", "display-name");
    static final ItemMetaKey LORE = new ItemMetaKey("Lore", "lore");
    static final ItemMetaKey ENCHANTMENTS = new ItemMetaKey("ench", "enchants");
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

        if (meta.lore != null) {
            this.lore = new ArrayList<String>(meta.lore);
        }

        if (meta.enchantments != null) {
            this.enchantments = new HashMap<Enchantment, Integer>(meta.enchantments);
        }

        this.repairCost = meta.repairCost;
    }

    CraftItemMeta(NBTTagCompound tag) {
        if (tag.hasKey("display")) {
            NBTTagCompound display = tag.getCompound("display");

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
                short id = ((NBTTagCompound) ench.get(i)).getShort("id");
                short level = ((NBTTagCompound) ench.get(i)).getShort("lvl");

                enchantments.put(Enchantment.getById(id), (int) level);
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

        if (map.containsKey(ENCHANTMENTS.BUKKIT)) {
            Object raw = map.get(ENCHANTMENTS.BUKKIT);

            if (raw instanceof Map) {
                Map<?, ?> ench = (Map<?, ?>) raw;

                for (Map.Entry<?, ?> entry : ench.entrySet()) {
                    Enchantment enchantment = Enchantment.getByName(entry.getKey().toString());

                    if ((enchantment != null) && (entry.getValue() instanceof Integer)) {
                        addEnchant(enchantment, (Integer) entry.getValue());
                    }
                }
            }
        }

        if (map.containsKey(REPAIR.BUKKIT)) {
            repairCost = (Integer) map.get(REPAIR.BUKKIT);
        }
    }

    void applyToItem(NBTTagCompound itemTag) {
        NBTTagCompound display = getDisplay(itemTag);

        if (hasDisplayName()) {
            display.setString(NAME.NBT, displayName);
        } else {
            display.remove(NAME.NBT);
        }

        if (hasLore()) {
            NBTTagList list = new NBTTagList(LORE.NBT);
            for (int i = 0; i < lore.size(); i++) {
                list.add(new NBTTagString(String.valueOf(i), lore.get(i)));
            }
            display.set(LORE.NBT, list);
        } else {
            display.remove(LORE.NBT);
        }

        if (hasEnchants()) {
            NBTTagList list = new NBTTagList(ENCHANTMENTS.NBT);

            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                NBTTagCompound subtag = new NBTTagCompound();

                subtag.setShort("id", (short) entry.getKey().getId());
                subtag.setShort("lvl", (short) (int) entry.getValue());

                list.add(subtag);
            }

            itemTag.set(ENCHANTMENTS.NBT, list);
        } else {
            itemTag.remove(ENCHANTMENTS.NBT);
        }

        if (hasRepairCost()) {
            itemTag.setInt(REPAIR.NBT, repairCost);
        } else {
            itemTag.remove(REPAIR.NBT);
        }
    }

    NBTTagCompound getDisplay(NBTTagCompound tag) {
        NBTTagCompound display = tag.getCompound("display");

        if (!tag.hasKey("display")) {
            tag.setCompound("display", tag);
        }

        return display;
    }

    boolean applicableTo(Material type) {
        return type != Material.AIR;
    }

    boolean isEmpty() {
        return !(hasDisplayName() || hasEnchants() || hasLore());
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public boolean hasDisplayName() {
        return displayName != null;
    }

    public boolean hasLore() {
        return this.lore != null && !this.lore.isEmpty();
    }

    public boolean hasRepairCost() {
        return repairCost > 0;
    }

    public boolean hasEnchant(Enchantment ench) {
        return enchantments.containsKey(ench);
    }

    public int getEnchantLevel(Enchantment ench) {
        Integer level = enchantments.get(ench);
        if (level == null) {
            return 0;
        }
        return level;
    }

    public Map<Enchantment, Integer> getEnchants() {
        return ImmutableMap.copyOf(enchantments);
    }

    public boolean addEnchant(Enchantment ench, int level, boolean ignoreRestrictions) {
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
        return enchantments.remove(ench) != null;
    }

    public boolean hasEnchants() {
        return !enchantments.isEmpty();
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
        return (this.displayName == that.displayName || (this.displayName != null && this.displayName.equals(that.displayName)))
                && (this.hasEnchants() ? this.enchantments.equals(that.enchantments) : !that.hasEnchants())
                && (this.hasLore() ? this.lore.equals(that.lore) : !that.hasLore())
                && this.repairCost == that.repairCost;
    }

    /**
     * This method is a bit weird...
     * Return true if you are a common class OR your uncommon parts are empty.
     * Empty uncommon parts implies the NBT data would be equivalent if both were applied to an item
     */
    boolean notUncommon(CraftItemMeta meta) {
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.displayName != null ? this.displayName.hashCode() : 0);
        hash = 61 * hash + (this.lore != null ? this.lore.hashCode() : 0);
        hash = 61 * hash + (this.enchantments != null ? this.enchantments.hashCode() : 0);
        hash = 61 * hash + this.repairCost;
        return hash;
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
        map.put(CraftItemFactory.SerializableMeta.TYPE_FIELD, deserializer().name());
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

    CraftItemFactory.SerializableMeta.Deserializers deserializer() {
        return CraftItemFactory.SerializableMeta.Deserializers.UNSPECIFIC;
    }
}
