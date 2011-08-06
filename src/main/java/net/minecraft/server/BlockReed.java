package net.minecraft.server;

import java.util.Random;

public class BlockReed extends Block {

    protected BlockReed(int i, int j) {
        super(i, Material.PLANT);
        this.textureId = j;
        float f = 0.375F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
        this.a(true);
    }

    public void a(World world, int i, int j, int k, Random random) {
        if (world.isEmpty(i, j + 1, k)) {
            int l;

            for (l = 1; world.getTypeId(i, j - l, k) == this.id; ++l) {
                ;
            }

            if (l < 3) {
                // CraftBukkit start
                j++;
                world.setTypeId(i, j, k, this.id);
                this.doPhysics(world, i, j, k, this.id);
                // CraftBukkit end
            }
        }
    }

    public boolean canPlace(World world, int i, int j, int k) {
        int l = world.getTypeId(i, j - 1, k);

        return l == this.id ? true : (l != Block.GRASS.id && l != Block.DIRT.id ? false : (world.getMaterial(i - 1, j - 1, k) == Material.WATER ? true : (world.getMaterial(i + 1, j - 1, k) == Material.WATER ? true : (world.getMaterial(i, j - 1, k - 1) == Material.WATER ? true : world.getMaterial(i, j - 1, k + 1) == Material.WATER))));
    }

    public void doPhysics(World world, int i, int j, int k, int l) {
        this.g(world, i, j, k);
    }

    protected final void g(World world, int i, int j, int k) {
        if (!this.f(world, i, j, k)) {
            this.g(world, i, j, k, world.getData(i, j, k));
            world.setTypeId(i, j, k, 0);

            // CraftBukkit start
            j--;
        }
        this.queueBlockTick(world, i, j, k);
    }

    public void queueBlockTick(net.minecraft.server.Chunk chunk, int x, int y, int z) {
        chunk.queueBlockTick(x, y, z, this.id, World.getTicksForChance(1, 16));
    }

    public void c(World world, int x, int y, int z) {
        this.queueBlockTick(world, x, y, z);
        // CraftBukkit end
    }

    public boolean f(World world, int i, int j, int k) {
        return this.canPlace(world, i, j, k);
    }

    public AxisAlignedBB e(World world, int i, int j, int k) {
        return null;
    }

    public int a(int i, Random random) {
        return Item.SUGAR_CANE.id;
    }

    public boolean a() {
        return false;
    }

    public boolean b() {
        return false;
    }
}
