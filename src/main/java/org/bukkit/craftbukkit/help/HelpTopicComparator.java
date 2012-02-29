package org.bukkit.craftbukkit.help;

import java.util.Comparator;

/**
 */
public class HelpTopicComparator implements Comparator<String> {
    public int compare(String lhs, String rhs) {
        if (lhs.startsWith("/") && !rhs.startsWith("/")) {
            return 1;
        } else if (!lhs.startsWith("/") && rhs.startsWith("/")) {
            return -1;
        } else {
            return lhs.compareToIgnoreCase(rhs);
        }
    }
}
