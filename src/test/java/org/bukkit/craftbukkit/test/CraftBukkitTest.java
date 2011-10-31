package org.bukkit.craftbukkit.test;

import org.bukkit.craftbukkit.CraftServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class CraftBukkitTest {
    protected static CraftServer server;

    @BeforeClass
    public static void oneTimeSetUp() throws InterruptedException {
        server = TestUtils.startServer();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        TestUtils.shutdownServer(server);
    }
}
