package org.bukkit.craftbukkit;

import org.bukkit.ChunkSnapshot;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.block.CraftBlock;

import net.minecraft.server.BiomeBase;

/**
 * Represents a static, thread-safe snapshot of chunk of blocks
 * Purpose is to allow clean, efficient copy of a chunk data to be made, and then handed off for processing in another thread (e.g. map rendering)
 */
public class CraftChunkSnapshot implements ChunkSnapshot {
    private final int x, z;
    private final String worldname;
    private final byte[][] buf; // Flat buffer in uncompressed chunk section file format, indexed by section
    private final int[] hmap; // Height map
    private final long captureFulltime;
    private final BiomeBase[] biome;
    private final double[] biomeTemp;
    private final double[] biomeRain;
    private final int topNonEmpty;

    private static final int BLOCKDATA_OFF = 16 * 16 * 16;
    private static final int BLOCKLIGHT_OFF = BLOCKDATA_OFF + (16 * 16 * 16 / 2);
    private static final int SKYLIGHT_OFF = BLOCKLIGHT_OFF + (16 * 16 * 16 / 2);

    CraftChunkSnapshot(int x, int z, String wname, long wtime, byte[][] buf, int[] hmap, BiomeBase[] biome, double[] biomeTemp, double[] biomeRain) {
        this.x = x;
        this.z = z;
        this.worldname = wname;
        this.captureFulltime = wtime;
        this.buf = buf;
        this.hmap = hmap;
        this.biome = biome;
        this.biomeTemp = biomeTemp;
        this.biomeRain = biomeRain;
        int top;
        for(top = buf.length - 1; top >= 0; top--) {
            if(buf[top] != null) break;
        }
        topNonEmpty = top;
    }

    public final int getX() {
        return x;
    }

    public final int getZ() {
        return z;
    }

    public final String getWorldName() {
        return worldname;
    }

    public final int getBlockTypeId(int x, int y, int z) {
        byte[] bp = buf[y >> 4];
        if(bp != null) {
            return bp[x << 8 | z << 4 | (y & 0x0F)] & 255;
        }
        return 0;
    }

    public final int getBlockData(int x, int y, int z) {
        byte[] bp = buf[y >> 4];
        if(bp != null) {
            int off = ((x << 7) | (z << 3) | ((y & 0x0F) >> 1)) + BLOCKDATA_OFF;
            return ((y & 1) == 0) ? (bp[off] & 0xF) : ((bp[off] >> 4) & 0xF);
        }
        return 0;
    }

    public final int getBlockSkyLight(int x, int y, int z) {
        byte[] bp = buf[y >> 4];
        if(bp != null) {
            int off = ((x << 7) | (z << 3) | ((y & 0x0F) >> 1)) + SKYLIGHT_OFF;
            return ((y & 1) == 0) ? (bp[off] & 0xF) : ((bp[off] >> 4) & 0xF);
        }
        return 15;
    }

    public final int getBlockEmittedLight(int x, int y, int z) {
        byte[] bp = buf[y >> 4];
        if(bp != null) {
            int off = ((x << 7) | (z << 3) | ((y & 0x0F) >> 1)) + BLOCKLIGHT_OFF;
            return ((y & 1) == 0) ? (bp[off] & 0xF) : ((bp[off] >> 4) & 0xF);
        }
        return 0;
    }

    public final int getHighestBlockYAt(int x, int z) {
        return hmap[z << 4 | x];
    }

    public final Biome getBiome(int x, int z) {
        return CraftBlock.biomeBaseToBiome(biome[z << 4 | x]);
    }

    public final double getRawBiomeTemperature(int x, int z) {
        return biomeTemp[z << 4 | x];
    }

    public final double getRawBiomeRainfall(int x, int z) {
        return biomeRain[z << 4 | x];
    }

    public final long getCaptureFullTime() {
        return captureFulltime;
    }

    public final boolean isSectionEmpty(int sy) {
        return (buf[sy] == null);
    }

    public final int getTopNonEmptySection() {
        return topNonEmpty;
    }
}
