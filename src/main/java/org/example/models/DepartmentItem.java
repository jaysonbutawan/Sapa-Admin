package org.example.models;

/**
 * Model class representing a Department item for ComboBox usage
 */
public class DepartmentItem {
    private final int id;
    private final String displayName;

    public DepartmentItem(int id, String displayName) {
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
