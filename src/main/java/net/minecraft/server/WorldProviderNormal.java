package net.minecraft.server;

public class WorldProviderNormal extends WorldProvider {

    public WorldProviderNormal() {}

    // CraftBukkit - Start SEALEVEL
    @Override
    public int getSeaLevel() {
        return 192;
    }
    // CraftBukkit - End
}