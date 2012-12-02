package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftItemMeta.SerializableMeta;
import org.bukkit.inventory.meta.BookMeta;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap.Builder;

@DelegateDeserialization(SerializableMeta.class)
final class CraftBookMeta extends CraftItemMeta implements BookMeta {
    static final ItemMetaKey BOOK_TITLE = new ItemMetaKey("title");
    static final ItemMetaKey BOOK_AUTHOR = new ItemMetaKey("author");
    static final ItemMetaKey BOOK_PAGES = new ItemMetaKey("pages");
    static final int MAX_PAGE_LENGTH = 256;
    static final int MAX_TITLE_LENGTH = 16;

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

        if (tag.hasKey(BOOK_PAGES.NBT)) {
            NBTTagList pages = tag.getList(BOOK_PAGES.NBT);
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

        setAuthor(SerializableMeta.getString(map, BOOK_AUTHOR.BUKKIT, true));

        setTitle(SerializableMeta.getString(map, BOOK_TITLE.BUKKIT, true));

        Collection<?> pages = SerializableMeta.getObject(Collection.class, map, BOOK_PAGES.BUKKIT, true);
        safelyAddPages(pages);
    }

    @Override
    void applyToItem(NBTTagCompound itemData) {
        super.applyToItem(itemData);
        if (hasTitle()) {
            itemData.setString(BOOK_TITLE.NBT, this.title);
        } else {
            itemData.o(BOOK_TITLE.NBT);
        }

        if (hasAuthor()) {
            itemData.setString(BOOK_AUTHOR.NBT, this.author);
        } else {
            itemData.o(BOOK_AUTHOR.NBT);
        }

        if (hasPages()) {
            NBTTagList itemPages = new NBTTagList(BOOK_PAGES.NBT);
            for (int i = 1; i < pages.size() + 1; i++) {
                itemPages.add(new NBTTagString(String.valueOf(i), pages.get(i - 1)));
            }
            itemData.set(BOOK_PAGES.NBT, itemPages);
        } else {
            itemData.o(BOOK_PAGES.NBT);
        }
    }

    @Override
    boolean hasExtraData() {
        return super.hasExtraData() || hasPages() || hasAuthor() || hasTitle();
    }

    @Override
    boolean applicableTo(Material type) {
        switch (type) {
        case WRITTEN_BOOK:
        case BOOK_AND_QUILL:
            return true;
        default:
            return false;
        }
    }

    public boolean hasAuthor() {
        return !Strings.isNullOrEmpty(author);
    }

    public boolean hasTitle() {
        return !Strings.isNullOrEmpty(title);
    }

    public boolean hasPages() {
        return !pages.isEmpty();
    }

    public String getTitle() {
        return this.title;
    }

    public boolean setTitle(final String title) {
        if (title == null) {
            this.title = null;
            return true;
        } else if (title.length() > MAX_TITLE_LENGTH) {
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
        if (!isValidPage(page)) {
            throw new IllegalArgumentException("Invalid page number " + page + "/" + pages.size());
        }

        pages.set(page - 1, text == null ? "" : text.length() > MAX_PAGE_LENGTH ? text.substring(0, MAX_PAGE_LENGTH) : text);
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
            } else if (page.length() > MAX_PAGE_LENGTH) {
                page = page.substring(0, MAX_PAGE_LENGTH);
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
        safelyAddPages(pages);
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

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (hasTitle()) {
            hash = 61 * hash + this.title.hashCode();
        }
        if (hasAuthor()) {
            hash = 61 * hash + 13 * this.author.hashCode();
        }
        if (hasPages()) {
            hash = 61 * hash + 17 * this.pages.hashCode();
        }
        return original != hash ? CraftBookMeta.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(CraftItemMeta meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftBookMeta) {
            CraftBookMeta that = (CraftBookMeta) meta;

            return (hasTitle() ? that.hasTitle() && this.title.equals(that.title) : !that.hasTitle())
                    && (hasAuthor() ? that.hasAuthor() && this.author.equals(that.author) : !that.hasAuthor())
                    && (hasPages() ? that.hasPages() && this.pages.equals(that.pages) : !that.hasPages());
        }
        return true;
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
    SerializableMeta.Deserializers deserializer() {
        return SerializableMeta.Deserializers.BOOK;
    }

    private void safelyAddPages(Collection<?> collection) {
        if (collection == null) {
            return;
        }

        for (Object object : collection) {
            if (!(object instanceof String)) {
                if (object != null) {
                    throw new IllegalArgumentException(collection + " cannot contain non-string " + object.getClass().getName());
                }

                this.pages.add("");
            } else {
                String page = object.toString();

                if (page.length() > MAX_PAGE_LENGTH) {
                    page = page.substring(0, MAX_PAGE_LENGTH);
                }

                this.pages.add(page);
            }
        }
    }
}
