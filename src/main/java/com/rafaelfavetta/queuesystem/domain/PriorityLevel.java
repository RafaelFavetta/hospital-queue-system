package com.rafaelfavetta.queuesystem.domain;

import lombok.Getter;

@Getter
public enum PriorityLevel {

    LOW(1),
    MEDIUM(2),
    HIGH(3),
    EXTREME(4);

    private final int level;

    PriorityLevel(int level) {
        this.level = level;
    }

}
