package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.TileEntityBeacon;

import org.bukkit.block.Beacon;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryBeacon extends CraftInventory implements BeaconInventory {
    public CraftInventoryBeacon(TileEntityBeacon beacon) {
        super(beacon);
    }

    public void setItem(ItemStack item) {
        setItem(0, item);
    }

    public ItemStack getItem() {
        return getItem(0);
    }

    @Override
    public Beacon getHolder() {
        TileEntityBeacon beacon = (TileEntityBeacon) inventory;
        return (Beacon) beacon.getWorld().getWorld().getBlockAt(beacon.x, beacon.y, beacon.z).getState();
    }
}
