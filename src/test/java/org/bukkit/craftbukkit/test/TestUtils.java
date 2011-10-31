package org.bukkit.craftbukkit.test;

import joptsimple.OptionSet;
import net.minecraft.server.MinecraftServer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.Main;

public class TestUtils {
    private static CraftServer server;
    private static OptionSet defaultOptions;
    private static String[] defaultArgs = {"-log-append", "false"};

    public static CraftServer startServer() throws InterruptedException {
        if (server == null) {
            if (defaultOptions == null) {
                defaultOptions = Main.defaultOptionParser().parse(defaultArgs);
            }
            server = MinecraftServer.createTestServer(defaultOptions);
        }
        return server;
    }

    public static void shutdownServer(CraftServer server) {
        server.shutdown();
    }
}
