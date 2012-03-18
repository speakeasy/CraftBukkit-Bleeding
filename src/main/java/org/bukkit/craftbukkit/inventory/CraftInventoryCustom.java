package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Recipe;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;

public class CraftInventoryCustom extends CraftInventory {
    public CraftInventoryCustom(InventoryHolder owner, InventoryType type) {
        super(new MinecraftInventory(owner, type));
    }

    public CraftInventoryCustom(InventoryHolder owner, int size) {
        super(new MinecraftInventory(owner, size));
    }

    public CraftInventoryCustom(InventoryHolder owner, int size, String title) {
        super(new MinecraftInventory(owner, size, title));
    }

    static class MinecraftInventory implements IInventory {
        private ItemStack[] items;
        private int maxStack = MAX_STACK;
        private List<HumanEntity> viewers;
        private String title;
        private InventoryType type;
        private InventoryHolder owner;

        public MinecraftInventory(InventoryHolder owner, InventoryType type) {
            this(owner, type.getDefaultSize(), type.getDefaultTitle());
            this.type = type;
        }

        public MinecraftInventory(InventoryHolder owner, int size) {
            this(owner, size, "Chest");
        }

        public MinecraftInventory(InventoryHolder owner, int size, String title) {
            this.items = new ItemStack[size];
            this.title = title;
            this.viewers = new ArrayList<HumanEntity>();
            this.owner = owner;
            this.type = InventoryType.CHEST;
        }

        public int getSize() {
            return items.length;
        }

        public ItemStack getItem(int i) {
            return items[i];
        }

        public ItemStack splitStack(int i, int j) {
            ItemStack stack = this.getItem(i);
            ItemStack result;
            if (stack == null) return null;
            if (stack.count <= j) {
                this.setItem(i, null);
                result = stack;
            } else {
                result = new ItemStack(stack.id, j, stack.getData(), stack.getEnchantments());
                stack.count -= j;
            }
            this.update();
            return result;
        }

        public ItemStack splitWithoutUpdate(int i) {
            ItemStack stack = this.getItem(i);
            ItemStack result;
            if (stack == null) return null;
            if (stack.count <= 1) {
                this.setItem(i, null);
                result = stack;
            } else {
                result = new ItemStack(stack.id, 1, stack.getData(), stack.getEnchantments());
                stack.count -= 1;
            }
            return result;
        }

        public void setItem(int i, ItemStack itemstack) {
            items[i] = itemstack;
            if (itemstack != null && this.getMaxStackSize() > 0 && itemstack.count > this.getMaxStackSize()) {
                itemstack.count = this.getMaxStackSize();
            }
        }

        public String getName() {
            return title;
        }

        public int getMaxStackSize() {
            return maxStack;
        }

        public void setMaxStackSize(int size) {
            maxStack = size;
        }

        public void update() {}

        public boolean a(EntityHuman entityhuman) {
            return true;
        }

        public ItemStack[] getContents() {
            return items;
        }

        public void onOpen(CraftHumanEntity who) {
            viewers.add(who);
        }

        public void onClose(CraftHumanEntity who) {
            viewers.remove(who);
        }

        public List<HumanEntity> getViewers() {
            return viewers;
        }

        public InventoryType getType() {
            return type;
        }

        public void f() {}

        public void g() {}

        public InventoryHolder getOwner() {
            return owner;
        }
    }

    public static class Brewing extends CraftInventoryCustom implements BrewerInventory {
        public Brewing(InventoryHolder owner) {
            super(owner, InventoryType.BREWING);
        }

        public org.bukkit.inventory.ItemStack getIngredient() {
            return getItem(3);
        }

        public void setIngredient(org.bukkit.inventory.ItemStack ingredient) {
            setItem(3, ingredient);
        }
    }

    public static class Enchanting extends CraftInventoryCustom implements EnchantingInventory {
        public Enchanting(InventoryHolder owner) {
            super(owner, InventoryType.ENCHANTING);
        }

        public void setItem(org.bukkit.inventory.ItemStack item) {
            setItem(0,item);
        }

        public org.bukkit.inventory.ItemStack getItem() {
            return getItem(0);
        }
    }

    public static class Workbench extends CraftInventoryCustom implements CraftingInventory {
        public Workbench(InventoryHolder owner) {
            super(owner, InventoryType.WORKBENCH);
        }

        public org.bukkit.inventory.ItemStack getResult() {
            return getItem(0);
        }

        public org.bukkit.inventory.ItemStack[] getMatrix() {
            org.bukkit.inventory.ItemStack[] items = new org.bukkit.inventory.ItemStack[getSize() - 1];

            for (int i = 1; i < getSize(); i++ ) {
                items[i] = getItem(i);
            }

            return items;
        }

        public void setResult(org.bukkit.inventory.ItemStack newResult) {
            setItem(0, newResult);
        }

        public void setMatrix(org.bukkit.inventory.ItemStack[] contents) {
            if (getSize() - 1 < contents.length) {
                throw new IllegalArgumentException("Invalid inventory size; expected " + (getSize() - 1) + " or less");
            }
            for (int i = 1; i < getSize(); i++ ) {
                setItem(i, contents[i]);
            }
        }

        public Recipe getRecipe() {
            // TODO: Figure out what, if any, recipe is on the matrix?
            return null;
        }
    }

    public static class Furnace extends CraftInventoryCustom implements FurnaceInventory {
        public Furnace(InventoryHolder owner) {
            super(owner, InventoryType.FURNACE);
        }

        public org.bukkit.inventory.ItemStack getResult() {
            return getItem(2);
        }

        public org.bukkit.inventory.ItemStack getFuel() {
            return getItem(1);
        }

        public org.bukkit.inventory.ItemStack getSmelting() {
            return getItem(0);
        }

        public void setFuel(org.bukkit.inventory.ItemStack stack) {
            setItem(1,stack);
        }

        public void setResult(org.bukkit.inventory.ItemStack stack) {
            setItem(2,stack);
        }

        public void setSmelting(org.bukkit.inventory.ItemStack stack) {
            setItem(0,stack);
        }
    }
}
