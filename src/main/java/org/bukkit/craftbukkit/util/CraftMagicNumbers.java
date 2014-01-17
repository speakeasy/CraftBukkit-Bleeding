package org.bukkit.craftbukkit.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.server.Block;
import net.minecraft.server.Blocks;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.GroupDataEntity;
import net.minecraft.server.Item;
import net.minecraft.server.MojangsonParser;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.StatisticList;

import org.bukkit.Achievement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.UnsafeValues;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftStatistic;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

@SuppressWarnings("deprecation")
public final class CraftMagicNumbers implements UnsafeValues {
    public static final UnsafeValues INSTANCE = new CraftMagicNumbers();

    private CraftMagicNumbers() {}

    public static Block getBlock(org.bukkit.block.Block block) {
        return getBlock(block.getType());
    }

    @Deprecated
    // A bad method for bad magic.
    public static Block getBlock(int id) {
        return getBlock(Material.getMaterial(id));
    }

    @Deprecated
    // A bad method for bad magic.
    public static int getId(Block block) {
        return Block.b(block);
    }

    public static Material getMaterial(Block block) {
        return Material.getMaterial(Block.b(block));
    }

    public static Item getItem(Material material) {
        // TODO: Don't use ID
        Item item = Item.d(material.getId());
        return item;
    }

    @Deprecated
    // A bad method for bad magic.
    public static Item getItem(int id) {
        return Item.d(id);
    }

    @Deprecated
    // A bad method for bad magic.
    public static int getId(Item item) {
        return Item.b(item);
    }

    public static Material getMaterial(Item item) {
        // TODO: Don't use ID
        Material material = Material.getMaterial(Item.b(item));

        if (material == null) {
            return Material.AIR;
        }

        return material;
    }

    public static Block getBlock(Material material) {
        // TODO: Don't use ID
        Block block = Block.e(material.getId());

        if (block == null) {
            return Blocks.AIR;
        }

        return block;
    }

    @Override
    public Material getMaterialFromInternalName(String name) {
        return getMaterial((Item) Item.REGISTRY.a(name));
    }

    @Override
    public List<String> tabCompleteInternalMaterialName(String token, List<String> completions) {
        return StringUtil.copyPartialMatches(token, Item.REGISTRY.b(), completions);
    }

    @Override
    public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        net.minecraft.server.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

        nmsStack.setTag((NBTTagCompound) MojangsonParser.a(arguments));

        stack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));

        return stack;
    }

    @Override
    public Statistic getStatisticFromInternalName(String name) {
        return CraftStatistic.getBukkitStatisticByName(name);
    }

    @Override
    public Achievement getAchievementFromInternalName(String name) {
        return CraftStatistic.getBukkitAchievementByName(name);
    }

    @Override
    public List<String> tabCompleteInternalStatisticOrAchievementName(String token, List<String> completions) {
        Iterator iterator = StatisticList.b.iterator();
        while (iterator.hasNext()) {
            String statistic = ((net.minecraft.server.Statistic) iterator.next()).e;
            if (statistic.startsWith(token)) {
                completions.add(statistic);
            }
        }
        return completions;
    }

    @Override
    public Entity createEntity(String entityId, World bukkitWorld, double x, double y, double z, String data) throws Exception {
        net.minecraft.server.World world = ((CraftWorld) bukkitWorld).getHandle();
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        if (data != null) {
            NBTBase nbtbase = MojangsonParser.a(data);
            if (nbtbase instanceof NBTTagCompound) {
                nbttagcompound = (NBTTagCompound) nbtbase;
            } else {
                throw new Exception("Not a valid tag");
            }
        }
        nbttagcompound.setString("id", entityId);
        net.minecraft.server.Entity entity = EntityTypes.a(nbttagcompound, world);
        if (entity != null) {
            entity.setPositionRotation(x, y, z, entity.yaw, entity.pitch);
            if (data == null && (entity instanceof EntityInsentient)) {
                ((EntityInsentient) entity).a((GroupDataEntity) null);
            }
            world.addEntity(entity);
            net.minecraft.server.Entity vehicle = entity;
            for (NBTTagCompound nbttagcompound1 = nbttagcompound; nbttagcompound1.hasKeyOfType("Riding", 10); nbttagcompound1 = nbttagcompound1.getCompound("Riding")) {
                net.minecraft.server.Entity passenger = EntityTypes.a(nbttagcompound1.getCompound("Riding"), world);
                if (passenger != null) {
                    passenger.setPositionRotation(x, y, z, passenger.yaw, passenger.pitch);
                    world.addEntity(passenger);
                    vehicle.mount(passenger);
                }
                vehicle = passenger;
            }
            return entity.getBukkitEntity();
        }
        return null;
    }

    @Override
    public List<String> tabCompleteEntityType(String token, List<String> completions) {
        for (String string : ((Set<String>) EntityTypes.b())) {
            if (string.startsWith(token)) {
                completions.add(string);
            }
        }
        return completions;
    }
}
