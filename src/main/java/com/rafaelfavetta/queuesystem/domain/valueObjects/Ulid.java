package com.rafaelfavetta.queuesystem.domain.valueObjects;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
public final class Ulid {

    private final String ulid;

    public Ulid(String ulid) {
        if (ulid == null || ulid.isBlank()) {
            throw new IllegalArgumentException("ULID cannot be null or empty");
        }
        this.ulid = ulid;
    }

    public static Ulid generate() {
        return new Ulid(UlidCreator.getUlid().toString());
    }

    public static Ulid of(String ulid) {
        if (ulid == null || ulid.isBlank()) {
            throw new IllegalArgumentException("ULID cannot be null or empty");
        }

        try {
            com.github.f4b6a3.ulid.Ulid.from(ulid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ULID format");
        }

        return new Ulid(ulid);
    }
}