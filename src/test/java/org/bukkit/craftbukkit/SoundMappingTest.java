package org.bukkit.craftbukkit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.bukkit.Sound;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * This file opens a connection to check for sound availability.
 */
@RunWith(Parameterized.class)
public final class SoundMappingTest {
    private static Set<String> MINECRAFT_SOUNDS;
    private static Map<String, Sound> BUKKIT_SOUNDS;

    public static void processSounds() throws MalformedURLException, ParserConfigurationException, SAXException, IOException {
        final URL url = new URL("https://s3.amazonaws.com/Minecraft.Resources/");
        final NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream()).getElementsByTagName("Contents");

        final Pattern pattern = Pattern.compile("sound/([a-zA-Z/_ ]+)[0-9]*\\.ogg");

        final ImmutableSet.Builder<String> set_builder = ImmutableSet.<String>builder();

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);

            if (node.getNodeType() == 1) {
                final Element element = (Element) node;
                final String key = element.getElementsByTagName("Key").item(0).getChildNodes().item(0).getNodeValue();
                final Matcher matcher = pattern.matcher(key);

                if (matcher.matches()) {
                    final String sound = matcher.group(1).replace('/', '.');
                    set_builder.add(sound);
                }
            }
        }

        final ImmutableMap.Builder<String, Sound> map_builder = ImmutableMap.<String, Sound>builder();

        for (Sound sound : Sound.values()) {
            final String craftsound = CraftSound.getSound(sound);
            if (craftsound != null) {
                map_builder.put(craftsound, sound);
            }
        }

        MINECRAFT_SOUNDS = set_builder.build();
        BUKKIT_SOUNDS = map_builder.build();
    }

    @Parameters(name="Sound[{index}]: {0}")
    public static List<Object[]> data() throws MalformedURLException, MalformedURLException, ParserConfigurationException, SAXException, IOException {
        processSounds();

        final Set<String> data = new HashSet<String>();
        data.addAll(MINECRAFT_SOUNDS);
        data.addAll(BUKKIT_SOUNDS.keySet());

        final List<Object[]> list = new ArrayList<Object[]>(data.size());

        for (String sound : data) {
            list.add(new Object[] {sound});
        }

        Collections.sort(list, new Comparator<Object[]>() {
            @Override
            public int compare(Object[] o1, Object[] o2) {
                return ((String) o1[0]).compareTo((String) o2[0]);
            }
        });

        return list;
    }

    @Parameter(0) public String soundpath;

    @Test
    public void existsInBukkit() {
        Sound bukkitsound = BUKKIT_SOUNDS.get(soundpath);
        assertThat("Sound is not in bukkit", bukkitsound, is(not(nullValue())));
    }

    @Test
    public void existsInMinecraft() {
        if (!soundpath.isEmpty()) {
            assertThat("Sound is no longer live", soundpath, isIn(MINECRAFT_SOUNDS));
        }
    }
}
