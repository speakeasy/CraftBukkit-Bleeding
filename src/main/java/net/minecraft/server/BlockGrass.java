package net.minecraft.server;

import java.util.Random;

public class BlockGrass extends Block {

    protected BlockGrass(int i) {
        super(i, Material.GRASS);
        this.textureId = 3;
        this.a(true);
    }

    public void a(World world, int i, int j, int k, Random random) {
        if (!world.isStatic) {
            if (world.getLightLevel(i, j + 1, k) < 4 && Block.q[world.getTypeId(i, j + 1, k)] > 2) {
                if (random.nextInt(4) != 0) {
                    return;
                }

                world.setTypeId(i, j, k, Block.DIRT.id);
            } else if (world.getLightLevel(i, j + 1, k) >= 9) {
                int l = i + random.nextInt(3) - 1;
                // int i1 = j + random.nextInt(5) - 3; // CraftBukkit
                int j1 = k + random.nextInt(3) - 1;

                // CraftBukkit start
                for (int i1 = j + 1; i1 >= j - 3; i1--) {
                    int k1 = world.getTypeId(l, i1 + 1, j1);
                    if (world.getTypeId(l, i1, j1) == Block.DIRT.id && world.getLightLevel(l, i1 + 1, j1) >= 4 && Block.q[k1] <= 2) {
                        world.setTypeId(l, i1, j1, Block.GRASS.id);
                        this.queueBlockTick(world, l, i1, j1);
                        break;
                    }
                    // CraftBukkit end
                }
            }
        }
        this.queueBlockTick(world, i, j, k); // CraftBukkit
    }

    // CraftBukkit start
    public void queueBlockTick(net.minecraft.server.Chunk chunk, int x, int y, int z) {
        chunk.queueBlockTick(x, y, z, this.id, World.getTicksForChance(1, 5));
    }

    public void c(World world, int x, int y, int z) {
        queueBlockTick(world, x, y, z);
    }
    // CraftBukkit end

    public int a(int i, Random random) {
        return Block.DIRT.a(0, random);
    }
}
