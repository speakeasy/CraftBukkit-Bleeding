package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;

import com.google.common.collect.ImmutableMap.Builder;

public final class CraftBookMeta extends CraftItemMeta implements BookMeta {
    static final ItemMetaKey BOOK_TITLE = new ItemMetaKey("title");
    static final ItemMetaKey BOOK_AUTHOR = new ItemMetaKey("author");
    static final ItemMetaKey BOOK_PAGES = new ItemMetaKey("pages");

    private String title;
    private String author;
    private List<String> pages = new ArrayList<String>();

    CraftBookMeta(CraftItemMeta meta) {
        super(meta);

        if (!(meta instanceof CraftBookMeta)) {
            return;
        }
        CraftBookMeta bookMeta = (CraftBookMeta) meta;
        this.title = bookMeta.title;
        this.author = bookMeta.author;
        pages.addAll(bookMeta.pages);
    }

    CraftBookMeta(NBTTagCompound tag) {
        super(tag);

        if (tag.hasKey(BOOK_TITLE.NBT)) {
            this.title = tag.getString(BOOK_TITLE.NBT);
        }

        if (tag.hasKey(BOOK_AUTHOR.NBT)) {
            this.author = tag.getString(BOOK_AUTHOR.NBT);
        }

        if (tag.hasKey("pages")) {
            NBTTagList pages = tag.getList("pages");
            String[] pageArray = new String[pages.size()];

            for (int i = 0; i < pages.size(); i++) {
                String page = ((NBTTagString) pages.get(i)).data;
                pageArray[i] = page;
            }

            addPage(pageArray);
        }
    }

    CraftBookMeta(Map<String, Object> map) {
        super(map);

        if (map.containsKey(BOOK_AUTHOR.BUKKIT)) {
            this.author = (String) map.get(BOOK_AUTHOR.BUKKIT);
        }

        if (map.containsKey(BOOK_TITLE.BUKKIT)) {
            this.title = (String) map.get(BOOK_TITLE.BUKKIT);
        }

        if (map.containsKey(BOOK_PAGES.BUKKIT)) {
            this.pages.addAll((List<String>) map.get(BOOK_PAGES.BUKKIT));
        }
    }

    @Override
    void applyToItem(NBTTagCompound itemData) {
        if (hasTitle()) {
            itemData.setString(BOOK_TITLE.NBT, this.title);
        } else {
            itemData.remove(BOOK_TITLE.NBT);
        }

        if (hasAuthor()) {
            itemData.setString(BOOK_AUTHOR.NBT, this.author);
        } else {
            itemData.remove(BOOK_AUTHOR.NBT);
        }

        if (hasPages()) {
            NBTTagList itemPages = new NBTTagList(BOOK_PAGES.NBT);
            for (int i = 1; i < pages.size() + 1; i++) {
                itemPages.add(new NBTTagString(String.valueOf(i), pages.get(i - 1)));
            }
            itemData.set(BOOK_PAGES.NBT, itemPages);
        } else {
            itemData.remove(BOOK_PAGES.NBT);
        }
    }

    @Override
    boolean isEmpty() {
        return !(hasPages() || hasAuthor() || hasTitle()) && super.isEmpty();
    }

    boolean applicableTo(Material type) {
        switch (type) {
        case BOOK:
        case BOOK_AND_QUILL:
            return true;
        default:
            return false;
        }
    }

    boolean hasAuthor() {
        return author != null;
    }

    boolean hasTitle() {
        return title != null;
    }

    boolean hasPages() {
        return !pages.isEmpty();
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

    public List<String> getPages() {
        return ImmutableList.copyOf(pages);
    }

    public void setPages(List<String> pages) {
        this.pages.clear();
        if (pages == null) {
            return;
        }

        addPage(pages.toArray(new String[pages.size()]));
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

        if (!hasTitle() ? objectMeta.title != null : !this.title.equals(objectMeta.title)) {
            return false;
        }

        if (!hasAuthor() ? objectMeta.author != null : !this.author.equals(objectMeta.author)) {
            return false;
        }

        if (!this.pages.equals(objectMeta.pages)) {
            return false;
        }

        return super.equals(object);
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasTitle()) {
            builder.put(BOOK_TITLE.BUKKIT, title);
        }

        if (hasAuthor()) {
            builder.put(BOOK_AUTHOR.BUKKIT, author);
        }

        if (hasPages()) {
            builder.put(BOOK_PAGES.BUKKIT, pages);
        }

        return builder;
    }

    @Override
    CraftItemFactory.SerializableMeta.Deserializers deserializer() {
        return CraftItemFactory.SerializableMeta.Deserializers.BOOK;
    }
}
