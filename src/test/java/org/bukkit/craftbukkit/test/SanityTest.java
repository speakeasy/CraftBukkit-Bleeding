package org.bukkit.craftbukkit.test;

import org.bukkit.craftbukkit.CraftServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SanityTest extends CraftBukkitTest{

    @Test
    public void testGetName() {
        assertEquals(server.getName(), "Craftbukkit");
    }

}
