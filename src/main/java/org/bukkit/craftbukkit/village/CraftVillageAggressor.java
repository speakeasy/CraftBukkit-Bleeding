package org.bukkit.craftbukkit.village;

import net.minecraft.server.VillageAgressor;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;

public class CraftVillageAggressor implements org.bukkit.village.VillageAggressor {
    private final VillageAgressor handle;

    public CraftVillageAggressor(VillageAgressor handle) {
        this.handle = handle;
    }

    public CraftLivingEntity getEntity() {
        return (CraftLivingEntity) handle.a.getBukkitEntity();
    }

    public CraftVillage getVillage() {
        return handle.c.world.getWorld().getVillageManager().getVillage(handle.c);
    }

    public int getAggressionTicks() {
        return handle.b;
    }

    public void setAggressionTicks(int ticks) {
        handle.b = ticks;
    }
}
