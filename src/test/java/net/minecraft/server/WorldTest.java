package net.minecraft.server;

import org.bukkit.Location;
import org.bukkit.craftbukkit.test.CraftBukkitTest;
import org.junit.Test;

public class WorldTest extends CraftBukkitTest{
    @Test
    public void addEntityTest() {
        World world = server.getHandle().server.getWorldServer(0);
        ItemStack itemstack = new ItemStack(Block.STONE);
        Location spawnLoc = server.getWorlds().get(0).getSpawnLocation();
        EntityItem entityitem = new EntityItem(world, spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ(), itemstack);
        world.addEntity(entityitem);
    }
}
