package org.bukkit.craftbukkit.util;

import java.util.BitSet;

public class TickQueue {
    private BitSet blockCache = new BitSet(32768);
    private BitSet columnCache = new BitSet(256);
    private LongHeap heap = new LongHeap();

    /**
     * Each long has on the inside:
     * 32bit time
     *  1bit isColumn
     *  4bit x
     *  4bit z
     *  7bit y
     * 16bit data
     */

    public synchronized void insert(long entry) {
        int coord = (int) ((entry >> 16) & 0xFFFF);

        BitSet cache = blockCache;
        int cacheEntry = coord & 0x7FFF;

        // isColumn
        if (((entry >> 31) & 0x1) == 1) {
            cache = columnCache;
            cacheEntry = (coord >> 7) & 0xFF;
        }

        if (cache.get(cacheEntry)) {
            heap.removeByCoord(coord);
        } else {
            cache.set(cacheEntry);
        }

        heap.insert(entry);
    }

    public synchronized long pop() {
        long entry = heap.popHead();

        // isColumn
        if (((entry >> 31) & 0x1) == 1) {
            columnCache.clear((int) ((entry >> 7) & 0xFF));
        } else {
            blockCache.clear((int) ((entry >> 16) & 0x7FFF));
        }

        return entry;
    }

    public long peek() {
        if (heap.isEmpty()) return Long.MAX_VALUE;
        return heap.peekHead();
    }
}
