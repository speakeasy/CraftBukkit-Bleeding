package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityFishingHook;
import net.minecraft.server.EntityHuman;

import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ProjectileShooter;

public class CraftFish extends AbstractProjectile implements Fish {
    public CraftFish(CraftServer server, EntityFishingHook entity) {
        super(server, entity);
    }

    public ProjectileShooter getShooter() {
        if (getHandle().owner != null) {
            return (LivingEntity) getHandle().owner.getBukkitEntity();
        } else {
            BlockState block = getWorld().getBlockAt(getHandle().getSourceBlock()).getState();
            if (block instanceof Dispenser) {
                return (Dispenser) block;
            }
        }

        return null;
    }

    public void setShooter(ProjectileShooter shooter) {
        if (shooter instanceof CraftLivingEntity) {
            Validate.isTrue(shooter instanceof HumanEntity, "Entities other than players cannot shoot fishing hooks.");
            getHandle().owner = (EntityHuman) ((CraftLivingEntity) shooter).entity;
        } else if (shooter instanceof Dispenser) {
            getHandle().owner = null;
            getHandle().setSourceBlock(((Dispenser) shooter).getBlock().getLocation());
        }
    }

    @Override
    public EntityFishingHook getHandle() {
        return (EntityFishingHook) entity;
    }

    @Override
    public String toString() {
        return "CraftFish";
    }

    public EntityType getType() {
        return EntityType.FISHING_HOOK;
    }
}
