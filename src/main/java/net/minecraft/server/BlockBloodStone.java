package net.minecraft.server;

public class BlockBloodStone extends Block {

    public BlockBloodStone(int i, int j) {
        super(i, j, Material.STONE);
    }

    // CraftBukkit start
    public void doPhysics(World world, int i, int j, int k, int l) {
        if (net.minecraft.server.Block.byId[l] != null && net.minecraft.server.Block.byId[l].isPowerSource()) {
            org.bukkit.craftbukkit.event.CraftEventFactory.callRedstoneChange(world, i, j, k);
        }
    }
    // CraftBukkit end
}
