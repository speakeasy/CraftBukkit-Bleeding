package org.bukkit.craftbukkit.inventory;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DummyServer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ApplicableItemTest {
    static final ItemFactory factory = CraftItemFactory.instance();
    static {
        DummyServer.setup();
    }

    @Parameters
    public static List<Object[]> data() {
        List<Object[]> list = new ArrayList<Object[]>();
        for (Material material : Material.values()) {
            list.add(new Object[] {material});
        }
        return list;
    }

    final Material material;

    public ApplicableItemTest(Material material) {
        super();
        this.material = material;
    }

    @Test
    public void craftItemStack() {
        CraftItemStack stack = CraftItemStack.asCraftCopy(new ItemStack(material));
        ItemMeta meta = factory.getItemMeta(material);
        if (meta == null) {
            assertThat(material, is(Material.AIR));
        } else {
            assertTrue(factory.isApplicable(meta, stack));
        }
    }

    @Test
    public void generalCase() {
        ItemMeta meta = factory.getItemMeta(material);
        if (meta == null) {
            assertThat(material, is(Material.AIR));
        } else {
            assertTrue(factory.isApplicable(meta, material));
        }
    }

}
