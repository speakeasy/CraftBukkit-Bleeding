package net.minecraft.server;

import java.util.Random;
import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class BlockIce extends BlockHalfTransparant {

    public BlockIce(int i, int j) {
        super(i, j, Material.ICE, false);
        this.frictionFactor = 0.98F;
        this.a(true);
    }

    public void a(World world, EntityHuman entityhuman, int i, int j, int k, int l) {
        super.a(world, entityhuman, i, j, k, l);
        Material material = world.getMaterial(i, j - 1, k);

        if (material.isSolid() || material.isLiquid()) {
            world.setTypeId(i, j, k, Block.WATER.id);
        }
    }

    public int a(Random random) {
        return 0;
    }

    public void a(World world, int i, int j, int k, Random random) {
        if (world.a(EnumSkyBlock.BLOCK, i, j, k) > 11 - Block.lightBlock[this.id]) {
            this.b(world, i, j, k, world.getData(i, j, k), 0);
            // CraftBukkit start - Delegate to Event Factory
            //world.setTypeId(i, j, k, Block.STATIONARY_WATER.id);
            CraftEventFactory.handleBlockFade(world, i, j, k, Block.STATIONARY_WATER.id);
            // CraftBukkit end
        }
    }

    public int g() {
        return 0;
    }

    protected ItemStack a_(int i) {
        return null;
    }
}
