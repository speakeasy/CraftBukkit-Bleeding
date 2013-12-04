package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntitySheep;

import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.material.Wool;

public class CraftSheep extends CraftAnimals implements Sheep {
    public CraftSheep(CraftServer server, EntitySheep entity) {
        super(server, entity);
    }

    public DyeColor getColor() {
        return DyeColor.values()[((byte) getHandle().getColor())];
    }

    public void setColor(DyeColor color) {
        getHandle().setColor(new Wool(color).getData());
    }

    public boolean isSheared() {
        return getHandle().isSheared();
    }

    public void setSheared(boolean flag) {
        getHandle().setSheared(flag);
    }

    @Override
    public EntitySheep getHandle() {
        return (EntitySheep) entity;
    }

    @Override
    public String toString() {
        return "CraftSheep";
    }

    public EntityType getType() {
        return EntityType.SHEEP;
    }
}
