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
            if (display.hasKey("Name")) {
                displayName = display.getString("Name");
            }

            if (display.hasKey("Lore")) {
                NBTTagList list = display.getList("Lore");
                lore = new ArrayList<String>(list.size());

                for (int index = 0; index < list.size(); index++) {
                    String line = ((NBTTagString) list.get(index)).data;
                    lore.add(line);
                }
            }
        }

        if (tag.hasKey("ench")) {
            NBTTagList enchantments = tag.getList("ench");

            int length = enchantments.size();
            for (int i = 0; i < length; i++) {
                NBTTagCompound enchantData = (NBTTagCompound) enchantments.get(i);
                int id = enchantData.getShort("id");
                Enchantment enchant = Enchantment.getById(id);
                if (enchant != null) {
                    this.enchantments.put(enchant, (int) enchantData.getShort("lvl"));
                }
            }
        }

        if (tag.hasKey("RepairCost")) {
            // TODO: RepairCost
            repairCost = tag.getInt("RepairCost");
        }
    }

    CraftItemMeta(Map<String, Object> map) {
        // TODO Auto-generated constructor stub
    }

    void applyToItem(NBTTagCompound itemTag) {
        NBTTagCompound display = getDisplay(itemTag);

        if (hasDisplayName()) {
            display.setString("Name", displayName);
        } else {
            display.remove("Name");
        }

        if (hasLore()) {
            NBTTagList list = display.getList("Lore");
            for (int i = 0; i < lore.size(); i++) {
                list.add(new NBTTagString(String.valueOf(i), lore.get(i)));
            }
            display.set("Lore", list);
        } else {
            display.remove("Lore");
        }

        if (hasEnchants()) {
            NBTTagList enchants = new NBTTagList();
            for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
                NBTTagCompound enchantData = new NBTTagCompound();
                enchantData.setShort("id", (short) enchant.getKey().getId());
                enchantData.setShort("lvl", (short) (int) enchant.getValue());
                enchants.add(enchantData);
            }
            itemTag.set("ench", enchants);
        } else {
            itemTag.remove("ench");
        }

        if (hasRepairCost()) {
            itemTag.setInt("RepairCost", repairCost);
        } else {
            itemTag.remove("RepairCost");
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
        // TODO
        return builder;
    }

    CraftItemFactory.SerializableMeta.Deserializers deserializer() {
        return CraftItemFactory.SerializableMeta.Deserializers.UNSPECIFIC;
    }
}
