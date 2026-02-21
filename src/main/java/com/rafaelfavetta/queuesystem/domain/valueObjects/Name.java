package com.rafaelfavetta.queuesystem.domain.valueObjects;

public record Name(String name) {

    private static final String NAME_PATTERN = "^[a-zA-ZÀ-ÿ\\s]+$";

    public Name {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (!name.matches(NAME_PATTERN)) {
            throw new IllegalArgumentException("Name can only contain letters and spaces");
        }
    }

    public boolean isEmpty() {
        return name.isBlank();
    }
}

