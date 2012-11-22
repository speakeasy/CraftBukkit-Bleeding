package org.bukkit.craftbukkit.inventory;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.DummyServer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableList;

@RunWith(Parameterized.class)
public class ItemStackTests {
    static abstract class StackProvider {
        final Material material;

        StackProvider(Material material) {
            this.material = material;
        }

        ItemStack bukkit() {
            return operate(cleanStack(material, false));
        }

        ItemStack craft() {
            return operate(cleanStack(material, true));
        }

        abstract ItemStack operate(ItemStack cleanStack);

        static ItemStack cleanStack(Material material, boolean craft) {
            final ItemStack stack = new ItemStack(material);
            return craft ? CraftItemStack.asCraftCopy(stack) : stack;
        }

        static List<Object[]> compound(final List<Object[]> parameterList, final String nameFormat, final int nameIndex, final Material...materials) {
            final List<Object[]> out = new ArrayList<Object[]>();
            for (Object[] params : parameterList) {
                final int len = params.length;
                for (final Material material : materials) {
                    final Object[] paramsOut = params.clone();
                    for (int i = 0; i < len; i++) {
                        final Object param = paramsOut[i];
                        if (param instanceof Operater) {
                            final Operater operater = (Operater) param;
                            paramsOut[i] = new StackProvider(material) {
                                @Override
                                ItemStack operate(ItemStack cleanStack) {
                                    return operater.operate(cleanStack);
                                }
                            };
                        }
                    }
                    paramsOut[nameIndex] = String.format(nameFormat, paramsOut[nameIndex], material);
                    out.add(paramsOut);
                }
            }
            return out;
        }
    }

    interface Operater {
        ItemStack operate(ItemStack cleanStack);
    }

    static class CompoundOperater implements Operater {
        static class RecursiveContainer {
            final String format;
            final Object[] strings;
            final int nameParameter;
            final List<Object[]> stack;
            final List<Object[]> out;
            final List<Object[]>[] lists;

            RecursiveContainer(String format, Object[] strings, int nameParameter, List<Object[]> stack, List<Object[]> out, List<Object[]>[] lists) {
                this.format = format;
                this.strings = strings;
                this.nameParameter = nameParameter;
                this.stack = stack;
                this.out = out;
                this.lists = lists;
            }
        }
        final Operater[] operaters;

        CompoundOperater(Operater...operaters) {
            this.operaters = operaters;
        }

        public ItemStack operate(ItemStack cleanStack) {
            for (Operater operater : operaters) {
                operater.operate(cleanStack);
            }
            return cleanStack;
        }


        static List<Object[]> compound(final String format, final int nameParameter, List<Object[]>...lists) {
            final RecursiveContainer methodParams = new RecursiveContainer(format, new Object[lists.length], nameParameter, new ArrayList<Object[]>(lists.length), new ArrayList<Object[]>(), lists);

            recursivelyCompound(methodParams, 0);

            return methodParams.out;
        }

        private static void recursivelyCompound(final RecursiveContainer methodParams, final int level) {
            final List<Object[]> stack = methodParams.stack;

            if (level == methodParams.lists.length) {
                final Object[] firstParams = stack.get(0);
                final int len = firstParams.length;
                final int stackSize = stack.size();
                final Object[] params = new Object[len];

                for (int i = 0; i < len; i++) {
                    final Object firstParam = firstParams[i];

                    if (firstParam instanceof Operater) {
                        final Operater[] operaters = new Operater[stackSize];
                        for (int j = 0; j < stackSize; j++) {
                            operaters[j] = (Operater) stack.get(j)[i];
                        }

                        params[i] = new CompoundOperater(operaters);
                    } else if (i == methodParams.nameParameter) {
                        Object[] strings = methodParams.strings;
                        for (int j = 0; j < stackSize; j++) {
                            strings[j] = stack.get(j)[i];
                        }

                        params[i] = String.format(methodParams.format, strings);
                    } else {
                        params[i] = firstParam;
                    }
                }

                methodParams.out.add(params);
            } else {
                final int marker = stack.size();

                for (final Object[] params : methodParams.lists[level]) {
                    stack.add(params);
                    recursivelyCompound(methodParams, level + 1);
                    stack.remove(marker);
                }
            }
        }
    }

