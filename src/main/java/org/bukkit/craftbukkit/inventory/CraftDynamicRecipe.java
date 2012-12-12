package org.bukkit.craftbukkit.inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import net.minecraft.server.IRecipe;
import net.minecraft.server.InventoryCrafting;
import net.minecraft.server.World;

public class CraftDynamicRecipe implements Recipe {
    net.minecraft.server.ItemStack result;
    private RecipeType type;

    public CraftDynamicRecipe(net.minecraft.server.ItemStack result, RecipeType type) {
        this.result = result;
        this.type = type;
    }

    public ItemStack getResult() {
        return new CraftItemStack(result);
    }

    public RecipeType getType() {
        return type;
    }

    // A sort of hack to make an IRecipe for tool repairs
    public static class RepairRecipe implements IRecipe {
        private net.minecraft.server.ItemStack result;

        public RepairRecipe(net.minecraft.server.ItemStack result) {
            this.result = result;
        }

        public boolean a(InventoryCrafting inventorycrafting, World world) {
            return false; // never called
        }

        public net.minecraft.server.ItemStack a(InventoryCrafting inventorycrafting) {
            return null; // never called
        }

        public int a() {
            return 0; // never called
        }

        public net.minecraft.server.ItemStack b() {
            return null; // never called
        }

        public Recipe toBukkitRecipe() {
            return new CraftDynamicRecipe(result, RecipeType.REPAIR);
        }
    }
}
