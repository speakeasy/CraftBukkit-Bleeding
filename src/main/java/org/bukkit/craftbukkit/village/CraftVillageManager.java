package org.bukkit.craftbukkit.village;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.Village;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.village.VillageManager;

public class CraftVillageManager implements VillageManager {
    private final WorldServer world;
    private final Map<Village, CraftVillage> villages = new HashMap<Village, CraftVillage>();

    public CraftVillageManager(WorldServer world) {
        this.world = world;
    }

    public List<org.bukkit.village.Village> getVillages() {
        return new ArrayList<org.bukkit.village.Village>(villages.values());
    }

    public CraftVillage getVillage(Location location) {
        return getVillage(location, 16);
    }

    public CraftVillage getVillage(Location location, int range) {
        Village found = world.villages.getClosestVillage(location.getBlockX(), location.getBlockY(), location.getBlockZ(), range);

        return found == null ? null : villages.get(found);
    }

    public CraftVillage getVillageUnderSiege() {
        if (!world.O.b) { // If siege is not present, return no village
            return null;
        }
        return villages.get(world.O.f);
    }

    public CraftVillage addVillage(Village village) {
        return villages.put(village, new CraftVillage(village));
    }

    public CraftVillage getVillage(Village village) {
        return villages.get(village);
    }

    public void removeVillage(Village village) {
        villages.remove(village);
    }
}
