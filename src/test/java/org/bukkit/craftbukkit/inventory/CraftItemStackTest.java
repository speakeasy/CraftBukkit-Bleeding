package org.bukkit.craftbukkit.inventory;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import net.minecraft.server.Enchantment;
import net.minecraft.server.StatisticList;

import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

public class CraftItemStackTest {
    @Before
    public void setUp() throws Exception {
        StatisticList.a();
    }

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
}
