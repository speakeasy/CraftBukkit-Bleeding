package org.bukkit.craftbukkit.entity;

import java.util.EnumSet;

import net.minecraft.server.EntityFallingBlock;

import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingSand;

public class CraftFallingBlock extends CraftEntity implements FallingSand {
    public CraftFallingBlock(CraftServer server, EntityFallingBlock entity) {
        super(server, entity);
    }

    @Override
    public EntityFallingBlock getHandle() {
        return (EntityFallingBlock) entity;
    }

    @Override
    public String toString() {
        return "CraftFallingSand";
    }

    public Material getBlockType() {
        return Material.getMaterial(getHandle().id);
    }

    public void setBlockType(Material material) {
        if (!EnumSet.of(Material.SAND, Material.GRAVEL, Material.DRAGON_EGG).contains(material)) {
            throw new IllegalArgumentException("That block cannot fall.");
        }
        getHandle().id = material.getId();
        // TODO: Update client?
    }

    public EntityType getType() {
        return EntityType.FALLING_BLOCK;
    }
}
