package org.bukkit.craftbukkit.inventory;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.Collections;
import java.util.HashMap;

import net.minecraft.server.TileEntityChest;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Test;

public class InventoryAddTest extends AbstractTestingBase {
    public static ItemStack[] emptyChest = new ItemStack[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};

    @Test
    public void testAdd64OfMax64() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItem(new ItemStack(Material.DIRT, 64));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.DIRT, 64);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAdd100OfMax64() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItem(new ItemStack(Material.DIRT, 100));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.DIRT, 64);
        expected[1] = new ItemStack(Material.DIRT, 36);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAddSplit64OfMax64() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItemSplit(new ItemStack(Material.DIRT, 64));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.DIRT, 64);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAddSplit100OfMax64() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItemSplit(new ItemStack(Material.DIRT, 100));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.DIRT, 64);
        expected[1] = new ItemStack(Material.DIRT, 36);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAdd64OfMax64ToExisting10() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        bukkitinv.setItem(0, new ItemStack(Material.DIRT, 10));
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItem(new ItemStack(Material.DIRT, 64));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.DIRT, 64);
        expected[1] = new ItemStack(Material.DIRT, 10);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAdd100OfMax64ToExisting10() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        bukkitinv.setItem(0, new ItemStack(Material.DIRT, 10));
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItem(new ItemStack(Material.DIRT, 100));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.DIRT, 64);
        expected[1] = new ItemStack(Material.DIRT, 46);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAddSplit64OfMax64ToExisting10() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        bukkitinv.setItem(0, new ItemStack(Material.DIRT, 10));
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItemSplit(new ItemStack(Material.DIRT, 64));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.DIRT, 64);
        expected[1] = new ItemStack(Material.DIRT, 10);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAddSplit100OfMax64ToExisting10() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        bukkitinv.setItem(0, new ItemStack(Material.DIRT, 10));
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItemSplit(new ItemStack(Material.DIRT, 100));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.DIRT, 64);
        expected[1] = new ItemStack(Material.DIRT, 46);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    // Now, compare to Ender Pearls

    @Test
    public void testAdd64OfMax16() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItem(new ItemStack(Material.ENDER_PEARL, 64));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.ENDER_PEARL, 64);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAdd100OfMax16() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItem(new ItemStack(Material.ENDER_PEARL, 100));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.ENDER_PEARL, 64);
        expected[1] = new ItemStack(Material.ENDER_PEARL, 36);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAddSplit64OfMax16() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItemSplit(new ItemStack(Material.ENDER_PEARL, 64));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[1] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[2] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[3] = new ItemStack(Material.ENDER_PEARL, 16);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAddSplit100OfMax16() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItemSplit(new ItemStack(Material.ENDER_PEARL, 100));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[1] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[2] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[3] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[4] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[5] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[6] = new ItemStack(Material.ENDER_PEARL, 4);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAdd64OfMax16ToExisting10() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        bukkitinv.setItem(0, new ItemStack(Material.ENDER_PEARL, 10));
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItem(new ItemStack(Material.ENDER_PEARL, 64));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.ENDER_PEARL, 64);
        expected[1] = new ItemStack(Material.ENDER_PEARL, 10);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAdd100OfMax16ToExisting10() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        bukkitinv.setItem(0, new ItemStack(Material.ENDER_PEARL, 10));
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItem(new ItemStack(Material.ENDER_PEARL, 100));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.ENDER_PEARL, 64);
        expected[1] = new ItemStack(Material.ENDER_PEARL, 46);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAddSplit64OfMax16ToExisting10() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        bukkitinv.setItem(0, new ItemStack(Material.ENDER_PEARL, 10));
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItemSplit(new ItemStack(Material.ENDER_PEARL, 64));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[1] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[2] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[3] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[4] = new ItemStack(Material.ENDER_PEARL, 10);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }

    @Test
    public void testAddSplit100OfMax16ToExisting10() {
        TileEntityChest nmsinv = new TileEntityChest();
        Inventory bukkitinv = new CraftInventory(nmsinv);
        bukkitinv.setItem(0, new ItemStack(Material.ENDER_PEARL, 10));
        HashMap<Integer, ItemStack> leftover = bukkitinv.addItemSplit(new ItemStack(Material.ENDER_PEARL, 100));

        ItemStack[] expected = emptyChest.clone();
        expected[0] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[1] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[2] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[3] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[4] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[5] = new ItemStack(Material.ENDER_PEARL, 16);
        expected[6] = new ItemStack(Material.ENDER_PEARL, 14);

        assertThat(bukkitinv.getContents(), is(expected));
        assertThat(leftover, is(Collections.EMPTY_MAP));
    }
}
