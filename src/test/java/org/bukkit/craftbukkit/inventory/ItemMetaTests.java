package org.bukkit.craftbukkit.inventory;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.bukkit.DummyServer;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public class ItemMetaTests {

    static {
        DummyServer.setup();
    }

    @Test
    public void testCrazyEquality() {
        CraftItemStack craft = CraftItemStack.asCraftCopy(new ItemStack(1));
        craft.setItemMeta(craft.getItemMeta());
        ItemStack bukkit = new ItemStack(craft);
        assertThat(craft, is(bukkit));
        assertThat(bukkit, is((ItemStack) craft));
    }
}
