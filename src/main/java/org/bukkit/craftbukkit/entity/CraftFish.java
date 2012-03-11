package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityFishingHook;
import net.minecraft.server.EntityHuman;

import org.apache.commons.lang.Validate;
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
        }

        return null;
    }

    public void setShooter(ProjectileShooter shooter) {
        Validate.isTrue(shooter instanceof HumanEntity, "Only players can shoot fishing hooks.");
        if (shooter instanceof CraftLivingEntity) {
            getHandle().owner = (EntityHuman) ((CraftLivingEntity) shooter).entity;
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
