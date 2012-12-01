package org.bukkit.craftbukkit.inventory;

import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ItemStackLoreEnchantmentTest extends ItemStackTests {

    @Parameters(name="[{index}]:{" + NAME_PARAMETER + "}")
    public static List<Object[]> data() {
        return StackProvider.compound(operaters(), "%s %s", NAME_PARAMETER, ItemStackTests.COMPOUND_MATERIALS);
    }

    static List<Object[]> operaters() {
        return CompoundOperater.compound(
            "%s+%s",
            NAME_PARAMETER,
            ~0l,
            Arrays.asList(
                new Object[] {
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add Lore
                            return cleanStack;
                        }
                    },
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add other lore
                            return cleanStack;
                        }
                    },
                    "Lore1"
                },
                new Object[] {
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add name
                            return cleanStack;
                        }
                    },
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add other name
                            return cleanStack;
                        }
                    },
                    "Name1"
                },
                new Object[] {
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add name / lore
                            return cleanStack;
                        }
                    },
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add other name / lore
                            return cleanStack;
                        }
                    },
                    "LoreAndName1"
                }
            ),
            Arrays.asList(
                new Object[] {
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add enchantment
                            return cleanStack;
                        }
                    },
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add other enchantment
                            return cleanStack;
                        }
                    },
                    "Enchant1"
                },
                new Object[] {
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add enchantment
                            return cleanStack;
                        }
                    },
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add other enchantment
                            return cleanStack;
                        }
                    },
                    "Enchant2"
                }
            )
        );
    }
}
