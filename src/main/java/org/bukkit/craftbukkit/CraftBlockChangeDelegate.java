package org.bukkit.craftbukkit;

import net.minecraft.server.Block;
import net.minecraft.server.World;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.material.MaterialData;

public class CraftBlockChangeDelegate {
    private final BlockChangeDelegate delegate;

    public CraftBlockChangeDelegate(BlockChangeDelegate delegate) {
        this.delegate = delegate;
    }

    public BlockChangeDelegate getDelegate() {
        return delegate;
    }

    public Block getType(int x, int y, int z) {
        return CraftMagicNumbers.getBlock(this.delegate.getType(x, y, z));
    }

    public void setTypeAndData(int x, int y, int z, Block block, int data, int updateFlag) {
        // Layering violation :(
        if (delegate instanceof World) {
            ((World) delegate).setTypeAndData(x, y, z, block, data, 2);
        } else {
            delegate.setRawTypeAndData(x, y, z, new MaterialData(CraftMagicNumbers.getMaterial(block), (byte) data));
        }
    }

    public boolean isEmpty(int x, int y, int z) {
        return delegate.isEmpty(x, y, z);
    }
}
