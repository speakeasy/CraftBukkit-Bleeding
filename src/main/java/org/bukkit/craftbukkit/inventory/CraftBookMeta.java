package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;

public final class CraftBookMeta extends CraftItemMeta implements BookMeta {
    private String title;
    private String author;
    private List<String> pages = new ArrayList<String>();

    CraftBookMeta() {}

    CraftBookMeta(NBTTagCompound tag) {
        super(tag);
        if (tag.hasKey("title")) {
            this.title = tag.getString("title");
        }

        if (tag.hasKey("author")) {
            this.author = tag.getString("author");
        }

        if (tag.hasKey("pages")) {
            NBTTagList pages = tag.getList("pages");
            for (int i = 0; i < pages.size(); i++) {
                String page = ((NBTTagString) pages.get(i)).data;
                // filter out null and longer (> 256) pages? (Even though it's done by MC and this implementation?)
                this.pages.add(page);
            }
        }
    }

    @Override
    void applyToItem(NBTTagCompound itemData) {
        if (this.title == null) {
            itemData.remove("title");
        } else {
            itemData.setString("title", this.title);
        }

        if (this.author == null) {
            itemData.remove("author");
        } else {
            itemData.setString("author", this.author);
        }

        if (pages.isEmpty()) {
            itemData.remove("pages");
        } else {
            NBTTagList itemPages = new NBTTagList("pages");
            for (int i = 1; i < pages.size() + 1; i++) {
                itemPages.add(new NBTTagString(String.valueOf(i), pages.get(i - 1)));
            }
            itemData.set("pages", itemPages);
        }
    }

    @Override
    boolean isEmpty() {
        return title == null && author == null && pages.isEmpty() && super.isEmpty();
    }

    boolean applicableTo(Material type) {
        switch(type) {
            case BOOK:
            case BOOK_AND_QUILL: return true;
            default: return false;
        }
    }

    public String getTitle() {
        return this.title;
    }

    public boolean setTitle(final String title) {
        if (title == null) {
            this.title = null;
            return true;
        } else if (title.length() > 16) {
            return false;
        }

        this.title = title;
        return true;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getPage(final int page) {
        Validate.isTrue(isValidPage(page), "Invalid page number");
        return pages.get(page - 1);
    }

    public boolean setPage(final int page, final String text) {
        Validate.isTrue(isValidPage(page), "Invalid page number " + page + "/" + pages.size());

        pages.set(page - 1, text == null ? "" : text.length() > 256 ? text.substring(0, 256) : text);
        return true;
    }

    public void setPages(final String... pages) {
        this.pages.clear();

        addPage(pages);
    }

    public void addPage(final String... pages) {
        for (String page : pages) {
            if (page == null) {
                page = "";
            } else if (page.length() > 256) {
                page = page.substring(0, 256);
            }

            this.pages.add(page);
        }
    }

    public int getPageCount() {
        return pages.size();
    }

    public String[] getPages() {
        return pages.toArray(new String[pages.size()]);
    }

    private boolean isValidPage(int page) {
        return page > 0 && page <= pages.size();
    }

    @Override
    public CraftBookMeta clone() {
        CraftBookMeta meta = (CraftBookMeta) super.clone();
        meta.pages = new ArrayList<String>(pages);
        return meta;
    }



    public boolean equals(Object object) {
        if (!super.equals(object)) {
            return false;
        } else if (!(object instanceof CraftBookMeta)) {
            return false;
        }

        CraftBookMeta objectMeta = (CraftBookMeta) object;

        if (this.title == null ? objectMeta.title != null : !this.title.equals(objectMeta.title)) {
            return false;
        }

        if (this.author == null ? objectMeta.author != null : !this.author.equals(objectMeta.author)) {
            return false;
        }

        if (!this.pages.equals(objectMeta.pages)) {
            return false;
        }

        return super.equals(object);
    }
}
