package org.bukkit.craftbukkit.test;

import org.bukkit.craftbukkit.CraftServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.sql.Time;
import java.util.Date;

public class CraftBukkitTest {
    protected static CraftServer server;
    private static long startup;

    @BeforeClass
    public static void oneTimeSetUp() throws InterruptedException {
        server = TestUtils.startServer();
        startup = new Date().getTime();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        try {
            while(new Date().getTime() - startup < 5000) {
                // wait at least a couple of ticks before shutting down
                Thread.sleep(5000);
            }
        } catch (InterruptedException ignored) {
        }
        TestUtils.shutdownServer(server);
    }
}
