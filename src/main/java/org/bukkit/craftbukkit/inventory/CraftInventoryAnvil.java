package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.IInventory;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryAnvil extends CraftInventory implements AnvilInventory {
    private final IInventory resultInventory;

    public CraftInventoryAnvil(IInventory anvil, IInventory resultInventory) {
        super(anvil);
        this.resultInventory = resultInventory;
    }

    public IInventory getResultInventory() {
        return resultInventory;
    }

    public ItemStack getBaseItem() {
        return getItem(0);
    }

    public void setBaseItem(ItemStack itemStack) {
        setItem(0, itemStack);
    }

    public ItemStack getCombiningItem() {
        return getItem(1);
    }

    public void setCombiningItem(ItemStack itemStack) {
        setItem(1, itemStack);
    }

    public ItemStack getResult() {
        net.minecraft.server.ItemStack item = getResultInventory().getItem(0);
        if (item != null) return CraftItemStack.asCraftMirror(item);
        return null;
    }

    public void setResult(ItemStack itemStack) {
        getResultInventory().setItem(0, CraftItemStack.asNMSCopy(itemStack));
    }
}