    interface StackWrapper {
        ItemStack stack();
    }

    static class CraftWrapper implements StackWrapper {
        final StackProvider provider;

        CraftWrapper(StackProvider provider) {
            this.provider = provider;
        }

        public ItemStack stack() {
            return provider.craft();
        }
    }

    static class BukkitWrapper implements StackWrapper {
        final StackProvider provider;

        BukkitWrapper(StackProvider provider) {
            this.provider = provider;
        }

        public ItemStack stack() {
            return provider.bukkit();
        }
    }

    static class NoOpProvider extends StackProvider {

        NoOpProvider(Material material) {
            super(material);
        }

        @Override
        ItemStack operate(ItemStack cleanStack) {
            return cleanStack;
        }

    }

    @Parameters(name="[{index}]:{2}")
    public static List<Object[]> data() {
        return ImmutableList.of(); // TODO, test basic durability issues
    }

    static final Material[] COMPOUND_MATERIALS;
    static final int NAME_PARAMETER = 2;
    static {
        DummyServer.setup();

        final ItemFactory factory = CraftItemFactory.instance();
        final Map<Class<? extends ItemMeta>, Material> possibleMaterials = new HashMap<Class<? extends ItemMeta>, Material>();
        for (final Material material : Material.values()) {
            final ItemMeta meta = factory.getItemMeta(material);
            if (meta == null || possibleMaterials.containsKey(meta.getClass()))
                continue;
            possibleMaterials.put(meta.getClass(), material);

        }
        COMPOUND_MATERIALS = possibleMaterials.values().toArray(new Material[possibleMaterials.size()]);
    }

    @Parameter(0) public StackProvider provider;
    @Parameter(1) public StackProvider unequalProvider;
    @Parameter(NAME_PARAMETER) public String name;

    @Test
    public void testBukkitInequality() {
        final StackWrapper bukkitWrapper = new CraftWrapper(provider);
        testInequality(bukkitWrapper, new BukkitWrapper(unequalProvider));
        testInequality(bukkitWrapper, new BukkitWrapper(new NoOpProvider(provider.material)));
    }

    @Test
    public void testCraftInequality() {
        final StackWrapper craftWrapper = new CraftWrapper(provider);
        testInequality(craftWrapper, new CraftWrapper(unequalProvider));
        testInequality(craftWrapper, new CraftWrapper(new NoOpProvider(provider.material)));
    }

    @Test
    public void testMixedInequality() {
        final StackWrapper craftWrapper = new CraftWrapper(provider);
        testInequality(craftWrapper, new BukkitWrapper(unequalProvider));
        testInequality(craftWrapper, new BukkitWrapper(new NoOpProvider(provider.material)));

        final StackWrapper bukkitWrapper = new CraftWrapper(provider);
        testInequality(bukkitWrapper, new CraftWrapper(unequalProvider));
        testInequality(bukkitWrapper, new CraftWrapper(new NoOpProvider(provider.material)));
    }

    static void testInequality(StackWrapper provider, StackWrapper unequalProvider) {
        final ItemStack stack = provider.stack();
        assertThat(stack, is(stack));

        final ItemStack unequalStack = unequalProvider.stack();
        assertThat(unequalStack, is(unequalStack));

        assertThat(stack, is(not(sameInstance(provider.stack()))));
        assertThat(stack, is(provider.stack()));
        assertThat(stack, is(not(unequalStack)));

        final ItemStack newStack = new ItemStack(stack);
        assertThat(newStack, is(stack));
        assertThat(newStack, is(not(unequalStack)));
        assertThat(newStack.getItemMeta(), is(stack.getItemMeta()));
        assertThat(newStack.getItemMeta(), is(not(unequalStack.getItemMeta())));

        final ItemStack craftStack = CraftItemStack.asCraftCopy(stack);
        assertThat(craftStack, is(stack));
        assertThat(craftStack, is(not(unequalStack)));
        assertThat(craftStack.getItemMeta(), is(stack.getItemMeta()));
        assertThat(craftStack.getItemMeta(), is(not(unequalStack.getItemMeta())));
    }

    @Test
    public void testDeserialize() {
        fail(); // TODO
    }
}
