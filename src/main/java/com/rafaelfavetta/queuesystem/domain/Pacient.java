package com.rafaelfavetta.queuesystem.domain;

import com.rafaelfavetta.queuesystem.domain.valueObjects.Name;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Ulid;

public class Pacient {

    private Ulid id;
    private Age age;
    private PriorityLevel priorityLevel;
    private Name name;
}
