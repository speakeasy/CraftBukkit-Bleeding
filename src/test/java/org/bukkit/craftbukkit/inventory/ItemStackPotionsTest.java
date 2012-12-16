package org.bukkit.craftbukkit.inventory;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ItemStackPotionsTest extends ItemStackTest {

    @Parameters(name="[{index}]:{" + NAME_PARAMETER + "}")
    public static List<Object[]> data() {
        return StackProvider.compound(operators(), "%s %s", NAME_PARAMETER, Material.POTION);
    }

    static List<Object[]> operators() {
        return Arrays.asList(
            new Object[] {
                new Operator() {
                    public ItemStack operate(ItemStack cleanStack) {
                        // TODO do POTION stuff
                        return cleanStack;
                    }
                },
                new Operator() {
                    public ItemStack operate(ItemStack cleanStack) {
                        // TODO Do other POTION stuff
                        return cleanStack;
                    }
                },
                "NoChange"
            },
            new Object[] {
                new Operator() {
                    public ItemStack operate(ItemStack cleanStack) {
                        // TODO Do POTION stuff
                        return cleanStack;
                    }
                },
                new Operator() {
                    public ItemStack operate(ItemStack cleanStack) {
                        // TODO Do other POTION stuff
                        return cleanStack;
                    }
                },
                "POTION2"
            }
        );
    }
}
