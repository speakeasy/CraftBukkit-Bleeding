package org.bukkit.craftbukkit;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import net.minecraft.server.ChunkPosition;

import net.minecraft.server.EmptyChunk;
import net.minecraft.server.WorldServer;

import org.bukkit.Chunk;
import org.bukkit.ChunkSectionSnapshot;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Entity;
import org.bukkit.ChunkSnapshot;
import net.minecraft.server.BiomeBase;
import net.minecraft.server.WorldChunkManager;

public class CraftChunk implements Chunk {
    private WeakReference<net.minecraft.server.Chunk> weakChunk;
    private WorldServer worldServer;
    private int x;
    private int z;
    private static byte[] trivialSectionSnapshot = new byte[4096 + 2048 + 2048 + 2048];

    public CraftChunk(net.minecraft.server.Chunk chunk) {
        if (!(chunk instanceof EmptyChunk)) {
            this.weakChunk = new WeakReference<net.minecraft.server.Chunk>(chunk);
        }
        worldServer = (WorldServer) getHandle().world;
        x = getHandle().x;
        z = getHandle().z;
    }

    public World getWorld() {
        return worldServer.getWorld();
    }

    public net.minecraft.server.Chunk getHandle() {
        net.minecraft.server.Chunk c = weakChunk.get();
        if (c == null) {
            c = worldServer.getChunkAt(x, z);
            if (!(c instanceof EmptyChunk)) {
                weakChunk = new WeakReference<net.minecraft.server.Chunk>(c);
            }
        }
        return c;
    }

    void breakLink() {
        weakChunk.clear();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "CraftChunk{" + "x=" + getX() + "z=" + getZ() + '}';
    }

    public Block getBlock(int x, int y, int z) {
        return new CraftBlock(this, (getX() << 4) | (x & 0xF), y & 0x7F, (getZ() << 4) | (z & 0xF));
    }

    public Entity[] getEntities() {
        int count = 0, index = 0;
        net.minecraft.server.Chunk chunk = getHandle();
        for (int i = 0; i < 8; i++) {
            count += chunk.entitySlices[i].size();
        }

        Entity[] entities = new Entity[count];
        for (int i = 0; i < 8; i++) {
            for (Object obj : chunk.entitySlices[i].toArray()) {
                if (!(obj instanceof net.minecraft.server.Entity)) {
                    continue;
                }
                entities[index++] = ((net.minecraft.server.Entity) obj).getBukkitEntity();
            }
        }
        return entities;
    }

    public BlockState[] getTileEntities() {
        int index = 0;
        net.minecraft.server.Chunk chunk = getHandle();
        BlockState[] entities = new BlockState[chunk.tileEntities.size()];
        for (Object obj : chunk.tileEntities.keySet().toArray()) {
            if (!(obj instanceof ChunkPosition)) {
                continue;
            }
            ChunkPosition position = (ChunkPosition) obj;
            entities[index++] = worldServer.getWorld().getBlockAt(position.x + (chunk.x << 4), position.y, position.z + (chunk.z << 4)).getState();
        }
        return entities;
    }

    public boolean isLoaded() {
        return getWorld().isChunkLoaded(this);
    }

    public boolean load() {
        return getWorld().loadChunk(getX(), getZ(), true);
    }

    public boolean load(boolean generate) {
        return getWorld().loadChunk(getX(), getZ(), generate);
    }

    public boolean unload() {
        return getWorld().unloadChunk(getX(), getZ());
    }

    public boolean unload(boolean save) {
        return getWorld().unloadChunk(getX(), getZ(), save);
    }

    public boolean unload(boolean save, boolean safe) {
        return getWorld().unloadChunk(getX(), getZ(), save, safe);
    }

    public ChunkSnapshot getChunkSnapshot() {
        return getChunkSnapshot(true, false, false);
    }
    
    public ChunkSnapshot getChunkSnapshot(boolean includeMaxblocky, boolean includeBiome, boolean includeBiomeTempRain) {
        net.minecraft.server.Chunk chunk = getHandle();

        int[] hmap = null;
        World world = getWorld();

        if (includeMaxblocky) {
            hmap = new int[256]; // Get copy of height map
            for(int i = 0; i < 256; i++) {
                hmap[i] = 0xFF & (int)chunk.heightMap[i];
            }
        }
        /* Now, read sections - see which are empty/trivial */
        int sectionCount = world.getMaxHeight() >> 4;
        byte[][] buf = new byte[sectionCount][];
        boolean[] notempty = new boolean[sectionCount];
        byte[] wrkbuf = null;
        for(int i = 0; i < sectionCount; i++) {
            if(wrkbuf == null)
                wrkbuf = new byte[10240]; // Get big enough buffer for whole section
            chunk.getData(wrkbuf, 0, i << 4, 0, 16, (i << 4) + 16, 16, 0); // Get whole section
            /* Test if trivial section (all zero, except sky light, which is all 15 */
            int j;
            for(j = 0; j < 8192; j++) {
                if(wrkbuf[j] != 0) break;
            }
            if(j == 8192) {
                for(; j < 10240; j++) {
                    if(wrkbuf[j] != (byte)0xFF) break;
                }
            }
            if(j == 10240) {
                buf[i] = trivialSectionSnapshot;
            }
            else {
                buf[i] = wrkbuf;
                wrkbuf = null;
                notempty[i] = true;
            }
        }
        
        BiomeBase[] biome = null;
        double[] biomeTemp = null;
        double[] biomeRain = null;

        if (includeBiome || includeBiomeTempRain) {
            WorldChunkManager wcm = chunk.world.getWorldChunkManager();

            if (includeBiome) {
                biome = new BiomeBase[256];
                wcm.getBiomeBlock(biome, x << 4, z << 4, 16, 16);
            }

            if (includeBiomeTempRain) {
                biomeTemp = new double[256];
                biomeRain = new double[256];
                float[] dat = wcm.getTemperatures((float[]) null, getX() << 4, getZ() << 4, 16, 16);
                for (int i = 0; i < 256; i++)
                    biomeTemp[i] = dat[i];
                dat = wcm.getWetness((float[]) null, getX() << 4, getZ() << 4, 16, 16);
                for (int i = 0; i < 256; i++)
                    biomeRain[i] = dat[i];
            }
        }
        return new CraftChunkSnapshot(getX(), getZ(), world.getName(), world.getFullTime(), buf, notempty, hmap, biome, biomeTemp, biomeRain);
    }

