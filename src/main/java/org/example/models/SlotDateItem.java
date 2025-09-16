package org.example.models;

/**
 * Model class representing a Slot Date item for ComboBox usage
 */
public class SlotDateItem {
    private final int id;
    private final String displayName;

    public SlotDateItem(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
