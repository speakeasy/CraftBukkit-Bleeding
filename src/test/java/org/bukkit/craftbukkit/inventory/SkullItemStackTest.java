package org.bukkit.craftbukkit.inventory;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.ItemStackTests.Operater;
import org.bukkit.craftbukkit.inventory.ItemStackTests.StackProvider;
import org.bukkit.inventory.ItemStack;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SkullItemStackTest extends ItemStackTests {

    @Parameters(name="[{index}]:{" + NAME_PARAMETER + "}")
    public static List<Object[]> data() {
        return StackProvider.compound(operaters(), "%s %s", NAME_PARAMETER, Material.SKULL, Material.SKULL_ITEM);
    }

    static List<Object[]> operaters() {
        return Arrays.asList(
            new Object[] {
                new Operater() {
                    public ItemStack operate(ItemStack cleanStack) {
                        // TODO do skull stuff
                        return cleanStack;
                    }
                },
                new Operater() {
                    public ItemStack operate(ItemStack cleanStack) {
                        // TODO Do other skull stuff
                        return cleanStack;
                    }
                },
                "Skull1"
            },
            new Object[] {
                new Operater() {
                    public ItemStack operate(ItemStack cleanStack) {
                        // TODO Do skull stuff
                        return cleanStack;
                    }
                },
                new Operater() {
                    public ItemStack operate(ItemStack cleanStack) {
                        // TODO Do other skull stuff
                        return cleanStack;
                    }
                },
                "Skull2"
            }
        );
    }
}
