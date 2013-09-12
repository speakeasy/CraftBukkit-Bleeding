package org.bukkit;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.craftbukkit.CraftSound;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SoundTest {
    @Parameters(name="Sound[{index}]: {0}")
    public static List<Object[]> data() {
        final List<Object[]> list = new ArrayList<Object[]>();

        for (Sound sound : Sound.values()) {
            list.add(new Object[] {sound});
        }

        return list;
    }

    @Parameter(0) public Sound sound;

    @Test
    public void hasCraftSound() {
        assertThat(CraftSound.getSound(sound), is(not(nullValue())));
    }
}
