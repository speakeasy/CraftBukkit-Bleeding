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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableMap;

@DelegateDeserialization(CraftItemFactory.SerializableMeta.class)
class CraftItemMeta implements ItemMeta {
    enum ItemMetaKeys {
        NAME("Name", "displayName"),
        LORE("Lore", "lore"),
        ENCHANTMENTS("ench", "enchants"),
        REPAIR("RepairCost", "repairCost");
        final String nbt;
        final String bukkit;

        ItemMetaKeys(String s1, String s2) {
            this.nbt = s1;
            this.bukkit = s2;
        }
    }
    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments; // TODO: enchantments
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

            if (display.hasKey(ItemMetaKeys.NAME.nbt)) {
                displayName = display.getString(ItemMetaKeys.NAME.nbt);
            }

            if (display.hasKey(ItemMetaKeys.LORE.nbt)) {
                NBTTagList list = display.getList(ItemMetaKeys.LORE.nbt);
                lore = new ArrayList<String>(list.size());

                for (int index = 0; index < list.size(); index++) {
                    String line = ((NBTTagString) list.get(index)).data;
                    lore.add(line);
                }
            }
        }

        if (tag.hasKey(ItemMetaKeys.ENCHANTMENTS.nbt)) {
            NBTTagList ench = tag.getList(ItemMetaKeys.ENCHANTMENTS.nbt);
            enchantments = new HashMap<Enchantment, Integer>(ench.size());

            for (int i = 0; i < ench.size(); i++) {
                short id = ((NBTTagCompound) ench.get(i)).getShort("id");
                short level = ((NBTTagCompound) ench.get(i)).getShort("lvl");

                enchantments.put(Enchantment.getById(id), (int) level);
            }
        }

        if (tag.hasKey(ItemMetaKeys.REPAIR.nbt)) {
            repairCost = tag.getInt(ItemMetaKeys.REPAIR.nbt);
        }
    }

    CraftItemMeta(Map<String, Object> map) {
        if (map.containsKey(ItemMetaKeys.NAME.bukkit)) {
            displayName = String.valueOf(map.get(ItemMetaKeys.NAME.bukkit));
        }

        if (map.containsKey(ItemMetaKeys.LORE.bukkit)) {
            lore = (List<String>) map.get(ItemMetaKeys.LORE.bukkit);
        }

        if (map.containsKey(ItemMetaKeys.ENCHANTMENTS.bukkit)) {
            Object raw = map.get(ItemMetaKeys.ENCHANTMENTS.bukkit);

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

        if (map.containsKey(ItemMetaKeys.REPAIR.bukkit)) {
            repairCost = (Integer) map.get(ItemMetaKeys.REPAIR.bukkit);
        }
    }

    void applyToItem(NBTTagCompound itemTag) {
        NBTTagCompound display = getDisplay(itemTag);

        if (hasDisplayName()) {
            display.setString(ItemMetaKeys.NAME.nbt, displayName);
        } else {
            display.remove(ItemMetaKeys.NAME.nbt);
        }

        if (hasLore()) {
            NBTTagList list = new NBTTagList(ItemMetaKeys.LORE.nbt);
            for (int i = 0; i < lore.size(); i++) {
                list.add(new NBTTagString(String.valueOf(i), lore.get(i)));
            }
            display.set(ItemMetaKeys.LORE.nbt, list);
        } else {
            display.remove(ItemMetaKeys.LORE.nbt);
        }

        if (hasEnchants()) {
            NBTTagList list = new NBTTagList(ItemMetaKeys.ENCHANTMENTS.nbt);

            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                NBTTagCompound subtag = new NBTTagCompound();

                subtag.setShort("id", (short) entry.getKey().getId());
                subtag.setShort("lvl", (short) (int) entry.getValue());

                list.add(subtag);
            }

            itemTag.set(ItemMetaKeys.ENCHANTMENTS.nbt, list);
        } else {
            itemTag.remove(ItemMetaKeys.ENCHANTMENTS.nbt);
        }

        if (hasRepairCost()) {
            itemTag.setInt(ItemMetaKeys.REPAIR.nbt, repairCost);
        } else {
            itemTag.remove(ItemMetaKeys.REPAIR.nbt);
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
        if (this == object) {
            return true;
        }
        if (!(object instanceof CraftItemMeta)) {
            return false;
        } else if (this.isEmpty()) {
            return ((CraftItemMeta) object).isEmpty();
        }

        CraftItemMeta objectMeta = (CraftItemMeta) object;

        // Do the display names equal?
        if ((this.hasDisplayName() && !objectMeta.hasDisplayName()) || (this.hasDisplayName() && !this.getDisplayName().equals(objectMeta.getDisplayName()))) {
            return false;
        }

        // Do the enchants equal?
        if ((this.hasEnchants() && !objectMeta.hasEnchants()) || (this.hasEnchants() && !this.getEnchants().equals(objectMeta.getEnchants()))) {
            return false;
        }

        // Does lore match?
        if ((this.hasLore() && !objectMeta.hasLore())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.displayName != null ? this.displayName.hashCode() : 0);
        hash = 61 * hash + (this.lore != null ? this.lore.hashCode() : 0);
        hash = 61 * hash + (this.enchantments != null ? this.enchantments.hashCode() : 0);
        // hash = 61 * hash + this.maxStackSize;
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
            builder.put(ItemMetaKeys.NAME.bukkit, displayName);
        }

        if (hasLore()) {
            builder.put(ItemMetaKeys.LORE.bukkit, lore);
        }

        if (hasEnchants()) {
            builder.put(ItemMetaKeys.ENCHANTMENTS.bukkit, enchantments);
        }

        if (hasRepairCost()) {
            builder.put(ItemMetaKeys.REPAIR.bukkit, repairCost);
        }

        return builder;
    }

    CraftItemFactory.SerializableMeta.Deserializers deserializer() {
        return CraftItemFactory.SerializableMeta.Deserializers.UNSPECIFIC;
    }
}
