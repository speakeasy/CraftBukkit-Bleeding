package org.bukkit.craftbukkit.inventory;

import java.util.List;

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
        return CompoundOperater.compound("%s+%s", NAME_PARAMETER, 0l, ItemStackLoreTest.operaters(), ItemStackEnchantmentTest.operaters());
    }
}
