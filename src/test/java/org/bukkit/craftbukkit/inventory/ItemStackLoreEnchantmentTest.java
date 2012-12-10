package org.bukkit.craftbukkit.inventory;

import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Joiner;

@RunWith(Parameterized.class)
public class ItemStackLoreEnchantmentTest extends ItemStackTests {

    @Parameters(name="[{index}]:{" + NAME_PARAMETER + "}")
    public static List<Object[]> data() {
        return StackProvider.compound(operators(), "%s %s", NAME_PARAMETER, ItemStackTests.COMPOUND_MATERIALS);
    }

    static List<Object[]> operators() {
        return CompoundOperator.compound(
            Joiner.on('+'),
            NAME_PARAMETER,
            ~0l,
            Arrays.asList(
                new Object[] {
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add other lore
                            return cleanStack;
                        }
                    },
                    "Lore1"
                },
                new Object[] {
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add name
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add other name
                            return cleanStack;
                        }
                    },
                    "Lore2"
                }
            ),
            Arrays.asList(
                new Object[] {
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add Lore
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add other lore
                            return cleanStack;
                        }
                    },
                    "Name1"
                },
                new Object[] {
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add name
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add other name
                            return cleanStack;
                        }
                    },
                    "Name2"
                }
            ),
            Arrays.asList(
                new Object[] {
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add enchantment
                            return cleanStack;
                        }
                    },
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add other enchantment
                            return cleanStack;
                        }
                    },
                    "Enchant1"
                },
                new Object[] {
                    new Operator() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Add enchantment
                            return cleanStack;
                        }
                    },
                    new Operator() {
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
