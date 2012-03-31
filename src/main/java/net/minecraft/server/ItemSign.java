package net.minecraft.server;

public class ItemSign extends Item {

    public ItemSign(int i) {
        super(i);
        this.maxStackSize = 1;
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
        if (l == 0) {
            return false;
        } else if (!world.getMaterial(i, j, k).isBuildable()) {
            return false;
        } else {
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

            if (!entityhuman.d(i, j, k)) {
                return false;
            } else if (!Block.SIGN_POST.canPlace(world, i, j, k)) {
                return false;
            } else {
                /* CraftBukkit start - Delegate to Event Factory
                if (l == 1) {
                    int i1 = MathHelper.floor((double) ((entityhuman.yaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    world.setTypeIdAndData(i, j, k, Block.SIGN_POST.id, i1);
                } else {
                    world.setTypeIdAndData(i, j, k, Block.WALL_SIGN.id, l);
                }
                // */
                // id and data must match above values
                int id = l == 1 ? Block.SIGN_POST.id : Block.WALL_SIGN.id;
                int data = l == 1 ? MathHelper.floor((double) ((entityhuman.yaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15 : l;

                if (!org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockPlace(world, entityhuman, i, j, k, id, data)) {
                    return false;
                }
                // CraftBukkit end

                --itemstack.count;
                TileEntitySign tileentitysign = (TileEntitySign) world.getTileEntity(i, j, k);

                if (tileentitysign != null) {
                    entityhuman.a(tileentitysign);
                }

                return true;
            }
        }
    }
}
