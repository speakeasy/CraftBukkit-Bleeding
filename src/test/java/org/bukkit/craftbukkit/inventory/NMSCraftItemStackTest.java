package org.bukkit.craftbukkit.inventory;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import net.minecraft.server.Enchantment;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Test;

public class NMSCraftItemStackTest extends AbstractTestingBase {

    @Test
    public void testCloneEnchantedItem() throws Exception {
        net.minecraft.server.ItemStack nmsItemStack = new net.minecraft.server.ItemStack(net.minecraft.server.Item.POTION);
        nmsItemStack.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemStack itemStack = CraftItemStack.asCraftMirror(nmsItemStack);
        ItemStack clone = itemStack.clone();
        assertThat(clone.getType(), is(itemStack.getType()));
        assertThat(clone.getAmount(), is(itemStack.getAmount()));
        assertThat(clone.getDurability(), is(itemStack.getDurability()));
        assertThat(clone.getEnchantments(), is(itemStack.getEnchantments()));
        assertThat(clone.getTypeId(), is(itemStack.getTypeId()));
        assertThat(clone.getData(), is(itemStack.getData()));
    }

    @Test
    public void testCloneNullItem() throws Exception {
        net.minecraft.server.ItemStack nmsItemStack = null;
        ItemStack itemStack = CraftItemStack.asCraftMirror(nmsItemStack);
        ItemStack clone = itemStack.clone();
        assertThat(clone, is((itemStack)));
    }

    @Test
    public void testStackSize() throws Exception {
        for (Material material : Material.values()) {
            ItemStack bukkit = new ItemStack(material);
            CraftItemStack craft = CraftItemStack.asCraftCopy(bukkit);
            net.minecraft.server.ItemStack nms = craft.handle;
            assertThat(material.name(), bukkit.getMaxStackSize(), is(nms != null ? nms.getItem().getMaxStackSize() : 0));
            assertThat(material.name(), craft.getMaxStackSize(), is(bukkit.getMaxStackSize()));
        }
    }
}
