package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.ContainerAnvil;
import net.minecraft.server.IInventory;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryAnvil extends CraftInventory implements AnvilInventory {
    private final ContainerAnvil anvil;

    public CraftInventoryAnvil(ContainerAnvil anvil) {
        super(anvil.g);
        this.anvil = anvil;
    }

    public IInventory getResultInventory() {
        return anvil.f;
    }

    public IInventory getIngredientsInventory() {
        return inventory;
    }

    @Override
    public ItemStack getItem(int slot) {
        Validate.isTrue(slot < getSize() && slot >= 0, "Slot must be greater than 0 and less than "+getSize());

        if (slot < getIngredientsInventory().getSize()) {
            net.minecraft.server.ItemStack nmsItem = getIngredientsInventory().getItem(slot);
            return nmsItem != null ? CraftItemStack.asCraftMirror(nmsItem) : null;
        } else {
            net.minecraft.server.ItemStack nmsItem = getResultInventory().getItem(slot - getIngredientsInventory().getSize());
            return nmsItem != null ? CraftItemStack.asCraftMirror(nmsItem) : null;
        }
    }

    @Override
    public void setItem(int index, ItemStack item) {
        Validate.isTrue(index < getSize() && index >= 0, "Slot must be greater than 0 and less than "+getSize());

        if (index < getIngredientsInventory().getSize()) {
            net.minecraft.server.ItemStack nmsItem = item != null ? CraftItemStack.asNMSCopy(item) : null;
            getIngredientsInventory().setItem(index, nmsItem);
        } else {
            net.minecraft.server.ItemStack nmsItem = item != null ? CraftItemStack.asNMSCopy(item) : null;
            getResultInventory().setItem(index - getIngredientsInventory().getSize(), nmsItem);
        }
    }

    @Override
    public int getSize() {
        return anvil.g.getSize() + anvil.f.getSize();
    }

    @Override
    public int getRepairCost() {
        return anvil.a;
    }

    @Override
    public void setRepairCost(int cost) {
        anvil.a = cost;
        anvil.b();
    }

    @Override
    public String getNewName() {
        return anvil.m;
    }

    @Override
    public void setNewName(String newName) {
        anvil.a(newName);
        anvil.e();
        anvil.b();
    }
}
