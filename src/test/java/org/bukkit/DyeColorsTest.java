package org.bukkit;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.AchievementList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DyeColorsTest {
    static {
        AchievementList.a();
    }

    @Parameters(name= "{index}: {0}")
    public static List<Object[]> data() {
        List<Object[]> list = new ArrayList<Object[]>();
        for (DyeColor dye : DyeColor.values()) {
            list.add(new Object[] {dye});
        }
        return list;
    }

    @Parameter public DyeColor dye;

    @Test
    public void checkColor() {
        Color color = dye.getColor();
        Color notchColor = null /* TODO, get vanilla version */;
        assertThat(color, is(notchColor));
    }
}
