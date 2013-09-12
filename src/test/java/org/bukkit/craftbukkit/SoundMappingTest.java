package org.bukkit.craftbukkit;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.bukkit.Sound;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * This file opens a connection to check for sound availability.
 */
public final class SoundMappingTest {
    private static final Set<String> MINECRAFT_SOUNDS = new HashSet<String>();
    private static final Map<String, Sound> BUKKIT_SOUNDS = new HashMap<String, Sound>();

    @BeforeClass
    public static void processSounds() throws MalformedURLException, ParserConfigurationException, SAXException, IOException {
        final URL url = new URL("https://s3.amazonaws.com/Minecraft.Resources/");
        final NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream()).getElementsByTagName("Contents");

        final Pattern pattern = Pattern.compile("sound/([a-zA-Z/_ ]+)[0-9]*\\.ogg");

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);

            if (node.getNodeType() == 1) {
                final Element element = (Element) node;
                final String key = element.getElementsByTagName("Key").item(0).getChildNodes().item(0).getNodeValue();
                final Matcher matcher = pattern.matcher(key);

                if (matcher.matches()) {
                    final String sound = matcher.group(1).replace('/', '.');
                    MINECRAFT_SOUNDS.add(sound);
                }
            }
        }

        for (Sound sound : Sound.values()) {
            BUKKIT_SOUNDS.put(CraftSound.getSound(sound), sound);
        }
    }

    @Parameters(name="Sound[{index}]: {0}<{1}>")
    public static List<Object[]> data() {
        List<Object[]> list = new ArrayList<Object[]>();
        for (Sound sound : Sound.values()) {
            list.add(new Object[] {sound, CraftSound.getSound(sound)});
        }
        return list;
    }

    @Parameter(0) public Sound bukitsound;
    @Parameter(1) public String craftsound;

    @Test
    public void existsInMinecraft() {
        if (!craftsound.isEmpty()) {
            assertThat(MINECRAFT_SOUNDS, contains(craftsound));
        }
    }

    @Test
    public void existsInBukkit() {
        // Assert there are no new sounds
        final List<String> missing_sounds_from_bukkit = new ArrayList<String>(MINECRAFT_SOUNDS);
        missing_sounds_from_bukkit.removeAll(EXISTING_SOUNDS);
        assertEquals("Expected no unmapped sounds in bukkit!", EXPECTED_ARRAY, missing_sounds_from_bukkit);
    }
}
