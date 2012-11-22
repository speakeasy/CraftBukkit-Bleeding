package org.bukkit.craftbukkit.inventory;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BookItemStackTest extends ItemStackTests {

    @Parameters(name="[{index}]:{" + NAME_PARAMETER + "}")
    public static List<Object[]> data() {
        return StackProvider.compound(operaters(), "%s %s", NAME_PARAMETER, Material.BOOK, Material.BOOK_AND_QUILL);
    }

    static List<Object[]> operaters() {
        return Arrays.asList(
            new Object[] {
                new Operater() {
                    public ItemStack operate(ItemStack cleanStack) {
                        // TODO do BOOK stuff
                        return cleanStack;
                    }
                },
                new Operater() {
                    public ItemStack operate(ItemStack cleanStack) {
                        // TODO Do other BOOK stuff
                        return cleanStack;
                    }
                },
                "BOOK1"
            },
            new Object[] {
                new Operater() {
                    public ItemStack operate(ItemStack cleanStack) {
                        // TODO Do BOOK stuff
                        return cleanStack;
                    }
                },
                new Operater() {
                    public ItemStack operate(ItemStack cleanStack) {
                        // TODO Do other BOOK stuff
                        return cleanStack;
                    }
                },
                "BOOK2"
            }
        );
    }
}
