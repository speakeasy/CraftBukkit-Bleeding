package net.minecraft.server;

import java.util.Map.Entry;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftShapedRecipe;
import org.bukkit.inventory.Recipe.RecipeType;
import org.bukkit.inventory.ShapedRecipe;
// CraftBukkit end

public class RecipesMapExtend extends ShapedRecipes {
    private ItemStack mapIn, mapOut; // CraftBukkit

    public RecipesMapExtend() {
        super(3, 3, new ItemStack[] { new ItemStack(Item.PAPER), new ItemStack(Item.PAPER), new ItemStack(Item.PAPER), new ItemStack(Item.PAPER), new ItemStack(Item.MAP, 0, -1), new ItemStack(Item.PAPER), new ItemStack(Item.PAPER), new ItemStack(Item.PAPER), new ItemStack(Item.PAPER)}, new ItemStack(Item.MAP_EMPTY, 0, 0));
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        if (!super.a(inventorycrafting, world)) {
            return false;
        } else {
            ItemStack itemstack = null;

            for (int i = 0; i < inventorycrafting.getSize() && itemstack == null; ++i) {
                ItemStack itemstack1 = inventorycrafting.getItem(i);

                if (itemstack1 != null && itemstack1.id == Item.MAP.id) {
                    itemstack = itemstack1;
                }
            }

            if (itemstack == null) {
                return false;
            } else {
                WorldMap worldmap = Item.MAP.getSavedMap(itemstack, world);

                return worldmap == null ? false : worldmap.scale < 4;
            }
        }
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = null;

        for (int i = 0; i < inventorycrafting.getSize() && itemstack == null; ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (itemstack1 != null && itemstack1.id == Item.MAP.id) {
                itemstack = itemstack1;
                mapIn = itemstack1; // CraftBukkit
            }
        }

        itemstack = itemstack.cloneItemStack();
        itemstack.count = 1;
        if (itemstack.getTag() == null) {
            itemstack.setTag(new NBTTagCompound());
        }

        itemstack.getTag().setBoolean("map_is_scaling", true);
        mapOut = itemstack; // CraftBukkit
        return itemstack;
    }

    // CraftBukkit start
    @Override
    public ShapedRecipe toBukkitRecipe() {
        CraftShapedRecipe recipe = new CraftShapedRecipe(new CraftItemStack(mapOut));
        recipe.setType(RecipeType.MAP_EXTEND);
        recipe.shape("+++","+x+","+++");
        recipe.setIngredient('+', org.bukkit.Material.PAPER);
        recipe.setIngredient('x', org.bukkit.Material.MAP, mapIn.getData());
        return recipe;
    }
    //CraftBukkit end
}
