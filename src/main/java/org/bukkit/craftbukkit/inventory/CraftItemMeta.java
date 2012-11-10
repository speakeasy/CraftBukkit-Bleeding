package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

class CraftItemMeta implements ItemMeta {
    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments; // TODO: enchantments
    private int repairCost;

    CraftItemMeta() {}

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

            // TODO: Enchantments
        }

        if (tag.hasKey("RepairCost")) {
            // TODO: RepairCost
            repairCost = tag.getInt("RepairCost");
        }
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
            // TODO: Apply enchantments
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getEnchantLevel(Enchantment ench) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<Enchantment, Integer> getEnchants() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addEnchant(Enchantment ench, int level, boolean ignoreRestrictions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeEnchant(Enchantment ench) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasEnchants() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("Not supported yet.");
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
}
