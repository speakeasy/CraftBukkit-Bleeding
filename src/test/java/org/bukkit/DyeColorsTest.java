package org.bukkit;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.server.AchievementList;
import net.minecraft.server.EntitySheep;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DyeColorsTest {
    static {
        AchievementList.a();
        try {
        } catch (Throwable t) {
            throw new AssertionError(t);
        }
    }

    @BeforeClass
    public static void printColors() throws Throwable {
        Field[] fields = Color.class.getFields();
        System.out.println("Fields:" + Arrays.toString(fields));
        for (Field field : fields) {
            System.out.println(field.getName() + ": " + field.get(null));
        }
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
        float[] nmsColorArray = EntitySheep.d[dye.getData()];
        Color nmsColor = Color.fromRGB((int) (nmsColorArray[0] * 255), (int) (nmsColorArray[1] * 255), (int) (nmsColorArray[2] * 255));
        assertThat(color, is(nmsColor));
    }
}
