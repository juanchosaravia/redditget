package com.droidcba.redditget.rest.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ItemContainer {

    /**
     * An array of sample (dummy) items.
     */
    public static List<ItemContent> ITEMS = new ArrayList<ItemContent>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, ItemContent> ITEM_MAP = new HashMap<String, ItemContent>();

    static {
        // Add 3 sample items.
        addItem(new ItemContent("1", "Item 1"));
        addItem(new ItemContent("2", "Item 2"));
        addItem(new ItemContent("3", "Item 3"));
    }

    public static void addItem(ItemContent item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class ItemContent {
        public String id;
        public String content;

        public ItemContent(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
