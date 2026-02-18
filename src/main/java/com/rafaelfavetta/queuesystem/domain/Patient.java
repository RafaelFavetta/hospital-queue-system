package com.rafaelfavetta.queuesystem.domain;

import com.rafaelfavetta.queuesystem.domain.valueObjects.Age;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Name;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Ulid;
import lombok.*;

@ToString
@Builder
@Getter
public class Patient {

    private Ulid id;
    private Age age;
    private PriorityLevel priorityLevel;
    private Name name;
    @Setter
    private long arrivalOrder;

    public Patient(Ulid id, Age age, PriorityLevel priorityLevel, Name name, long arrivalOrder) {
        this.id = id;
        this.age = age;
        this.priorityLevel = priorityLevel;
        this.name = name;
        this.arrivalOrder = arrivalOrder;
    }

    public boolean isElderly() {
        return age.age() >= 60;
    }
}
