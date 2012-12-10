package org.bukkit.craftbukkit.inventory;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.craftbukkit.Overridden;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ItemMetaOverrideTest {
    static final Class parent = CraftItemMeta.class;
    static final Class annotation = Overridden.class;

    static final List<Object[]> testData = new ArrayList<Object[]>();
    static final Method[] methods;

    static final Class[] subclasses = new Class[] {
        CraftBookMeta.class,
        CraftLeatherArmorMeta.class,
        CraftSkullMeta.class,
        CraftPotionMeta.class,
        // CraftMapMeta.class,
    };

    static {
        List<Method> list = new ArrayList<Method>();

        for (Method method: parent.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                list.add(method);
            }
        }

        for (Class clazz : subclasses) {
            for (Method method : list) {
                testData.add(new Object[]{clazz, method, clazz.getSimpleName() + " contains " + method.getName()});
            }
        }

        methods = list.toArray(new Method[list.size()]);
    }

    @Parameters(name="[{index}]:{2}")
    public static List<Object[]> data() {
        return testData;
    }

    @Parameter(0) public Class clazz;
    @Parameter(1) public Method method;
    @Parameter(2) public String name;

    @Test
    public void testClass() {
        try {
            clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
        } catch (Exception ex) {
            fail();
        }
    }
}
