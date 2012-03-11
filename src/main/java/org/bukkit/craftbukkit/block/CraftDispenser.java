package org.bukkit.craftbukkit.block;

import java.util.Random;
import net.minecraft.server.BlockDispenser;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityEgg;
import net.minecraft.server.EntityEnderPearl;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityFishingHook;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPotion;
import net.minecraft.server.EntityProjectile;
import net.minecraft.server.EntitySmallFireball;
import net.minecraft.server.EntitySnowball;
import net.minecraft.server.EntityThrownExpBottle;
import net.minecraft.server.TileEntityDispenser;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftArrow;
import org.bukkit.craftbukkit.entity.CraftFireball;
import org.bukkit.craftbukkit.entity.CraftFish;
import org.bukkit.craftbukkit.entity.CraftProjectile;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Fish;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

public class CraftDispenser extends CraftBlockState implements Dispenser {
    private final CraftWorld world;
    private final TileEntityDispenser dispenser;

    public CraftDispenser(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        dispenser = (TileEntityDispenser) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public Inventory getInventory() {
        return new CraftInventory(dispenser);
    }

    public boolean dispense() {
        Block block = getBlock();

        synchronized (block) {
            if (block.getType() == Material.DISPENSER) {
                BlockDispenser dispense = (BlockDispenser) net.minecraft.server.Block.DISPENSER;

                dispense.dispense(world.getHandle(), getX(), getY(), getZ(), new Random());
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean update(boolean force) {
        boolean result = super.update(force);

        if (result) {
            dispenser.update();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        net.minecraft.server.World world = ((CraftWorld) getWorld()).getHandle();
        net.minecraft.server.Entity launch = null;

        // We want the projectile to appear in front of the dispenser, not literally in it
        BlockFace facing = ((org.bukkit.material.Dispenser)getData()).getFacing();
        int x = getX() + facing.getModX();
        int y = getY() + facing.getModY();
        int z = getZ() + facing.getModZ();

        if (Snowball.class.isAssignableFrom(projectile)) {
            launch = new EntitySnowball(world, x, y, z);
            CraftProjectile.setSourceBlock((EntityProjectile) launch, getLocation());
        } else if (Egg.class.isAssignableFrom(projectile)) {
            launch = new EntityEgg(world, x, y, z);
            CraftProjectile.setSourceBlock((EntityProjectile) launch, getLocation());
        } else if (EnderPearl.class.isAssignableFrom(projectile)) {
            launch = new EntityEnderPearl(world, x, y, z);
            CraftProjectile.setSourceBlock((EntityProjectile) launch, getLocation());
        } else if (Arrow.class.isAssignableFrom(projectile)) {
            launch = new EntityArrow(world, x, y, z);
            CraftArrow.setSourceBlock((EntityArrow) launch, getLocation());
        } else if (Fireball.class.isAssignableFrom(projectile)) {
            if (SmallFireball.class.isAssignableFrom(projectile)) {
                launch = new EntitySmallFireball(world);
            } else {
                launch = new EntityFireball(world);
            }
            CraftFireball.setSourceBlock((EntityFireball) launch, getLocation());

            launch.setPosition(x, y, z);
            Vector direction = getLocation().getDirection().multiply(10);
            ((EntityFireball) launch).setDirection(direction.getX(), direction.getY(), direction.getZ());
        } else if (ThrownPotion.class.isAssignableFrom(projectile)) {
            launch = new EntityPotion(world, x, y, z, 0);
            CraftProjectile.setSourceBlock((EntityProjectile) launch, getLocation());
        } else if (ThrownExpBottle.class.isAssignableFrom(projectile)) {
            launch = new EntityThrownExpBottle(world, x, y, z);
            CraftProjectile.setSourceBlock((EntityProjectile) launch, getLocation());
        }

        Validate.notNull(launch, "Projectile not supported");

        world.addEntity(launch);
        return (T) launch.getBukkitEntity();
    }
}
