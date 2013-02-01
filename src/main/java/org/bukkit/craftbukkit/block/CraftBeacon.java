package org.bukkit.craftbukkit.block;

import net.minecraft.server.TileEntityBeacon;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.potion.PotionEffectType;

public class CraftBeacon extends CraftBlockState implements Beacon {
    private int pyramidHeight;
    private PotionEffectType primaryEffect;
    private PotionEffectType secondaryEffect;

    public CraftBeacon(final Block block) {
        super(block);

        CraftWorld world = (CraftWorld) getWorld();
        TileEntityBeacon beacon = (TileEntityBeacon) world.getTileEntityAt(getX(), getY(), getZ());

        pyramidHeight = beacon.k();
        primaryEffect = PotionEffectType.getById(beacon.i());
        secondaryEffect = PotionEffectType.getById(beacon.j());
    }

    public int getPyramidHeight() {
        return pyramidHeight;
    }

    public PotionEffectType getPrimaryEffect() {
        return primaryEffect;
    }

    public void setPrimaryEffect(PotionEffectType type) {
        primaryEffect = type;
    }

    public PotionEffectType getSecondaryEffect() {
        return secondaryEffect;
    }

    public void setSecondaryEffect(PotionEffectType type) {
        secondaryEffect = type;
    }

    @Override
    public boolean update(boolean force) {
        boolean result = super.update(force);

        CraftWorld world = (CraftWorld) getWorld();
        TileEntityBeacon beacon = (TileEntityBeacon) world.getTileEntityAt(getX(), getY(), getZ());

        int primaryEffectId = primaryEffect == null ? 0 : primaryEffect.getId();
        int secondaryEffectId = secondaryEffect == null ? 0 : secondaryEffect.getId();
        // TODO: add these into TileEntityBeacon from mc-dev
        beacon.setPrimaryEffect(primaryEffectId);
        beacon.setSecondaryEffect(secondaryEffectId);

        return result;
    }
}
