package org.bukkit.craftbukkit;

import org.bukkit.ChunkSectionSnapshot;

/**
 * Represents a static, thread-safe snapshot of chunk section (16x16x16) of blocks
 * Purpose is to allow clean, efficient copy of a chunk section data to be made, and then handed off for processing in another thread (e.g. map rendering)
 */

public class CraftChunkSectionSnapshot implements ChunkSectionSnapshot {
    private final int sx;
    private final int sy;
    private final int sz;
    private final String worldname;
    private final byte[] buf;   // Flat buffer in uncompressed chunk file format
    private final long captureFulltime;

    private static final int BLOCKDATA_OFF = 16 * 16 * 16;
    private static final int BLOCKLIGHT_OFF = BLOCKDATA_OFF + (16 * 16 * 16 / 2);
    private static final int SKYLIGHT_OFF = BLOCKLIGHT_OFF + (16 * 16 * 16 / 2);
    
    CraftChunkSectionSnapshot(int x, int y, int z, String wname, long wtime, byte[] buf) {
        this.sx = x;
        this.sy = y;
        this.sz = z;
        this.worldname = wname;
        this.captureFulltime = wtime;
        this.buf = buf;
    }
    
    public final int getX() {
        return sx;
    }

    public final int getY() {
        return sy;
    }

    public final int getZ() {
        return sz;
    }

    public final String getWorldName() {
        return worldname;
    }

    public final int getBlockTypeId(int x, int y, int z) {
        return buf[x << 8 | z << 4 | y] & 255;
    }

    public final int getBlockData(int x, int y, int z) {
        int off = ((x << 8) | (z << 4) | (y >> 1)) + BLOCKDATA_OFF;
        return (buf[off] >> ((y & 1) << 2)) & 0xF;
    }

    public final int getBlockSkyLight(int x, int y, int z) {
        int off = ((x << 10) | (z << 6) | (y >> 1)) + SKYLIGHT_OFF;
        return (buf[off] >> ((y & 1) << 2)) & 0xF;
    }

    public final int getBlockEmittedLight(int x, int y, int z) {
        int off = ((x << 10) | (z << 6) | (y >> 1)) + BLOCKLIGHT_OFF;
        return (buf[off] >> ((y & 1) << 2)) & 0xF;
    }

    public final boolean isEmpty() {
        return false;
    }

    public final long getCaptureFullTime() {
        return captureFulltime;
    }
}
