package com.rafaelfavetta.queuesystem.domain;

import com.rafaelfavetta.queuesystem.domain.valueObjects.Age;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Name;
import com.rafaelfavetta.queuesystem.domain.valueObjects.Ulid;
import lombok.*;

@ToString
@Getter
public class Patient implements Comparable<Patient> {

    private final Ulid id;

    private Age age;
    private PriorityLevel priorityLevel;
    private Name name;
    @Setter
    private long arrivalOrder;

    @Builder
    public Patient(Age age, PriorityLevel priorityLevel, Name name, long arrivalOrder) {
        this.id = Ulid.generate();
        this.age = age;
        this.priorityLevel = priorityLevel;
        this.name = name;
        this.arrivalOrder = arrivalOrder;
    }


    public boolean isElderly() {
        return age.age() >= 60;
    }

    private int calculatePriority() {
        int priorityScore = priorityLevel.getLevel() * 10;

        if (isElderly()) {
            priorityScore += 5;
        }

        return priorityScore;
    }


    @Override
    public int compareTo(Patient otherPatient) {
        int thisPriority = this.calculatePriority();
        int otherPriority = otherPatient.calculatePriority();

        if (thisPriority != otherPriority) {
            return Integer.compare(otherPriority, thisPriority);
        }

        return Long.compare(this.arrivalOrder, otherPatient.arrivalOrder);
    }
}
