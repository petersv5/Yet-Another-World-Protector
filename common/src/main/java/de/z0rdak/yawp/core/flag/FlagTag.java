package de.z0rdak.yawp.core.flag;

import java.util.Arrays;
import java.util.List;

public enum FlagTag {
    PLAYER("player"),
    BLOCK("block"),
    ENTITY("entity"),
    ITEM("item"),
    ENVIRONMENT("environment"),
    HIGH_FREQUENCY("high_frequency"),
    PROTECTION("protection");

    public final String name;

    FlagTag(String name) {
        this.name = name;
    }

    public static FlagTag from(String category) throws IllegalArgumentException {
        List<FlagTag> categories = Arrays.stream(values())
                .filter(flag -> flag.name.equalsIgnoreCase(category))
                .toList();
        if (categories.isEmpty()) {
            throw new IllegalArgumentException("Invalid flag category supplied");
        }
        return categories.get(0);
    }
}