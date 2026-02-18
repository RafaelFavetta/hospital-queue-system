package com.rafaelfavetta.queuesystem.domain.valueObjects;

public record Name(String name) {

    public Name {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
    }

    public boolean isEmpty() {
        return name.isBlank();
    }
}

