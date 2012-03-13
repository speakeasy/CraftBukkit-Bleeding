package org.bukkit.craftbukkit.village;

import net.minecraft.server.Village;
import net.minecraft.server.VillageDoor;

import org.bukkit.Location;

public class CraftVillageDoor implements org.bukkit.village.VillageDoor {
    private VillageDoor handle;
    private CraftVillage village;
    private Location location;

    public CraftVillageDoor(VillageDoor handle, Village village) {
        this.handle = handle;
        this.village = village.world.getWorld().getVillageManager().getVillage(village);
    }

    public Location getLocation() {
        if (location == null) {
            location = new Location(getVillage().getWorld(), handle.locX, handle.locY, handle.locZ);
        }
        return location;
    }

    public CraftVillage getVillage() {
        return village;
    }

    public int getAge() {
        return village.getHandle().time - handle.addedTime;
    }

    public void setAge(int age) {
        handle.addedTime = village.getHandle().time - age;
    }

}
