package org.bukkit.craftbukkit.block;

import java.util.Collection;
import java.util.List;

import net.minecraft.server.TileEntityBeacon;

import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
import org.bukkit.block.Beacon;
import org.bukkit.block.Beacon.ActivationState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventoryBeacon;
import org.bukkit.craftbukkit.potion.CraftPotionBrewer;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CraftBeacon extends CraftBlockState implements Beacon {
    private final CraftWorld world;
    private final TileEntityBeacon beacon;

    public CraftBeacon(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        beacon = (TileEntityBeacon) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public BeaconInventory getInventory() {
        return new CraftInventoryBeacon(beacon);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);

        if (result) {
            beacon.update();
        }

        return result;
    }

    @Override
    public List<PotionEffect> getEffects() {
        return CraftPotionBrewer.nmsToBukkitEffects(beacon.effects);
    }

    @Override
    public void setEffects(Collection<PotionEffect> newEffects) {
        Validate.noNullElements(newEffects, "Cannot set null PotionEffects");
        beacon.customEffects = true;
        beacon.effects = CraftPotionBrewer.bukkitToNmsEffects(newEffects);
    }

    @Override
    public List<PotionEffect> getDefaultEffects() {
        return CraftPotionBrewer.nmsToBukkitEffects(beacon.getDefaultEffects());
    }

    @Override
    public void resetEffects() {
        beacon.customEffects = false;
        beacon.updateEffects();
    }

    @Override
    public boolean hasCustomEffects() {
        return beacon.customEffects;
    }

    @Override
    public boolean isActive() {
        return beacon.isEnabled();
    }

    @Override
    public int getPyramidSize() {
        return beacon.e;
    }

    @Override
    public int getPyramidSize(boolean calculate) {
        if (!calculate) {
            return beacon.e;
        }
        return beacon.countPyramid(4);
    }

    @Override
    public int getPyramidSize(boolean calculate, int maximum) {
        Validate.isTrue(maximum > 1, "Maximum number of layers must be at least 1");
        if (!calculate) {
            return beacon.e;
        }
        return beacon.countPyramid(maximum);
    }

    @Override
    public void setActivationState(ActivationState state) {
        if (state == null) {
            state = ActivationState.DEFAULT;
        }
        beacon.overrideState = state;
    }

    @Override
    public double getRadius() {
        return beacon.getRadius();
    }

    @Override
    public double getDefaultRadius() {
        return beacon.e >= 4 ? 50D : (double) (beacon.e * 10 + 10);
    }

    @Override
    public void setRadius(double radius) {
        Validate.isTrue(radius >= 0, "Radius may not be lower than 0");
        beacon.radius = radius;
    }

    @Override
    public void resetRadius() {
        beacon.radius = -1;
    }

    @Override
    public ActivationState getActivationState() {
        return beacon.overrideState;
    }
}

