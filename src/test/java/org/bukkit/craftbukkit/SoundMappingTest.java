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
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * This file opens a connection to check for sound availability.
 */
public final class SoundMappingTest {
    private static final List<String> EXPECTED_ARRAY = new ArrayList<String>();
    private static final Pattern SOUND_PATTERN = Pattern.compile("sound/([a-zA-Z/_ ]+)[0-9]*\\.ogg");
    private static final List<String> LIVE_SOUNDS = new ArrayList<String>();
    private static final List<String> EXISTING_SOUNDS = new ArrayList<String>();

    @BeforeClass
    public static void processSounds() throws MalformedURLException, ParserConfigurationException, SAXException, IOException {
        final URL url = new URL("https://s3.amazonaws.com/Minecraft.Resources/");
        final NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream()).getElementsByTagName("Contents");

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);

            if (node.getNodeType() == 1) {
                final Element element = (Element) node;
                final String key = element.getElementsByTagName("Key").item(0).getChildNodes().item(0).getNodeValue();
                final Matcher matcher = SOUND_PATTERN.matcher(key);

                if (matcher.matches()) {
                    final String sound = matcher.group(1).replace('/', '.');
                    if (!LIVE_SOUNDS.contains(sound)) {
                        LIVE_SOUNDS.add(sound);
                    }
                }
            }
        }

        for (Sound sound : Sound.values()) {
            EXISTING_SOUNDS.add(CraftSound.getSound(sound));
        }
    }

    @Test
    public void allSoundsExistInMinecraft() {
        // Assert all current sounds are live
        final List<String> removed_sounds_from_minecraft = new ArrayList<String>(EXISTING_SOUNDS);
        removed_sounds_from_minecraft.removeAll(LIVE_SOUNDS);
        assertEquals("Expected all bukkit sounds to exist!", EXPECTED_ARRAY, removed_sounds_from_minecraft);
    }

    @Test
    public void allSoundsExistInBukkit() {
        // Assert there are no new sounds
        final List<String> missing_sounds_from_bukkit = new ArrayList<String>(LIVE_SOUNDS);
        missing_sounds_from_bukkit.removeAll(EXISTING_SOUNDS);
        assertEquals("Expected no unmapped sounds in bukkit!", EXPECTED_ARRAY, missing_sounds_from_bukkit);
    }
}
