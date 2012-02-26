package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityProjectile;

import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ProjectileShooter;

public abstract class CraftProjectile extends AbstractProjectile {
    public CraftProjectile(CraftServer server, net.minecraft.server.Entity entity) {
        super(server, entity);
    }

    public ProjectileShooter getShooter() {
        if (getHandle().shooter != null) {
            return (LivingEntity) getHandle().shooter.getBukkitEntity();
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
            getHandle().shooter = (EntityLiving) ((CraftLivingEntity) shooter).entity;
        } else if (shooter instanceof Dispenser) {
            getHandle().shooter = null;
            getHandle().setSourceBlock(((Dispenser) shooter).getBlock().getLocation());
        }
    }

    @Override
    public EntityProjectile getHandle() {
        return (EntityProjectile) entity;
    }

    @Override
    public String toString() {
        return "CraftProjectile";
    }
}
