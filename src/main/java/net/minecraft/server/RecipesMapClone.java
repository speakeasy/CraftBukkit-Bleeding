package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftDynamicRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.Recipe.RecipeType;
// CraftBukkit end

public class RecipesMapClone implements IRecipe {
    private ItemStack lastResult; // CraftBukkit

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (itemstack1 != null) {
                if (itemstack1.id == Item.MAP.id) {
                    if (itemstack != null) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.id != Item.MAP_EMPTY.id) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return itemstack != null && i > 0;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (itemstack1 != null) {
                if (itemstack1.id == Item.MAP.id) {
                    if (itemstack != null) {
                        lastResult = null; // CraftBukkit
                        return null;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.id != Item.MAP_EMPTY.id) {
                        lastResult = null; // CraftBukkit
                        return null;
                    }

                    ++i;
                }
            }
        }

        if (itemstack != null && i >= 1) {
            ItemStack itemstack2 = new ItemStack(Item.MAP, i + 1, itemstack.getData());

            if (itemstack.s()) {
                itemstack2.c(itemstack.r());
            }

            lastResult = itemstack2; // CraftBukkit
            return itemstack2;
        } else {
            lastResult = null; // CraftBukkit
            return null;
        }
    }

    public int a() {
        return 9;
    }

    public ItemStack b() {
        return null;
    }

    // CraftBukkit start
    public Recipe toBukkitRecipe() {
        return new CraftDynamicRecipe(lastResult, RecipeType.MAP_COPY);
    }
    // CraftBukkit end
}