    public static ChunkSnapshot getEmptyChunkSnapshot(int x, int z, CraftWorld world, boolean includeBiome, boolean includeBiomeTempRain) {
        BiomeBase[] biome = null;
        double[] biomeTemp = null;
        double[] biomeRain = null;

        if (includeBiome || includeBiomeTempRain) {
            WorldChunkManager wcm = world.getHandle().getWorldChunkManager();

            if (includeBiome) {
                biome = new BiomeBase[256];
                wcm.getBiomeBlock(biome, x << 4, z << 4, 16, 16);
            }

            if (includeBiomeTempRain) {
                biomeTemp = new double[256];
                biomeRain = new double[256];
                float[] dat = wcm.getTemperatures((float[]) null, x << 4, z << 4, 16, 16);
                for (int i = 0; i < 256; i++)
                    biomeTemp[i] = dat[i];
                dat = wcm.getWetness((float[]) null, x << 4, z << 4, 16, 16);
                for (int i = 0; i < 256; i++)
                    biomeRain[i] = dat[i];
            }
        }
        int sectionCount = world.getMaxHeight() >> 4;
        byte[][] buf = new byte[sectionCount][];
        boolean[] notempty = new boolean[sectionCount];
        for(int i = 0; i < sectionCount; i++)
            buf[i] = trivialSectionSnapshot;
        return new CraftChunkSnapshot(x, z, world.getName(), world.getFullTime(), buf, notempty, 
                new int[16*16], biome, biomeTemp, biomeRain);
    }

    private static class EmptySectionSnapshot implements ChunkSectionSnapshot {
        private final int x, y, z;
        private final String worldName;
        private final long captureFullTime;
        
        EmptySectionSnapshot(int x, int y, int z, String worldName, long captureFullTime) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.worldName = worldName;
            this.captureFullTime = captureFullTime;
        }
        
        public final int getX() {
            return x;
        }

        public final int getY() {
            return y;
        }

        public final int getZ() {
            return z;
        }

        public final String getWorldName() {
            return worldName;
        }

        public final int getBlockTypeId(int x, int y, int z) {
            return 0;
        }

        public int getBlockData(int x, int y, int z) {
            return 0;
        }

        public int getBlockSkyLight(int x, int y, int z) {
            return 15;
        }

        public int getBlockEmittedLight(int x, int y, int z) {
            return 0;
        }

        public boolean isEmpty() {
            return true;
        }

        public long getCaptureFullTime() {
            return captureFullTime;
        }
        
    }
    
    public ChunkSectionSnapshot getChunkSectionSnapshot(int sy) {
        net.minecraft.server.Chunk chunk = getHandle();

        World world = getWorld();
     
        if(isSectionEmpty(sy)) { /* If empty section */
            return new EmptySectionSnapshot(getX(), sy, getZ(), world.getName(), world.getFullTime());
        }
        else {
            byte[] buf = new byte[4096 + 2048 + 2048 + 2048]; // Get big enough buffer for whole section
            chunk.getData(buf, 0, sy << 4, 0, 16, (sy << 4) + 16, 16, 0); // Get whole section
            return new CraftChunkSectionSnapshot(getX(), sy, getZ(), world.getName(), world.getFullTime(), buf);
        }
    }

    public boolean isSectionEmpty(int sy) {
        return (sy > getTopNonEmptySection());
    }

    public int getTopNonEmptySection() {
        net.minecraft.server.Chunk chunk = getHandle();
        int maxy = 0;
        /* Scan for max Y - find which sections are empty */
        for(int i = 0; i < 256; i++) {
            int hmax = 0xFF & (int)chunk.heightMap[i];
            if(hmax > maxy) maxy = hmax;
        }
        maxy = (maxy + 15) >> 4;    /* Round up, and make into section Y coord */

        return (maxy - 1);
    }

    public Biome getBiome(int x, int z) {
        BiomeBase base = worldServer.getWorldChunkManager().getBiome((getX() << 4) + x, (getZ() << 4) + z);

        return CraftBlock.biomeBaseToBiome(base);
    }

    public boolean setBiome(int x, int z, Biome biome) {
        throw new UnsupportedOperationException("Not compatible with 1.1");
    }
    
    static {
        /* Trivial is all zero, except sky light, which is all 15 */
        Arrays.fill(trivialSectionSnapshot, 8192, 8192+2048, (byte)255);
    }

}
