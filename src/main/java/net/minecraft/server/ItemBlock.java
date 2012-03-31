package net.minecraft.server;

public class ItemBlock extends Item {

    private int id;

    public ItemBlock(int i) {
        super(i);
        this.id = i + 256;
        this.d(Block.byId[i + 256].a(2));
    }

    public int a() {
        return this.id;
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
        int i1 = world.getTypeId(i, j, k);

        if (i1 == Block.SNOW.id) {
            l = 1;
        } else if (i1 != Block.VINE.id && i1 != Block.LONG_GRASS.id && i1 != Block.DEAD_BUSH.id) {
            if (l == 0) {
                --j;
            }

            if (l == 1) {
                ++j;
            }

            if (l == 2) {
                --k;
            }

            if (l == 3) {
                ++k;
            }

            if (l == 4) {
                --i;
            }

            if (l == 5) {
                ++i;
            }
        }

        if (itemstack.count == 0) {
            return false;
        } else if (!entityhuman.d(i, j, k)) {
            return false;
        } else if (j == 255 && Block.byId[this.id].material.isBuildable()) {
            return false;
        }
        // CraftBukkit start
        Integer id = (l == -1 && itemstack.getItem() instanceof ItemStep) ? Block.DOUBLE_STEP.id : this.id;
        if (id != this.id || world.mayPlace(this.id, i, j, k, false, l)) {
            Block block = Block.byId[id];

            if (org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockPlace(world, entityhuman, i, j, k, id, this.filterData(itemstack.getData()))) {
                if (Block.byId[id] != null) {
                    block = Block.byId[id];
                    block.postPlace(world, i, j, k, l);
                    block.postPlace(world, i, j, k, entityhuman);
                    // CraftBukkit end
                }

                world.makeSound((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), block.stepSound.getName(), (block.stepSound.getVolume1() + 1.0F) / 2.0F, block.stepSound.getVolume2() * 0.8F);
                --itemstack.count;
            }

            return true;
        } else {
            return false;
        }
    }

    public String a(ItemStack itemstack) {
        return Block.byId[this.id].q();
    }

    public String getName() {
        return Block.byId[this.id].q();
    }
}
