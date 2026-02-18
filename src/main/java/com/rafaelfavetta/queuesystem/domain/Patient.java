package com.rafaelfavetta.queuesystem.domain;

import com.rafaelfavetta.queuesystem.domain.valueObjects.Age;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Name;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Ulid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

    public boolean isElderly() {
        return age.age() >= 60;
    }
}
