package org.example.models;

/**
 * Model class representing a Hospital item for ComboBox usage
 */
public class HospitalItem {
    private final int id;
    private final String name;

    public HospitalItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
