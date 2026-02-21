package com.rafaelfavetta.queuesystem.service;

import com.rafaelfavetta.queuesystem.domain.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class QueueService {

    private final PriorityQueue<Patient> queue;
    private long arrivalCounter;

    public QueueService() {
        this.queue = new PriorityQueue<>();
        this.arrivalCounter = 0;
    }

    public void addPatient(Patient patient) {
        patient.setArrivalOrder(arrivalCounter++);
        queue.add(patient);
    }

    public Patient callNextPatient() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void showQueue() {
        queue.stream()
                .sorted((p1, p2) -> {
                    int priorityComparison = Integer.compare(p2.getPriorityLevel().getLevel(), p1.getPriorityLevel().getLevel());
                    if (priorityComparison != 0) {
                        return priorityComparison;
                    }
                    return Long.compare(p1.getArrivalOrder(), p2.getArrivalOrder());
                })
                .forEach(System.out::println);
    }

    public List<Patient> getSnapshotQueue() {
        return queue.stream()
                .sorted()
                .toList();
    }
}

