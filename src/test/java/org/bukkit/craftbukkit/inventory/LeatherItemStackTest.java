package org.bukkit.craftbukkit.inventory;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Joiner;

@RunWith(Parameterized.class)
public class LeatherItemStackTest extends ItemStackTests {

    @Parameters(name="[{index}]:{" + NAME_PARAMETER + "}")
    public static List<Object[]> data() {
        return StackProvider.compound(operaters(), "%s %s", NAME_PARAMETER, Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.LEATHER_LEGGINGS);
    }

    static List<Object[]> operaters() {
        return CompoundOperater.compound(
            Joiner.on('+'),
            NAME_PARAMETER,
            Long.parseLong("10", 2),
            ItemStackLoreEnchantmentTest.operaters(),
            Arrays.asList(
                new Object[] {
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                            return cleanStack;
                        }
                    },
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Do other LEATHER stuff
                            return cleanStack;
                        }
                    },
                    "LEATHER1"
                },
                new Object[] {
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Do LEATHER stuff
                            return cleanStack;
                        }
                    },
                    new Operater() {
                        public ItemStack operate(ItemStack cleanStack) {
                            // TODO Do other LEATHER stuff
                            return cleanStack;
                        }
                    },
                    "LEATHER2"
                }
            )
        );
    }
}
