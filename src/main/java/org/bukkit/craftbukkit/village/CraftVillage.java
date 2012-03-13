package org.bukkit.craftbukkit.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityIronGolem;
import net.minecraft.server.EntityVillager;
import net.minecraft.server.Village;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftIronGolem;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.village.VillageAggressor;
import org.bukkit.village.VillageDoor;

public class CraftVillage implements org.bukkit.village.Village {
    private final Village handle;

    public CraftVillage(Village village) {
        handle = village;
    }

    public boolean isAbandoned() {
        return handle.isAbandoned();
    }

    public void abandon() {
        handle.getDoors().clear();
    }

    public int getPopulation() {
        return handle.getPopulationCount();
    }

    public Location getCenter() {
        return new Location(getWorld(), handle.getCenter().x, handle.getCenter().y, handle.getCenter().z);
    }

    public int getSize() {
        return handle.getSize();
    }

    // Doors to a village are constantly changing, there is probably no better way to do this other than to implement it directly in nms.VillageDoor
    public List<VillageDoor> getDoors() {
        List doorList = handle.getDoors();
        List<VillageDoor> doors = new ArrayList<VillageDoor>(doorList.size());
        Iterator<net.minecraft.server.VillageDoor> i = doorList.iterator();

        while (i.hasNext()) {
            doors.add(new CraftVillageDoor(i.next(), getHandle()));
        }

        return doors;
    }

    public int getDoorCount() {
        return handle.getDoorCount();
    }

    // These are handled the same as doors... Very ugly. :(
    public List<VillageAggressor> getAggressors() {
        List attackerList = handle.i;
        List<VillageAggressor> attackers = new ArrayList<VillageAggressor>(attackerList.size());
        Iterator<net.minecraft.server.VillageAgressor> i = attackerList.iterator();

        while (i.hasNext()) {
            attackers.add(new CraftVillageAggressor(i.next()));
        }

        return attackers;
    }

    public int getAggressorCount() {
        return handle.i.size();
    }

    public CraftIronGolem spawnGolem() {
        EntityIronGolem entity = new EntityIronGolem(handle.world);
        setPosition(entity);
        entity.world.addEntity(entity); // SpawnReason.CUSTOM

        return (CraftIronGolem) entity.getBukkitEntity();
    }

    public CraftVillager spawnVillager() {
        return spawnVillager(false);
    }

    public CraftVillager spawnVillager(boolean child) {
        EntityVillager entity = new EntityVillager(handle.world, handle.world.random.nextInt(5));
        if (child) {
            entity.setAge(-6000);
        }
        setPosition(entity);
        entity.world.addEntity(entity); // SpawnReason.CUSTOM

        return (CraftVillager) entity.getBukkitEntity();
    }

    public boolean isUnderSiege() {
        return this.equals(getWorld().getVillageManager().getVillageUnderSiege());
    }

    public CraftWorld getWorld() {
        return handle.world.getWorld();
    }

    public Village getHandle() {
        return handle;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CraftVillage)) return false;
        return object == this;
    }

    @Override
    public int hashCode() {
        return handle.hashCode();
    }

    private void setPosition(Entity entity) {
        int x = handle.getCenter().x + handle.world.random.nextInt(16) - 8;
        int y = handle.getCenter().y + handle.world.random.nextInt(6) - 3;
        int z = handle.getCenter().z + handle.world.random.nextInt(16) - 8;

        entity.setPosition(x, y, z);
    }
}
