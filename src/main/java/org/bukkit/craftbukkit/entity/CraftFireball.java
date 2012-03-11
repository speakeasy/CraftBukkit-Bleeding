package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityLiving;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ProjectileShooter;
import org.bukkit.util.Vector;

public class CraftFireball extends AbstractProjectile implements Fireball {
    public CraftFireball(CraftServer server, EntityFireball entity) {
        super(server, entity);
    }

    public float getYield() {
        return getHandle().yield;
    }

    public boolean isIncendiary() {
        return getHandle().isIncendiary;
    }

    public void setIsIncendiary(boolean isIncendiary) {
        getHandle().isIncendiary = isIncendiary;
    }

    public void setYield(float yield) {
        getHandle().yield = yield;
    }

    public ProjectileShooter getShooter() {
        if (getHandle().shooter != null) {
            return (LivingEntity) getHandle().shooter.getBukkitEntity();
        } else {
            BlockState block = getWorld().getBlockAt(getSourceBlock(getHandle())).getState();
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
            setSourceBlock(getHandle(), ((Dispenser) shooter).getBlock().getLocation());
        }
    }

    public Vector getDirection() {
        return new Vector(getHandle().dirX, getHandle().dirY, getHandle().dirZ);
    }

    public void setDirection(Vector direction) {
        getHandle().setDirection(direction.getX(), direction.getY(), direction.getZ());
    }

    @Override
    public EntityFireball getHandle() {
        return (EntityFireball) entity;
    }

    @Override
    public String toString() {
        return "CraftFireball";
    }

    public EntityType getType() {
        return EntityType.FIREBALL;
    }

    public static void setSourceBlock(EntityFireball proj, int x, int y, int z) {
        proj.sourceX = x;
        proj.sourceY = y;
        proj.sourceZ = z;
    }

    public static void setSourceBlock(EntityFireball proj, Location loc) {
        setSourceBlock(proj, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static Location getSourceBlock(EntityFireball proj) {
        return new Location(proj.world.getWorld(), proj.sourceX, proj.sourceY, proj.sourceZ);
    }
}
