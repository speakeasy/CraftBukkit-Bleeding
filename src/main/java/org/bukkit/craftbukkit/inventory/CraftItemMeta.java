package org.bukkit.craftbukkit.inventory;

import java.util.List;
import java.util.Map;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

class CraftItemMeta implements ItemMeta {
    private String displayName;
    private List<String> lore; // TODO: lore
    private Map<Enchantment, Integer> enchantments; // TODO: enchantments
    private int maxStackSize; // Scratch this

    CraftItemMeta() {}

    CraftItemMeta(CraftItemStack itemstack) {
        net.minecraft.server.ItemStack nmsStack = itemstack.getHandle();

        readTag(nmsStack.tag);
    }

    public CraftItemMeta clone() {
        try {
            return (CraftItemMeta) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    private void readTag(NBTTagCompound itemTag) {
        if (itemTag == null) {
            return;
        }

        if (itemTag.hasKey("display")) {
            NBTTagCompound display = itemTag.getCompound("display");
            if (display.hasKey("Name")) {
                setDisplayName(display.getString("Name"));
            }

            if (display.hasKey("Lore")) {
                // TODO: Lore
            }
        }

        if (itemTag.hasKey("ench")) {
            NBTTagList enchantments = itemTag.getList("ench");

            // TODO: Enchantments
        }
    }

    void applyToItem(CraftItemStack item) {
        net.minecraft.server.ItemStack nmsStack = item.getHandle();
        NBTTagCompound itemTag = nmsStack.tag;

        if (itemTag == null) {
            itemTag = new NBTTagCompound();
        }

        if (displayName != null) {
            setDisplay(itemTag, new NBTTagString("Name", displayName));
        }

        // TODO: Apply enchantments
    }

    void setDisplay(NBTTagCompound tag, NBTBase nbtitem) {
        NBTTagCompound display = tag.getCompound("display");

        if (!tag.hasKey("display")) {
            tag.setCompound("display", tag);
        }

        display.set(nbtitem.getName(), nbtitem);
    }

    boolean applicableTo(ItemStack itemstack) {
        return true;
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

    public int getCustomStackSize() {
        return maxStackSize;
    }

    public void setCustomStackSize(int size) {
        maxStackSize = size;
    }

    public boolean hasCustomStackSize() {
        return maxStackSize > 0;
    }

    @Override
    public boolean equals(Object object) {
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
}
