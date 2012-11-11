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

        if (tag.hasKey(ItemMetaKeys.BOOK_TITLE.nbt)) {
            this.title = tag.getString(ItemMetaKeys.BOOK_TITLE.nbt);
        }

        if (tag.hasKey(ItemMetaKeys.BOOK_AUTHOR.nbt)) {
            this.author = tag.getString(ItemMetaKeys.BOOK_AUTHOR.nbt);
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

        if (map.containsKey(ItemMetaKeys.BOOK_AUTHOR.bukkit)) {
            this.author = (String) map.get(ItemMetaKeys.BOOK_AUTHOR.bukkit);
        }

        if (map.containsKey(ItemMetaKeys.BOOK_TITLE.bukkit)) {
            this.title = (String) map.get(ItemMetaKeys.BOOK_TITLE.bukkit);
        }

        if (map.containsKey(ItemMetaKeys.BOOK_PAGES.bukkit)) {
            this.pages.addAll((List<String>) map.get(ItemMetaKeys.BOOK_PAGES.bukkit));
        }
    }

    @Override
    void applyToItem(NBTTagCompound itemData) {
        if (hasTitle()) {
            itemData.setString(ItemMetaKeys.BOOK_TITLE.nbt, this.title);
        } else {
            itemData.remove(ItemMetaKeys.BOOK_TITLE.nbt);
        }

        if (hasAuthor()) {
            itemData.setString(ItemMetaKeys.BOOK_AUTHOR.nbt, this.author);
        } else {
            itemData.remove(ItemMetaKeys.BOOK_AUTHOR.nbt);
        }

        if (hasPages()) {
            NBTTagList itemPages = new NBTTagList(ItemMetaKeys.BOOK_PAGES.nbt);
            for (int i = 1; i < pages.size() + 1; i++) {
                itemPages.add(new NBTTagString(String.valueOf(i), pages.get(i - 1)));
            }
            itemData.set(ItemMetaKeys.BOOK_PAGES.nbt, itemPages);
        } else {
            itemData.remove(ItemMetaKeys.BOOK_PAGES.nbt);
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
            builder.put(ItemMetaKeys.BOOK_TITLE.bukkit, title);
        }

        if (hasAuthor()) {
            builder.put(ItemMetaKeys.BOOK_AUTHOR.bukkit, author);
        }

        if (hasPages()) {
            builder.put(ItemMetaKeys.BOOK_PAGES.bukkit, pages);
        }

        return builder;
    }

    @Override
    CraftItemFactory.SerializableMeta.Deserializers deserializer() {
        return CraftItemFactory.SerializableMeta.Deserializers.BOOK;
    }
}
