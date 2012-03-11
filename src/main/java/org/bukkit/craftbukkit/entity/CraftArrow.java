package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityArrow;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ProjectileShooter;

public class CraftArrow extends AbstractProjectile implements Arrow {

    public CraftArrow(CraftServer server, EntityArrow entity) {
        super(server, entity);
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
            getHandle().shooter = ((CraftLivingEntity) shooter).entity;
        } else if (shooter instanceof Dispenser) {
            getHandle().shooter = null;
            setSourceBlock(getHandle(), ((Dispenser) shooter).getBlock().getLocation());
        }
    }

    @Override
    public EntityArrow getHandle() {
        return (EntityArrow) entity;
    }

    @Override
    public String toString() {
        return "CraftArrow";
    }

    public EntityType getType() {
        return EntityType.ARROW;
    }

    public static void setSourceBlock(EntityArrow proj, int x, int y, int z) {
        proj.sourceX = x;
        proj.sourceY = y;
        proj.sourceZ = z;
    }

    public static void setSourceBlock(EntityArrow proj, Location loc) {
        setSourceBlock(proj, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static Location getSourceBlock(EntityArrow proj) {
        return new Location(proj.world.getWorld(), proj.sourceX, proj.sourceY, proj.sourceZ);
    }
}
