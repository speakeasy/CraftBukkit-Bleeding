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
public class FactoryItemMaterialTests {
    static final ItemFactory factory = CraftItemFactory.instance();
    static final StringBuilder buffer = new StringBuilder();
    static final Material[] materials = Material.values();
    static {
        DummyServer.setup();
    }

    static String name(Enum<?> from, Enum<?> to) {
        if (from.getClass() == to.getClass()) {
            return buffer.delete(0, Integer.MAX_VALUE).append(from.getClass().getName()).append(' ').append(from.name()).append(" to ").append(to.name()).toString();
        }
        return buffer.delete(0, Integer.MAX_VALUE).append(from.getClass().getName()).append('(').append(from.name()).append(") to ").append(to.getClass().getName()).append('(').append(to.name()).append(')').toString();
    }

    @Parameters
    public static List<Object[]> data() {
        List<Object[]> list = new ArrayList<Object[]>();
        for (Material material : materials) {
            list.add(new Object[] {material});
        }
        return list;
    }

    final Material material;

    public FactoryItemMaterialTests(Material material) {
        this.material = material;
    }

    @Test
    public void itemStack() {
        ItemStack bukkitStack = new ItemStack(material);
        CraftItemStack craftStack = CraftItemStack.asCraftCopy(bukkitStack);
        ItemMeta meta = factory.getItemMeta(material);
        if (meta == null) {
            assertThat(material.name(), material, is(Material.AIR));
        } else {
            assertTrue(material.name(), factory.isApplicable(meta, bukkitStack));
            assertTrue(material.name(), factory.isApplicable(meta, craftStack));
        }
    }

    @Test
    public void generalCase() {
        CraftItemMeta meta = (CraftItemMeta) factory.getItemMeta(material);
        if (meta == null) {
            assertThat(material.name(), material, is(Material.AIR));
        } else {
            assertTrue(material.name(), factory.isApplicable(meta, material));
            assertTrue(material.name(), meta.applicableTo(material));

            meta = meta.clone();
            assertTrue(material.name(), factory.isApplicable(meta, material));
            assertTrue(material.name(), meta.applicableTo(material));
        }
    }

    @Test
    public void asMetaFor() {
        final CraftItemMeta baseMeta = (CraftItemMeta) factory.getItemMeta(material);
        if (baseMeta == null) {
            assertThat(material.name(), material, is(Material.AIR));
            return;
        }

        for (Material other : materials) {
            final ItemStack bukkitStack = new ItemStack(other);
            final CraftItemStack craftStack = CraftItemStack.asCraftCopy(bukkitStack);
            final CraftItemMeta otherMeta = (CraftItemMeta) factory.asMetaFor(baseMeta, other);

            final String testName = name(material, other);

            if (otherMeta == null) {
                assertThat(testName, other, is(Material.AIR));
                continue;
            }

            assertTrue(testName, factory.isApplicable(otherMeta, craftStack));
            assertTrue(testName, factory.isApplicable(otherMeta, bukkitStack));
            assertTrue(testName, factory.isApplicable(otherMeta, other));
            assertTrue(testName, otherMeta.applicableTo(other));
        }
    }

    @Test
    public void blankEqualities() {
        if (material == Material.AIR) {
            return;
        }
        final CraftItemMeta baseMeta = (CraftItemMeta) factory.getItemMeta(material);
        final CraftItemMeta baseMetaClone = baseMeta.clone();

        final ItemStack baseMetaStack = new ItemStack(material);
        baseMetaStack.setItemMeta(baseMeta);

        assertThat(material.name(), baseMeta, is(not(sameInstance(baseMetaStack.getItemMeta()))));

        assertTrue(material.name(), factory.equals(baseMeta, null));
        assertTrue(material.name(), factory.equals(null, baseMeta));

        assertTrue(material.name(), factory.equals(baseMeta, baseMetaClone));
        assertTrue(material.name(), factory.equals(baseMetaClone, baseMeta));

        assertThat(material.name(), baseMeta, is(not(sameInstance(baseMetaClone))));

        assertThat(material.name(), baseMeta, is(baseMetaClone));
        assertThat(material.name(), baseMetaClone, is(baseMeta));

        for (Material other : materials) {
            final String testName = name(material, other);

            final CraftItemMeta otherMeta = (CraftItemMeta) factory.asMetaFor(baseMetaClone, other);

            if (otherMeta == null) {
                assertThat(testName, other, is(Material.AIR));
                continue;
            }

            assertTrue(testName, factory.equals(baseMeta, otherMeta));
            assertTrue(testName, factory.equals(otherMeta, baseMeta));

            assertThat(testName, baseMeta, is(otherMeta));
            assertThat(testName, otherMeta, is(baseMeta));

            assertThat(testName, baseMeta.hashCode(), is(otherMeta.hashCode()));
        }
    }
}
