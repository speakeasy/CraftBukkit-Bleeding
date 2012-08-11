package org.bukkit.craftbukkit.util;

import static org.bukkit.craftbukkit.util.Java15Compat.Arrays_copyOf;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class LongHashset extends LongHash {
    long[][][] values = new long[256][][];
    int count = 0;

    public boolean isEmpty() {
        return this.count == 0;
    }

    public int size() {
        return count;
    }

    public void add(int msw, int lsw) {
        add(toLong(msw, lsw));
    }

    public void add(long key) {
        int mainIdx = (int) (key & 255);
        long outer[][] = this.values[mainIdx];
        if (outer == null) this.values[mainIdx] = outer = new long[256][];

        int outerIdx = (int) ((key >> 32) & 255);
        long inner[] = outer[outerIdx];

        if (inner == null) {
            outer[outerIdx] = inner = new long[1];
            inner[0] = key;
            this.count++;
        } else {
            int i;
            for (i = 0; i < inner.length; i++) {
                if (inner[i] == key) {
                    return;
                }
            }
            inner = Arrays_copyOf(inner, i + 1);
            outer[outerIdx] = inner;
            inner[i] = key;
            this.count++;
        }
    }

    public boolean containsKey(long key) {
        long[][] outer = this.values[(int) (key & 255)];
        if (outer == null) return false;

        long[] inner = outer[(int) ((key >> 32) & 255)];
        if (inner == null) return false;

        for (long entry : inner) {
            if (entry == key) return true;
        }
        return false;
    }

    public void remove(long key) {
        long[][] outer = this.values[(int) (key & 255)];
        if (outer == null) return;

        long[] inner = outer[(int) ((key >> 32) & 255)];
        if (inner == null) return;

        int max = inner.length - 1;
        for (int i = 0; i <= max; i++) {
            if (inner[i] == key) {
                this.count--;
                if (i != max) {
                    inner[i] = inner[max];
                }

                outer[(int) ((key >> 32) & 255)] = (max == 0 ? null : Arrays_copyOf(inner, max));
                return;
            }
        }
    }

    public long popFirst() {
        for (long[][] outer: this.values) {
            if (outer == null) continue;

            for (int i = 0; i < outer.length; i++) {
                long[] inner = outer[i];
                if (inner == null || inner.length == 0) continue;

                this.count--;
                long ret = inner[inner.length - 1];
                outer[i] = Arrays_copyOf(inner, inner.length - 1);

                return ret;
            }
        }
        return 0;
    }

    public long[] popAll() {
        int index = 0;
        long[] ret = new long[this.count];
        for (long[][] outer : this.values) {
            if (outer == null) continue;

            for (int oIdx = outer.length - 1; oIdx >= 0; oIdx--) {
                long[] inner = outer[oIdx];
                if (inner == null) continue;

                for (long entry: inner) {
                    ret[index++] = entry;
                }
                outer[oIdx] = null;
            }
        }
        count = 0;
        return ret;
    }

    public long[] keys() {
        int index = 0;
        long[] ret = new long[this.count];
        for (long[][] outer : this.values) {
            if (outer == null) continue;

            for (long[] inner : outer) {
                if (inner == null) continue;

                for (long entry : inner) {
                    ret[index++] = entry;
                }
            }
        }
        return ret;
    }
}
