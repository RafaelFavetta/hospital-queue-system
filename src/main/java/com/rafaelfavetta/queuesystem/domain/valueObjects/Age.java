package com.rafaelfavetta.queuesystem.domain.valueObjects;

public record Age(int age) {

    public Age {
        if (age < 0 || age > 130) {
            throw new IllegalArgumentException("Age must be between 0 and 130");
        }
    }
}