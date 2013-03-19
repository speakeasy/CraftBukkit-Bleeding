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
}
