package net.minecraft.server;

public class VillageAgressor { // Craftbukkit - public

    public EntityLiving a;
    public int b;

    public final Village c; // CraftBukkit - public

    VillageAgressor(Village village, EntityLiving entityliving, int i) {
        this.c = village;
        this.a = entityliving;
        this.b = i;
    }
}
