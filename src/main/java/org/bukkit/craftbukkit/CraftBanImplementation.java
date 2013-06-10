package org.bukkit.craftbukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import org.bukkit.bans.Ban;
import org.bukkit.bans.BanImplementation;
import org.bukkit.bans.BanList;

public class CraftBanImplementation implements BanImplementation {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private File file;

    public CraftBanImplementation(File file) {
        this.file = file;
    }

    public void save(BanList list) {
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.file, false));

            for (Ban banentry : list.getEntries()) {
                StringBuilder stringbuilder = new StringBuilder();

                stringbuilder.append(banentry.getName());
                stringbuilder.append("|");
                stringbuilder.append(DATE_FORMAT.format(banentry.getCreated()));
                stringbuilder.append("|");
                stringbuilder.append(banentry.getSource());
                stringbuilder.append("|");
                stringbuilder.append(banentry.getExpires() == null ? "Forever" : DATE_FORMAT.format(banentry.getExpires()));
                stringbuilder.append("|");
                stringbuilder.append(banentry.getReason());
                printwriter.println(stringbuilder.toString());
            }
            printwriter.close();
        } catch (IOException ioexception) {
            // OH DEAR
        }
    }
}
