
package org.bukkit.craftbukkit.util;

import net.minecraft.server.MinecraftServer;

public class ServerShutdownThread extends Thread {
    private final MinecraftServer server;

    public ServerShutdownThread(MinecraftServer server) {
        this.server = server;
        this.setName("Server Shutdown Thread");
    }

    @Override
    public void run() {
        if (!server.isStopped) {
            if (server.isAlive()) {
                server.safeShutdown();
            } else {
                server.stop();
            }
        }
    }
}
